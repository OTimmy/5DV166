/*
 * globals.c
 * Written by Joakim Sandman, September 2015.
 * Last update: 8/10-15.
 * Lab 1: Chattserver, Datakommunikation och datornät HT15.
 *
 * globals.c contains global variables and functions for using them.
 */

/* --- Standard headers --- */
#include <stdlib.h>
//#include <stdio.h>
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
//#include <sys/socket.h>
//#include <netinet/in.h>
#include <arpa/inet.h>
//#include <endian.h>
//#include <netdb.h>
/* --- Functions --- */
//#include <stdarg.h>

/* --- Local headers --- */
#include "globals.h"
#include "queue.h"

pdu_data quit_pdu = {4, {QUIT_OP, 0, 0, 0}};

/* Number of clients currently connected to the server */
uint8_t nrof_clients = 0;
pthread_mutex_t nrof_clients_mutex = PTHREAD_MUTEX_INITIALIZER;

/* Array of connected clients (dynamic list not necessary since max 255) */
client *clients[255]; /* Protocol limits number of clients to 255. */
pthread_mutex_t clients_mutex = PTHREAD_MUTEX_INITIALIZER;

uint8_t get_nrof_clients()
{
    pthread_mutex_lock(&nrof_clients_mutex);
    uint8_t num = nrof_clients;
    pthread_mutex_unlock(&nrof_clients_mutex);
    return num;
}

void incr_nrof_clients()
{
    pthread_mutex_lock(&nrof_clients_mutex);
    nrof_clients++;
    pthread_mutex_unlock(&nrof_clients_mutex);
    return;
}

void decr_nrof_clients()
{
    pthread_mutex_lock(&nrof_clients_mutex);
    nrof_clients--;
    pthread_mutex_unlock(&nrof_clients_mutex);
    return;
}

void enqueue(client *cli, pdu_data *pdu)
{
    pthread_mutex_lock(&cli->queue_mutex);
    queue_enqueue(cli->send_queue, pdu);
    pthread_mutex_unlock(&cli->queue_mutex);
    pthread_cond_signal(&cli->queue_cond);
    return;
}

pdu_data *dequeue(client *cli)
{
    pdu_data *data = NULL;
    pthread_mutex_lock(&cli->queue_mutex);
    if (queue_isEmpty(cli->send_queue))
    {
        pthread_cond_wait(&cli->queue_cond, &cli->queue_mutex);
    }
    if (!queue_isEmpty(cli->send_queue))
    {
        data = queue_front(cli->send_queue);
        queue_dequeue(cli->send_queue);
    }
    pthread_mutex_unlock(&cli->queue_mutex);
    return data;
}

int add_client(client *cli)
{
    int fail = 1;
    pthread_mutex_lock(&clients_mutex);
    if (get_nrof_clients() >= 255)
    {
        pthread_mutex_unlock(&clients_mutex);
        return fail;
    }

    size_t nick_len = strlen(cli->nick);
    size_t pad = pad_length(nick_len);
    uint8_t ujoin[8 + nick_len + pad];
    memset(ujoin, 0, sizeof(ujoin));
    ujoin[0] = UJOIN_OP;
    ujoin[1] = nick_len;

    uint32_t unix_time = htonl(time(NULL));
    size_t i = 4;
    memcpy(&ujoin[i], &unix_time, sizeof(unix_time));
    i += sizeof(unix_time);
    memcpy(&ujoin[i], cli->nick, nick_len);

    for (int i = 0; i < 255; i++)
    {
        if (NULL != clients[i])
        {
            uint8_t *ujoin_copy = malloc(sizeof(ujoin));
            memcpy(ujoin_copy, ujoin, sizeof(ujoin));
            pdu_data *ujoin_pdu = malloc(sizeof(pdu_data));
            ujoin_pdu->len = sizeof(ujoin);
            ujoin_pdu->pdu = ujoin_copy;
            enqueue(clients[i], ujoin_pdu);

/*            uint8_t ujoin_copy[sizeof(ujoin)];*/
/*            memcpy(ujoin_copy, ujoin, sizeof(ujoin));*/
/*            pdu_data ujoin_pdu = {sizeof(ujoin), ujoin_copy};*/
/*            enqueue(clients[i], &ujoin_pdu);*/
        }
        else if (1 == fail)
        {
            clients[i] = cli;
            incr_nrof_clients();
            fail = 0;
        }
    }
    pthread_mutex_unlock(&clients_mutex);
    return fail;
}

//remove_client

