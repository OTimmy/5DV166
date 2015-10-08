/*
 * name_server.c
 * Written by Joakim Sandman, September 2015.
 * Last update: 8/10-15.
 * Lab 1: Chattserver, Datakommunikation och datornät HT15.
 *
 * name_server.c contains functions for connecting to the name server.
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
#include <sys/types.h>
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
#include <sys/select.h>
/* --- Signals and threads --- */
//#include <signal.h>
//#include <setjmp.h>
//#include <pthread.h> /* -pthread &or -lpthread */
/* --- Sockets --- */
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
//#include <endian.h>
#include <netdb.h>
/* --- Functions --- */
//#include <stdarg.h>

/* --- Local headers --- */
#include "globals.h"
#include "pdu.h"
#include "name_server.h"

/*
 * register_at_name_server: Registers at the given name server with the given
 *      info and periodically updates the name server with the number of
 *      connected clients through heartbeats.
 * Params: thread_data_ns = reg_data pointer containing the following values.
 *      ns_name = string with the name of the name server.
 *      ns_port = string representing the port number where the name server
 *                accepts connections.
 *      reg = pdu_reg struct containing this servers name and open port nr.
 * Returns: NULL.
 * Notes:
 */
void *register_at_name_server(void *thread_data_ns)
{
    /* Extract thread data */
    reg_data thread_data = *(reg_data *) thread_data_ns;
    char *ns_name = thread_data.name_server_address;
    char *ns_port = thread_data.name_server_port;
    pdu_reg reg = thread_data.reg;

    int err;
    uint8_t buffer[4] = {0}; /* Same size for ACK, ALIVE and NOTREG */
    int sockfd = connect_to_name_server(ns_name, ns_port);
    printf("Name server found!\n");

    /* Convert pdu_reg to byte array */
    size_t reg_arr_len = reg_arr_size(reg);
    uint8_t reg_array[reg_arr_len];
    reg_to_array(reg_array, reg, reg_arr_len);

    /* Setup select() to monitor the name server socket, with a timeout */
    struct timeval timeout = {6, 0};
    struct timeval count_down;
    fd_set changed_fds;
    fd_set read_fds;
    FD_ZERO(&read_fds);
    FD_SET(sockfd, &read_fds);

    /* Register at name server */
    for EVER // break if some global var set? if some signal sent?????????????
    {
        printf("Registering at name server...\n");
        err = send(sockfd, reg_array, reg_arr_len, 0);
        if (err < 0)
        {
            perror("send (reg)");
            exit(EXIT_FAILURE);
        }
        count_down = timeout; /* Reset timeout period for monitoring */
        changed_fds = read_fds; /* Reset file descriptors to monitor */
        if (select(sockfd + 1, &changed_fds, NULL, NULL, &count_down) < 0)
        {
            perror("select (reg)");
            exit(EXIT_FAILURE);
        }
        else /* FD_ISSET(sockfd, &read_fds) */
        {
            err = recv(sockfd, buffer, sizeof(buffer),
                       MSG_DONTWAIT | MSG_WAITALL);
            if (err < 0)
            {
                /* select() timed out and no ACK was received */
                if (EAGAIN == errno || EWOULDBLOCK == errno)
                {
                    continue; /* No additional sleep */
                }
                else
                {
                    perror("recv (reg)");
                    exit(EXIT_FAILURE);
                }
            }
            if (ACK_OP == buffer[0])
            {
                printf("Registration successful!       Server ID: %d\n",
                       (buffer[2] << 8) | (buffer[3] & 0xFF));
            }
            sleep(6);
        }

        /* Stay alive and update number of connected clients */
        while (ACK_OP == buffer[0]) /* Otherwise a NOTREG was received */
        {
            /* Build and send ALIVE PDU */
            buffer[0] = ALIVE_OP;
            buffer[1] = get_nrof_clients();
            err = send(sockfd, buffer, sizeof(buffer), 0);
            if (err < 0)
            {
                perror("send (alive)");
                exit(EXIT_FAILURE);
            }
            count_down = timeout; /* Reset timeout period for monitoring */
            changed_fds = read_fds; /* Reset file descriptors to monitor */
            if (select(sockfd + 1, &changed_fds, NULL, NULL, &count_down) < 0)
            {
                perror("select (alive)");
                exit(EXIT_FAILURE);
            }
            else /* FD_ISSET(sockfd, &read_fds) */
            {
                err = recv(sockfd, buffer, sizeof(buffer),
                           MSG_DONTWAIT | MSG_WAITALL);
                if (err < 0)
                {
                    /* select() timed out and no ACK was received */
                    if (EAGAIN == errno || EWOULDBLOCK == errno)
                    {
                        buffer[0] = ACK_OP; /* To send another alive pdu */
                        continue; /* No additional sleep */
                    }
                    else
                    {
                        perror("recv (alive)");
                        exit(EXIT_FAILURE);
                    }
                }
                sleep(6); /* Only called if a message was received */
            }
        }
    }
    printf("Leaving name server.\n");
    close(sockfd);
    return NULL;
}

/*
 * connect_to_name_server: Connects to the given name server and creates a
 *      socket for communication.
 * Params: ns_name = string with the name of the name server.
 *         ns_port = string representing the port number where the name server
 *                   accepts connections.
 * Returns: The socket file descriptor associated with the connection.
 * Notes: Since the created socket is UDP, there is no "real connection" to
 *      the name server which needs to be closed later. However, when finished,
 *      the socket should be closed to free the file descriptor.
 */
int connect_to_name_server(char *ns_name, char *ns_port)
{
    int sockfd;
    int err;
    struct addrinfo hints;
    struct addrinfo *result, *res;

    /* Search for addresses corresponding to the given server name and port */
    memset(&hints, 0, sizeof(struct addrinfo));
    hints.ai_family = AF_INET;      /* Allow only IPv4 */
    hints.ai_socktype = SOCK_DGRAM; /* Datagram socket */
    hints.ai_protocol = 0;          /* Any protocol (expecting UDP) */
    err = getaddrinfo(ns_name, ns_port, &hints, &result);
    if (0 != err)
    {
        fprintf(stderr,"getaddrinfo (ns): %s\n", gai_strerror(err));
        exit(EXIT_FAILURE);
    }

    /* Loop through the results until an address can be connected to */
    for (res = result; NULL != res; res = res->ai_next)
    {
        sockfd = socket(res->ai_family, res->ai_socktype, res->ai_protocol);
        if (sockfd < 0)
        {
            perror("socket (ns)");
            continue;
        }
        /* For UDP sockets connect() only sets the default address and port
           for subsequent calls to send() and recv() */
        if (connect(sockfd, res->ai_addr, res->ai_addrlen) < 0)
        {
            close(sockfd);
            perror("connect");
            continue;
        }
        break; /* Connection succeeded */
    }

    if (NULL == res) /* No connection found */
    {
        fprintf(stderr, "ERROR: No connection found!\n");
        exit(EXIT_FAILURE);
    }
    freeaddrinfo(result);
    return sockfd;
}

