/*
 * server.c
 * Written by Joakim Sandman, September 2015.
 * Last update: 6/11-15.
 * Lab 1: Chattserver, Datakommunikation och datornät HT15.
 *
 * server.c implements a chat server.
 */

/*
 * Compile: gcc -g -std=gnu99 -Wall -pedantic -pthread -o server server.c globals.c pdu.c name_server.c doorman.c clients.c queue.c -lpthread
 * Memcheck: valgrind --tool=memcheck --leak-check=yes --show-reachable=yes -v ./server
 * Run: ./server "Server name here!" 51515
 */

/* --- Standard headers --- */
#include <stdlib.h>
#include <stdio.h>
#include <time.h> /* -lrt (sometimes for glibc < 2.17) */
//#include <math.h> /* -lm */
#include <string.h>
//#include <sys/wait.h>
/* --- Data types --- */
//#include <stdbool.h>
//#include <stdint.h> /* Subset of inttypes.h */
#include <inttypes.h> /* Fixed width integers */
//#include <sys/types.h>
//#include <limits.h>
//#include <stddef.h>
//#include <ctype.h> /* E.g. isalnum(), tolower() */
/* --- System calls --- */
#include <unistd.h>
#include <errno.h>
//#include <dirent.h>
//#include <sys/param.h> /* E.g. MAXPATHLEN for getcwd() */
//#include <fcntl.h> /* File control (including sockets) */
//#include <sys/stat.h> /* Stat function */
//#include <sys/select.h>
/* --- Signals and threads --- */
//#include <signal.h>
//#include <setjmp.h>
#include <pthread.h> /* -pthread &or -lpthread */
/* --- Sockets --- */
//#include <sys/socket.h>
//#include <netinet/in.h>
//#include <arpa/inet.h>
//#include <endian.h>
//#include <netdb.h>
/* --- Functions --- */
//#include <stdarg.h>

/* --- Local headers --- */
#include "globals.h"
#include "pdu.h"
#include "server.h"
#include "doorman.h"
#include "name_server.h"

/* --- Static global variables --- */
/* Address of the name server */
static char *name_server_address = "itchy.cs.umu.se";
/* Port where name server accepts server connections */
static char *name_server_port = "1337";
/* Port where this server accepts client connections */
static char *client_conn_port = "51515";
/* Server name */
static char *name = "Joshua - \"The only winning move is not to play\"";
/*static char *name = "Anti-SkyNet";*/
/*static char *name = "Transhumanism (H+)";*/
/*static char *name = "Epistemological Cyberneticist";*/
/*static char *name = "Þursa-smiðja";*/
/*static char *name = "¬(µæłø ∨ (Ω»¦«¥⅝²ß)) ∧ ©";*/

/*
 * main: Runs a chat server. It registers at a name server where clients can
 *      find it and then waits for clients to connect. It then relays messages
 *      received from clients back to all clients.
 * Params: (from command line) [-a name server address] [-p name server port]
           [server name [client connection port]].
 * Returns: EXIT_FAILURE if error occurred, otherwise EXIT_SUCCESS.
 * Notes:
 */
