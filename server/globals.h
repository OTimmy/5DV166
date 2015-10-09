/*
 * globals.h
 * Written by Joakim Sandman, September 2015.
 * Last update: 9/10-15.
 * Lab 1: Chattserver, Datakommunikation och datornät HT15.
 *
 * globals.h is the header file for the globals.c file.
 * It also defines some global macros and structs.
 */

#ifndef GLOBALS_H_
#define GLOBALS_H_

/* --- Standard headers --- */
//#include <stdlib.h>
//#include <stdio.h>
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
#include <netinet/in.h>
//#include <arpa/inet.h>
//#include <endian.h>
//#include <netdb.h>
/* --- Functions --- */
//#include <stdarg.h>

/* --- Local headers --- */
#include "pdu.h"
#include "queue.h"

#define EVER (;;)

/* Struct containing the data associated with a client */
typedef struct {
    char *nick;
    int sockfd;
    pthread_mutex_t queue_mutex;
    pthread_cond_t queue_cond;
    queue *send_queue;
} client;

/* Struct containing the data needed by the name server thread */
typedef struct {
    char *name_server_address;
    char *name_server_port;
    pdu_reg reg;
} reg_data;

/* Struct containing the data needed to send a PDU */
typedef struct {
    size_t len;
    uint8_t *pdu;
} pdu_data;

extern pdu_data quit_pdu;

/* Array of connected clients (dynamic list not necessary since max 255) */
extern client *clients[];
extern pthread_mutex_t clients_mutex;

/* Number of clients currently connected to the server */
extern uint8_t nrof_clients;
extern pthread_mutex_t nrof_clients_mutex;

uint8_t get_nrof_clients();
void incr_nrof_clients();
void decr_nrof_clients();
void enqueue(client *cli, pdu_data *pdu);
pdu_data *dequeue(client *cli);
int add_client(client *cli);

#endif /* GLOBALS_H_ */

