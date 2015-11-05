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
#include <stdio.h>
#include <time.h> /* -lrt (sometimes for glibc < 2.17) */
//#include <math.h> /* -lm */
#include <string.h>
//#include <sys/wait.h>
/* --- Data types --- */
#include <stdbool.h>
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

uint8_t quit[] = {QUIT_OP, 0, 0, 0};
pdu_data quit_pdu = {4, quit};

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

bool add_client(client *cli)
{
    bool added = false;
    pthread_mutex_lock(&clients_mutex);
    if (get_nrof_clients() >= 255)
    {
        pthread_mutex_unlock(&clients_mutex);
        return added;
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
        }
        else if (!added)
        {
            clients[i] = cli;
            incr_nrof_clients();
            added = true;
        }
    }
    pthread_mutex_unlock(&clients_mutex);
    return added;
}

void remove_client(client *cli)
{
    pthread_mutex_lock(&clients_mutex);//mutex lower!!!!

    size_t nick_len = strlen(cli->nick);
    size_t pad = pad_length(nick_len);
    uint8_t uleave[8 + nick_len + pad];
    memset(uleave, 0, sizeof(uleave));
    uleave[0] = ULEAVE_OP;
    uleave[1] = nick_len;

    uint32_t unix_time = htonl(time(NULL));
    size_t i = 4;
    memcpy(&uleave[i], &unix_time, sizeof(unix_time));
    i += sizeof(unix_time);
    memcpy(&uleave[i], cli->nick, nick_len);

    for (int i = 0; i < 255; i++)
    {
        if (cli == clients[i])
        {
            clients[i] = NULL;
            decr_nrof_clients();
            //enqueue(cli, &quit_pdu);
        }
        else if (NULL != clients[i])
        {
            uint8_t *uleave_copy = malloc(sizeof(uleave));
            memcpy(uleave_copy, uleave, sizeof(uleave));
            pdu_data *uleave_pdu = malloc(sizeof(pdu_data));
            uleave_pdu->len = sizeof(uleave);
            uleave_pdu->pdu = uleave_copy;
            enqueue(clients[i], uleave_pdu);
        }
    }
    pthread_mutex_unlock(&clients_mutex);
    return;
}

bool nick_used(char *nick)
{
    bool used = false;
    pthread_mutex_lock(&clients_mutex);
    for (int i = 0; i < 255; i++)
    {
        if (NULL != clients[i])
        {
            if (!strcmp(nick, clients[i]->nick))
            {
                used = true;
                break;
            }
        }
    }
    pthread_mutex_unlock(&clients_mutex);
    return used;
}

pdu_data *get_nicks_pdu(client *cli)
{
    pthread_mutex_lock(&clients_mutex);

    int nrof_nicks = 0;
    int nicks_len = 0;
    char *nick_strings[255] = {0};
    for (int i = 0; i < 255; i++)
    {
        if (NULL != clients[i])
        {
            nick_strings[nrof_nicks] = clients[i]->nick;
            nicks_len += strlen(clients[i]->nick) + 1;
            nrof_nicks++;
        }
    }
    nick_strings[nrof_nicks] = cli->nick;
    nicks_len += strlen(cli->nick) + 1;
    nrof_nicks++;

    size_t pad = pad_length(nicks_len);
    size_t nicks_size = 4 + nicks_len + pad;
    uint8_t *nicks = malloc(nicks_size);
    memset(nicks, 0, nicks_size);
    nicks[0] = NICKS_OP;
    nicks[1] = nrof_nicks;
    uint16_t nicks_len_nbo = htons(nicks_len);
    memcpy(&nicks[2], &nicks_len_nbo, sizeof(nicks_len_nbo));
    int len = 0;
    int ind = 4;
    for (int i = 0; i < nrof_nicks; i++)
    {
        len = snprintf((char *) &nicks[ind], nicks_len, "%s", nick_strings[i]);
        if (len < 0)
        {
            perror("snprintf (nicks)");
            free(nicks);
            pthread_mutex_unlock(&clients_mutex);
            return NULL;
        }
        ind += len;
        ind++;
    }

    pdu_data *nicks_pdu = malloc(sizeof(pdu_data));
    nicks_pdu->len = nicks_size;
    nicks_pdu->pdu = nicks;

/*    for (int i = 0; i < 255; i++)*/
/*    {*/
/*        if (NULL != clients[i])*/
/*        {*/
/*            enqueue(clients[i], nicks_pdu);*/
/*        }*/
/*    }*/
    pthread_mutex_unlock(&clients_mutex);
    return nicks_pdu;
}

//one each
pdu_data *server_mess(char *msg)
{
    size_t mess_len = strlen(msg);
    size_t pad = pad_length(mess_len);
    size_t mess_size = 12 + mess_len + pad;
    uint8_t *mess = malloc(mess_size);
    memset(mess, 0, mess_size);
    mess[0] = MESS_OP;

    uint16_t mess_len_nbo = htons(mess_len);
    memcpy(&mess[4], &mess_len_nbo, sizeof(mess_len_nbo));
    uint32_t unix_time = htonl(time(NULL));
    memcpy(&mess[8], &unix_time, sizeof(unix_time));
    memcpy(&mess[12], msg, mess_len);
    mess[3] = get_checksum(mess, mess_size);

    pdu_data *mess_pdu = malloc(sizeof(pdu_data));
    mess_pdu->len = mess_size;
    mess_pdu->pdu = mess;
    return mess_pdu;
}

//enqueue_copy()
//kick cli/all???

