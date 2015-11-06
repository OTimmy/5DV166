/*
 * globals.h
 * Written by Joakim Sandman, September 2015.
 * Last update: 6/11-15.
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

extern pdu_data quit_pdu;

/* Array of connected clients (dynamic list not necessary since max 255) */
extern client *clients[];
extern pthread_mutex_t clients_mutex;

/* Number of clients currently connected to the server */
extern uint8_t nrof_clients;
extern pthread_mutex_t nrof_clients_mutex;

/*
 * get_nrof_clients: Gets the number of currently connected clients.
 * Params:
 * Returns: The number of currently connected clients.
 * Notes:
 */
uint8_t get_nrof_clients();

/*
 * incr_nrof_clients: Increments the number of currently connected clients by 1.
 * Params:
 * Returns:
 * Notes:
 */
void incr_nrof_clients();

/*
 * decr_nrof_clients: Decrements the number of currently connected clients by 1.
 * Params:
 * Returns:
 * Notes:
 */
void decr_nrof_clients();

/*
 * enqueue: Enqueues the given pdu in the output queue of the given client.
 *      It then signals, with a condition variable, a potentially waiting call
 *      to the dequeue function.
 * Params: cli = client pointer for client to enqueue pdu at.
 *         pdu = pdu_data struct pointer to be enqueued in the output queue.
 * Returns:
 * Notes: Signals after enqueueing!
 */
void enqueue(client *cli, pdu_data *pdu);

/*
 * dequeue: Dequeues the first pdu in the output queue of the given client.
 *      If the queue is empty the function blocks and waits for a condition
 *      variable signal.
 * Params: cli = client pointer for client to dequeue pdu from.
 * Returns: The first pdu_data struct pointer from the output queue.
 * Notes: Potentially blocking call!
 */
pdu_data *dequeue(client *cli);

/*
 * add_client: Adds a client to the connected clients array, if it's not full,
 *      and increments the number of connected clients. Also enqueues UJOIN
 *      PDUs to all other clients.
 * Params: cli = client pointer for client to add to the array.
 * Returns: TRUE if the client was added successfully, FALSE otherwise.
 * Notes:
 */
bool add_client(client *cli);

/*
 * remove_client: Removes a client from the connected clients array, and
 *      decrements the number of connected clients. Also enqueues ULEAVE
 *      PDUs to all other clients.
 * Params: cli = client pointer for client to remove from the array.
 * Returns:
 * Notes:
 */
void remove_client(client *cli);

/*
 * nick_used: Checks weather the given nick is already in use by some other
 *      client on the server.
 * Params: nick = string with nick to check.
 * Returns: TRUE if the nick is already taken, FALSE otherwise.
 * Notes:
 */
bool nick_used(char *nick);

/*
 * get_nicks_pdu: Composes a NICKS PDU to be sent to a new client, including
 *      all currently connected clients and the new (joining) client.
 * Params: cli = client pointer for new client (to make NICKS PDU for).
 * Returns: The pdu_data stuct pointer with the NICKS PDU.
 * Notes:
 */
pdu_data *get_nicks_pdu(client *cli);

/*
 * server_mess: Composes a MESS PDU to be sent to a single client as a server
 *      message (without nick).
 * Params: msg = string to send in the MESS PDU.
 * Returns: The pdu_data stuct pointer with the MESS PDU.
 * Notes: strlen(msg) must be <= 65535.
 */
pdu_data *server_mess(char *msg);

/*
 * mass_server_kick_mess: Composes and propagates a message about a (soon to
 *      be) kicked client to all connected clients.
 * Params: nick = string containing the name of the kicked client.
 *         msg = string giving the reason for kicking.
 * Returns:
 * Notes: strlen(msg) must be <= 32.
 */
void mass_server_kick_mess(char *nick, char *msg);

#endif /* GLOBALS_H_ */

