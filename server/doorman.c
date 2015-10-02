/*
 * doorman.c
 * Written by Joakim Sandman, October 2015.
 * Last update: 2/10-15.
 * Lab 1: Chattserver, Datakommunikation och datornät HT15.
 *
 * doorman.c contains functions for accepting connections from clients.
 */

/* --- Standard headers --- */
#include <stdlib.h>
#include <stdio.h>
//#include <time.h> /* -lrt (sometimes for glibc < 2.17) */
//#include <math.h> /* -lm */
#include <string.h>
//#include <sys/wait.h>
/* --- Data types --- */
//#include <stdbool.h>
//#include <stdint.h> /* Subset of inttypes.h */
//#include <inttypes.h> /* Fixed width integers */
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
//#include <sys/select.h>
/* --- Signals and threads --- */
//#include <signal.h>
//#include <setjmp.h>
//#include <pthread.h> /* -pthread &or -lpthread */
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
#include "doorman.h"

/*
 * register_at_name_server: Registers at the given name server with the given
 *      info and periodically updates the name server with the number of
 *      connected clients through heartbeats.
 * Params: thread_data_dm = reg_data pointer containing the following values.
 *      ns_name = string with the name of the name server.
 *      ns_port = string representing the port number where the name server
 *                accepts connections.
 *      reg = pdu_reg struct containing this servers name and open port nr.
 * Returns: NULL.
 * Notes:
 */
void *handle_connecting_clients(void *thread_data_dm)
{
    /* Extract thread data */
    char *client_conn_port = (char *) thread_data_dm;

    //int err;
    int clifd;
    int listener = bind_client_conn_port(client_conn_port);
    struct sockaddr_storage client_addr;
    socklen_t client_addr_len = sizeof(client_addr);
    client *new_client;

    if (listen(listener, 17) < 0) /* Listen for connections to port */
    {
        perror("listen");
        exit(EXIT_FAILURE);
    }

    for EVER
    {
        /* Wait for a connection on the client connection socket (listener) */
        clifd = accept(listener, (struct sockaddr *)&client_addr,
                       &client_addr_len);
        if (clifd < 0)
        {
            perror("accept");
        }
        else
        {
            new_client = malloc(sizeof(client));
            if (NULL == new_client)
            {
                perror("malloc (new cli)");
            }
            new_client->sockfd = clifd;
            new_client->address = client_addr;//??????
            /* Create thread for processing client input */
            //new thread stuff (will read and init nick, mutex and queue -> output thread?)
/*            pthread_t thread_cli;*/
/*            if (0 != pthread_create(&thread_cli, &attr, handle_connecting_clients,*/
/*                                    (void *) new_client))*/
/*            {*/
/*                fprintf(stderr, "ERROR: Failed to create thread cli!\n");*/
/*                exit(EXIT_FAILURE);*/
/*            }*/
        }
    }
    close(listener);
    return NULL;
}

/*
 * bind_client_conn_port: Creates a socket where client connections are to be
 *      accepted and binds it to the given port.
 * Params: conn_port = string representing the port number where the server
 *                     accepts connections.
 * Returns: The socket file descriptor associated with the port.
 * Notes: When finished using it, the socket should be closed to free the
 *      file descriptor.
 */
int bind_client_conn_port(char *conn_port)
{
    int sockfd;
    int err;
    int yes = 1; /* For setsockopt, SO_REUSEADDR */
    struct addrinfo hints;
    struct addrinfo *result, *res;

    /* Get addresses corresponding to the local host and the given port */
    memset(&hints, 0, sizeof(struct addrinfo));
    hints.ai_family = AF_UNSPEC;        /* Allow IPv4 or IPv6 */
    hints.ai_socktype = SOCK_STREAM;    /* Stream socket */
    hints.ai_protocol = 0;              /* Any protocol (expecting TCP) */
    hints.ai_flags = AI_PASSIVE | AI_ADDRCONFIG; /* For wildcard IP address */
    err = getaddrinfo(NULL, conn_port, &hints, &result);
    if (0 != err)
    {
        fprintf(stderr,"getaddrinfo (bind): %s\n", gai_strerror(err));
        exit(EXIT_FAILURE);
    }

    /* Loop through the results until an address can be bound to */
    for (res = result; NULL != res; res = res->ai_next)
    {
        sockfd = socket(res->ai_family, res->ai_socktype, res->ai_protocol);
        if (sockfd < 0)
        {
            perror("socket (bind)");
            continue;
        }
        setsockopt(sockfd, SOL_SOCKET, SO_REUSEADDR, &yes, sizeof(yes));
        if (bind(sockfd, res->ai_addr, res->ai_addrlen) < 0)
        {
            close(sockfd);
            perror("bind");
            continue;
        }
        break; /* Bind successful */
    }

    if (NULL == res) /* No bind successful */
    {
        fprintf(stderr, "ERROR: Could not bind to port!\n");
        exit(EXIT_FAILURE);
    }
    freeaddrinfo(result);
    return sockfd;
}

