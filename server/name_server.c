/*
 * name_server.c
 * Written by Joakim Sandman, September 2015.
 * Last update: 23/9-15.
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
//#include <fcntl.h> /* File control */
//#include <sys/stat.h> /* Stat function */
/* --- Signals and threads --- */
//#include <signal.h>
//#include <setjmp.h>
//#include <pthread.h> /* -lpthread */
/* --- Sockets --- */
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
/* --- Functions --- */
//#include <stdarg.h>

/* --- Local headers --- */
#include "server.h"
#include "name_server.h"

/*    n = sendto(sockfd,buffer,8, 0,NULL,0);
/*    n = recvfrom(sockfd,buffer,4, 0,NULL,NULL);*/
/*    uint16_t ID = (buffer[2] << 8) | (buffer[3] && 0xFF);*/

#define EVER (;;)
#define ACK_OP 1
#define ALIVE_OP 2

extern uint8_t nrof_clients;

size_t pad_length(size_t len)
{
    return ((4 - len % 4) % 4);
}

size_t reg_arr_size(pdu_reg reg)
{
    size_t len = sizeof(reg.op)
                + sizeof(reg.name_len)
                + sizeof(reg.tcp_port)
                + strlen(reg.name);
    len += pad_length(len);
    return len;
}

void reg_to_array(uint8_t reg_array[], pdu_reg reg, size_t array_len)
{
    size_t i = array_len;
    memcpy(&reg_array[i], &reg.op, sizeof(reg.op));
    i -= sizeof(reg.op);
    memcpy(&reg_array[i], &reg.name_len, sizeof(reg.name_len));
    i -= sizeof(reg.name_len);
    memcpy(&reg_array[i], &reg.tcp_port, sizeof(reg.tcp_port));
    i -= sizeof(reg.tcp_port);
    memcpy(&reg_array[i], reg.name, strlen(reg.name));
    i -= strlen(reg.name);
    memset(&reg_array[i], 0, i);
    return;
}

/*
 * register_at_name_server: Registers at the given name server with the given
 *      info and periodically updates the name server with the number of
 *      connected clients through heartbeats.
 * Params: ns_name = string with the name of the name server.
 *         ns_port = string representing the port number where the name server
 *                   accepts connections.
 *         reg = pdu_reg struct containing this servers name and open port nr.
 * Returns:
 * Notes:
 */
void register_at_name_server(char *ns_name, char *ns_port, pdu_reg reg)
{
    int err;
    int sockfd = connect_to_name_server(ns_name, ns_port);
    uint8_t buffer[4] = {0}; /* Same size for ACK, ALIVE and NOTREG */

    /* Convert pdu_reg to byte array */
    size_t reg_arr_len = reg_arr_size(reg);
    uint8_t reg_array[reg_arr_len];
    reg_to_array(reg_array, reg, reg_arr_len);
    //for (int i =0; i<16; i++)
    //{
    //    printf("%c\n", reg_array[i]);
    //}
    //fflush(stdout);

    /* Register at name server */
    for EVER // break if some global var set? if some signal sent?
    {
        err = send(sockfd, reg_array, reg_arr_len, 0);
        if (err < 0)
        {
            perror("send (reg)");
            exit(EXIT_FAILURE);
        }
        //select non block? 6sec
        err = recv(sockfd, buffer, sizeof(buffer), 0); // MSG_DONTWAIT);
        if (err < 0)
        {
            if (EAGAIN == errno || EWOULDBLOCK == errno) /* Timed out */
            {
                continue; /* No additional sleep */
            }
            else
            {
                perror("recv (reg)");
                exit(EXIT_FAILURE);
            }
        }
        sleep(6);

        /* Stay alive and update number of connected clients */
        while (ACK_OP == buffer[0]) /* Otherwise a NOTREG was received */
        {
            buffer[0] = ALIVE_OP;
            buffer[1] = nrof_clients;
            err = send(sockfd, buffer, sizeof(buffer), 0);
            if (err < 0)
            {
                perror("send (alive)");
                exit(EXIT_FAILURE);
            }
            //select non block? 6sec
            err = recv(sockfd, buffer, sizeof(buffer), 0); // MSG_DONTWAIT);
            if (err < 0)
            {
                if (EAGAIN == errno || EWOULDBLOCK == errno) /* Timed out */
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

    return;
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
    hints.ai_family = AF_UNSPEC;        /* Allow IPv4 or IPv6 */
    hints.ai_socktype = SOCK_DGRAM;     /* Datagram socket */
    hints.ai_protocol = IPPROTO_UDP;    /* UDP protocol */
    err = getaddrinfo(ns_name, ns_port, &hints, &result);
    if (0 != err)
    {
        fprintf(stderr,"getaddrinfo: %s\n", gai_strerror(err));
        exit(EXIT_FAILURE);
    }

    /* Loop through the results until an address can be connected to */
    for (res = result; NULL != res; res = res->ai_next)
    {
        sockfd = socket(res->ai_family, res->ai_socktype, res->ai_protocol);
        if (sockfd < 0)
        {
            perror("socket");
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

