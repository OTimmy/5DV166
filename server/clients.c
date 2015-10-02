/*
 * clients.c
 * Written by Joakim Sandman, October 2015.
 * Last update: 2/10-15.
 * Lab 1: Chattserver, Datakommunikation och datornät HT15.
 *
 * clients.c contains functions for handling client input and output.
 */

/* --- Standard headers --- */
#include <stdlib.h>
#include <stdio.h>
//#include <time.h> /* -lrt (sometimes for glibc < 2.17) */
//#include <math.h> /* -lm */
//#include <string.h>
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
#include "queue.h"
#include "clients.h"

/*
 * register_at_name_server: Registers at the given name server with the given
 *      info and periodically updates the name server with the number of
 *      connected clients through heartbeats.
 * Params: thread_data_cli = reg_data pointer containing the following values.
 *      ns_name = string with the name of the name server.
 *      ns_port = string representing the port number where the name server
 *                accepts connections.
 *      reg = pdu_reg struct containing this servers name and open port nr.
 * Returns: NULL.
 * Notes:
 */
void *init_new_client(void *thread_data_cli)
{
    /* Extract thread data */
    client *cli = (client *) thread_data_cli;

    /* Initialize thread attribute to detached */
    pthread_attr_t attr;
    pthread_attr_init(&attr);
    pthread_attr_setdetachstate(&attr, PTHREAD_CREATE_DETACHED);

    /* Initialize client structure data */
    pthread_mutex_init(&cli->queue_mutex, NULL);
    cli->send_queue = queue_empty();
    // free func???
    // create out thread (cli)

    int err;
    uint8_t header[4] = {0}; /* JOIN PDU header */

    /* Setup select() to monitor the client socket, with a timeout */
    struct timeval timeout = {3, 0};
    struct timeval count_down;
    fd_set changed_fds;
    fd_set read_fds;
    FD_ZERO(&read_fds);
    FD_SET(cli->sockfd, &read_fds);

    /* Wait for JOIN PDU and close connection if too much time passes */
    for (int i = 0; i < 3; i++) /* Allow limited number of faulty packages */
    {
        count_down = timeout; /* Reset timeout period for monitoring */
        changed_fds = read_fds; /* Reset file descriptors to monitor */
        if (select(cli->sockfd + 1, &changed_fds, NULL, NULL, &count_down) < 0)
        {
            perror("select (join)");
            continue;
        }
        else /* FD_ISSET(cli->sockfd, &read_fds) */
        {
            err = recv(cli->sockfd, header, sizeof(header), MSG_DONTWAIT);
            if (err < 0)
            {
                /* select() timed out and no JOIN was received */
                if (EAGAIN == errno || EWOULDBLOCK == errno)
                {
                    break; /* Terminate client connection */
                }
                else
                {
                    perror("recv (join)");
                    continue;
                }
            }
            // check header
            if (JOIN_OP == header[0])
            {
                int nick_len = header[1] + 1;
                cli->nick = malloc(sizeof(nick_len));
                if (NULL == cli->nick)
                {
                    perror("malloc (nick)");
                    break; /* Terminate client connection */
                }
                size_t pad = pad_length(nick_len);
                uint8_t buf[nick_len+pad]; /* Client nickname buffer */
                // nul term???????
                err = recv(cli->sockfd, buf, sizeof(buf), MSG_DONTWAIT);
                if (err < 0)
                {
                    perror("recv (nick)");
                    break; /* Terminate client connection */
                }
                err = snprintf(cli->nick, nick_len, "%s", buf);
                if (err < 0 || err >= nick_len)
                {
                    perror("snprintf (nick)");
                    break; /* Terminate client connection */
                }
                uint8_t nicks[] = {NICKS_OP, 1, 0, 4, 'S', 'i', 'r', '\0'};
                // input func -> sign up on list
                send(cli->sockfd, nicks, 8, 0);//select???????
                for EVER {}
            }
            else /* Not a JOIN PDU */
            {
                //return quit????
                break; /* Terminate client connection */
            }
        }
    }

    /* Free all thread resources, exit thread and close the connection */
    pthread_attr_destroy(&attr);
    close(cli->sockfd);
    queue_free(cli->send_queue);
    pthread_mutex_destroy(&cli->queue_mutex);
    free(cli);
    return NULL;
}