int main(int argc, char *argv[])
{
    struct timespec start_time, end_time;
    clock_gettime(CLOCK_MONOTONIC_RAW, &start_time); /* Start timer */

    parse_arguments(argc, argv);

    /* Create thread for handling new client connections (doorman) */
    pthread_t thread_dm;
    if (0 != pthread_create(&thread_dm, NULL, handle_connecting_clients,
                            (void *) client_conn_port))
    {
        fprintf(stderr, "ERROR: Failed to create thread dm!\n");
        exit(EXIT_FAILURE);
    }

    /* Create thread for communication with name server */
    uint16_t ccp = strtoul(client_conn_port, NULL, 10);
    pdu_reg reg = {REG_OP, strlen(name), ccp, name};
    reg_data thread_data_ns = {name_server_address, name_server_port, reg};
    pthread_t thread_ns;
    if (0 != pthread_create(&thread_ns, NULL, register_at_name_server,
                            (void *) &thread_data_ns))
    {
        fprintf(stderr, "ERROR: Failed to create thread ns!\n");
        exit(EXIT_FAILURE);
    }

    /* Wait for inputs and follow commands */
    char cmdline[20];
    char cmd[20];
    int exit_flag = 0;
    fflush(stdin);
    while (NULL != fgets(cmdline, sizeof(cmdline), stdin))
    {
        sscanf(cmdline, "%s", cmd);
        fflush(stdin);
        if (!strcmp(cmd, "exit")) /* Exit the program */
        {
            /* Cancel major threads */
            pthread_cancel(thread_dm);
            pthread_cancel(thread_ns);
            if (0 != pthread_join(thread_dm, NULL))
            {
                fprintf(stderr, "ERROR: Failed to join with thread dm!\n");
            }
            if (0 != pthread_join(thread_ns, NULL)) 
            {
                fprintf(stderr, "ERROR: Failed to join with thread ns!\n");
            }
            /* Tell clients we are closing and request they remove themselves */
            pthread_mutex_lock(&clients_mutex);
            for (int i = 0; i < 255; i++)
            {
                if (NULL != clients[i])
                {
                    enqueue(clients[i], server_mess("-Server shutting down!-"));
                    shutdown(clients[i]->sockfd, SHUT_RD); /* Stop input */
                }
            }
            pthread_mutex_unlock(&clients_mutex);
            sleep(2); /* Give clients time to exit gracefully */
            exit_flag = 1;
            break;
        }
        else if (!strcmp(cmd, "up")) /* Print server uptime */
        {
            struct timespec curr_time;
            clock_gettime(CLOCK_MONOTONIC_RAW, &curr_time);
            uint64_t uptime = curr_time.tv_sec - start_time.tv_sec;
            fprintf(stderr, "\nUptime: %"PRIu64" days %02"PRIu64
                    ":%02"PRIu64":%02"PRIu64"\n", uptime/86400,
                    (uptime%86400)/3600, (uptime%3600)/60, uptime%60);
        }
        /* Feature bloat here! Could for example add commands for kicking
           client, spying on conversation, mini client, etc. */
    }
    if (!exit_flag)
    {
        fprintf(stderr, "Server commands unavailable!\n");
        pause(); /* Until program manually terminated */
    }

    clock_gettime(CLOCK_MONOTONIC_RAW, &end_time); /* End timer */
    double runtime = (end_time.tv_sec - start_time.tv_sec)
                     + (end_time.tv_nsec - start_time.tv_nsec)/1000000000.0;
    fprintf(stderr, "\nRuntime: %.2f sec.\n", runtime);

    return 0;
}

/*
 * parse_arguments: Parse the input arguments, including the options
 *      (preceded by -) and set the corresponding values.
 * Params: argc = the number of arguments in argv.
 *         argv = array of strings from command line input.
 * Returns:
 * Notes: Can alter the value of this files global variables.
 */
void parse_arguments(int argc, char *argv[])
{
    int opt = 0;
    while (-1 != (opt = getopt(argc, argv, "a:p:")))
    {
        switch (opt)
        {
        case 'a':
            name_server_address = optarg;
            break;
        case 'p':
            name_server_port = optarg;
            if (!is_uint16(name_server_port)) /* Validate port number */
            {
                fprintf(stderr, "Warning: %s is not a valid port!\n",
                        name_server_port);
                exit(EXIT_FAILURE);
            }
            break;
        default:
            fprintf(stderr,
                    "Usage: %s [-a name server address] [-p name server port]"
                    " [server name [client connection port]]\n", argv[0]);
            exit(EXIT_FAILURE);
        }
    }
    if (optind < (argc)) /* There are more arguments in argv */
    {
        name = argv[optind];
        if (255 < strlen(name)) /* Check server name length */
        {
            fprintf(stderr, "Warning: server name longer than 255 bytes!\n");
            exit(EXIT_FAILURE);
        }
        if (optind + 1 < (argc)) /* There is another argument in argv */
        {
            client_conn_port = argv[optind + 1];
            if (!is_uint16(client_conn_port)) /* Validate port number */
            {
                fprintf(stderr, "Warning: %s is not a valid port!\n",
                        client_conn_port);
                exit(EXIT_FAILURE);
            }
        }
    }
    return;
}

/*
 * is_uint16: Determines if the string represents an integer in [0, 65535].
 * Params: str = string to evaluate.
 * Returns: TRUE if the string represents an uint16, FALSE otherwise.
 * Notes:
 */
int is_uint16(const char *str)
{
    errno = 0;
    char *end = NULL;
    long val = strtol(str, &end, 10);
    if (errno || str == end || '\0' != *end || val < 0 || val > UINT16_MAX)
    {
        return 0;
    }
    return 1;
}

