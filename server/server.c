/*
 * server.c
 * Written by Joakim Sandman, September 2015.
 * Last update: 28/9-15.
 * Lab 1: Chattserver, Datakommunikation och datornät HT15.
 *
 * server.c implements a chat server.
 */

/*
 * Compile: gcc -g -std=gnu99 -Wall -pedantic -pthread -o server server.c globals.c pdu.c name_server.c -lpthread
 * Memcheck: valgrind --tool=memcheck --leak-check=yes --show-reachable=yes -v ./server
 * Run: ./server "Server name here!"
 */

//                                                          with timeout
// uchnick to changer too? 65507 buffer size udp. non blocking recv for ack?
// existing nick change or deny? wrong PDU quit, message allowed? c tests?
// illasinnade klienter? PDU validering? "händelser" samma ordn? checksum?
// pad func. toByteArray for each PDU struct. how error safe? user friendly?
// notice failing clients.

// yes, yes, select timeout.
// deny, yes, kinda.
// ??, kinda, guess, soon (tm).
// quite safe (don't allways quit), naah.
// yeah.

// uchnick, freeall?
// main 2 threads (or admin client), ack to alive func, nrof_clients getter.

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
#include <sys/types.h>
//#include <limits.h>
//#include <stddef.h>
//#include <ctype.h> /* E.g. isalnum(), tolower() */
/* --- System calls --- */
//#include <unistd.h>
//#include <errno.h>
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
#include <sys/socket.h>
#include <netinet/in.h>
//#include <arpa/inet.h>
//#include <endian.h>
#include <netdb.h>
/* --- Functions --- */
//#include <stdarg.h>

/* --- Local headers --- */
#include "globals.h"
#include "pdu.h"
#include "server.h"
#include "name_server.h"

/* Port where this server accepts client connections */
int client_conn_port = 51515; // func to search up. if bind fails.

//struct client clients[255]; // Dynamic list not necessary since protocol
// limits number of clients to 255.
//message_queue;

/*
 * main: Runs a chat server. It registers at a name server where clients can
 *      find it and then waits for clients to connect. It then relays messages
 *      received from clients back to all clients.
 * Params: (from command line) [server name].
 * Returns: EXIT_FAILURE if error occurred, otherwise EXIT_SUCCESS.
 * Notes:
 */
int main(int argc, char *argv[])
{
    struct timespec start_time, end_time;
    clock_gettime(CLOCK_MONOTONIC_RAW, &start_time); /* Start timer */

    double proc_runtime = 0;
    clock_t proc_start_time, proc_end_time;
    proc_start_time = clock(); /* Start process timer */

    /* Initialize variables (could be extended to be done dynamically) */
    char name_server_address[] = "itchy.cs.umu.se";
    char name_server_port[] = "1337";
    char *name;
    if (1 < argc)
    {
        name = argv[1];
    }
    else
    {
        //name = "Anti-SkyNet";
        name = "Joshua - \"The only winning move is not to play\"";
        //name = "Transhumanism (H+)";
        //name = "Epistemological Cyberneticist";
    }
    //init
    //find portno
    //listen
    /* Create thread for communication with name server */
    pdu_reg reg = {REG_OP, strlen(name), client_conn_port, name};
    reg_data thread_data_ns = {name_server_address, name_server_port, reg};
    pthread_t thread_ns;
    if (0 != pthread_create(&thread_ns, NULL, register_at_name_server,
                            (void *) &thread_data_ns))
    {
        fprintf(stderr, "ERROR: Failed to create thread ns!\n");
        exit(EXIT_FAILURE);
    }
    //register_at_name_server(thread_data_ns);

/*    int sockfd, newsockfd, portno, clilen, n;*/
/*    char buffer[256];*/
/*    struct sockaddr_in serv_addr, cli_addr;*/
/*    struct hostent *server;*/
/*    if (argc < 2) {*/
/*        fprintf(stderr,"ERROR, no port provided");*/
/*        exit(1);*/
/*    }*/
/*    sockfd = socket(AF_INET, SOCK_STREAM, 0);*/
/*    if (sockfd < 0)*/
/*        error("ERROR opening socket");*/
/*    */
/*    bzero((char *) &serv_addr, sizeof(serv_addr));*/
/*    portno = atoi(argv[1]);*/
/*    serv_addr.sin_family = AF_INET;*/
/*    serv_addr.sin_port = htons(portno);*/
/*    serv_addr.sin_addr.s_addr = INADDR_ANY;*/
/*    */
/*    if (bind(sockfd, (struct sockaddr *) &serv_addr, sizeof(serv_addr)) < 0)*/
/*        error("ERROR on binding");*/
/*    listen(sockfd,5);*/
/*    clilen = sizeof(cli_addr);*/
/*    newsockfd = accept(sockfd, (struct sockaddr *) &cli_addr, &clilen);*/
/*    if (newsockfd < 0)*/
/*        error("ERROR on accept");*/
/*    */
/*    bzero(buffer,256);*/
/*    n = read(newsockfd,buffer,255);*/
/*    if (n < 0) error("ERROR reading from socket");*/
/*    printf("Here is the message: %s",buffer);*/
/*    n = write(newsockfd,"I got your message",18);*/
/*    if (n < 0) error("ERROR writing to socket");*/

    if (0 != pthread_join(thread_ns, NULL))
    {
        fprintf(stderr, "ERROR: Failed to join with thread ns!\n");
    }

    proc_end_time = clock(); /* End process timer */
    proc_runtime = (double) (proc_end_time - proc_start_time)/CLOCKS_PER_SEC;
    fprintf(stderr, "\nProcess runtime: %.2f sec.\n", proc_runtime);

    clock_gettime(CLOCK_MONOTONIC_RAW, &end_time); /* End timer */
    double runtime = (end_time.tv_sec - start_time.tv_sec)
                     + (end_time.tv_nsec - start_time.tv_nsec)/1000000000.0;
    fprintf(stderr, "\nRuntime: %.2f sec.\n", runtime);

    //getchar();
    return 0;
}

/* ===== WORK IN PROGRESS ===== */
/*
 * fatal_error: Exits the program (with unsuccessful exit status) after
 *      printing some error messages.
 * Params: pmsg = string to print with perror, denoting what failed.
 *         msg = string to print to stderr, describing the issue. E.g.
 *               "Usage: %s [-t type] start1 [start2 ...] name\n". (%s=argv[0])
 * Returns:
 * Notes: Frees dynamically allocated resources given function.
 */
/*void fatal_error(char *pmsg, char *msg)*/
/*{*/
/*    fprintf(stderr, "\nWarning: ERROR occurred, couldn't finish program!\n");*/
/*    fprintf(stderr, msg);*/
/*    perror(pmsg);*/
/*    //free_all(); // conditional to preprocessor directive (as timers?)*/
/*    exit(EXIT_FAILURE);*/
/*}*/

/*void error(char *msg)*/
/*{*/
/*    perror(msg);*/
/*    //free_all();*/
/*    exit(EXIT_FAILURE);*/
/*}*/

