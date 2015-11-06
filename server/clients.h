/*
 * clients.h
 * Written by Joakim Sandman, October 2015.
 * Last update: 6/11-15.
 * Lab 1: Chattserver, Datakommunikation och datornät HT15.
 *
 * clients.h is the header file for the clients.c file.
 */

#ifndef CLIENTS_H_
#define CLIENTS_H_

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
//#include <inttypes.h> /* Fixed width integers */
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
//#include <pthread.h> /* -pthread &or -lpthread */
/* --- Sockets --- */
//#include <sys/socket.h>
//#include <netinet/in.h>
//#include <arpa/inet.h>
//#include <endian.h>
//#include <netdb.h>
/* --- Functions --- */
//#include <stdarg.h>

/* --- Local headers --- */
//#include "globals.h"

/*
 * init_new_client: Initializes the client struct and creates a new thread for
 *      sending output to the client. Then wait for JOIN PDU from the client
 *      followed by adding it to the connected clients array. Unless the JOIN
 *      PDU is late or faulty, in which case the connection is closed.
 * Params: thread_data_cli = client pointer containing the following value.
 *      sockfd = integer with the socket file descriptor for communicating
 *               with the new client.
 * Returns: NULL.
 * Notes:
 */
void *init_new_client(void *thread_data_cli);

/*
 * free_pdu_data: Deallocates the dynamically allocated memory of pd.
 * Params: pd = pdu_data struct to deallocate.
 * Returns:
 * Notes: Handles NULL input.
 */
void free_pdu_data(void *pd);

/*
 * handle_client_output: Waits for data to be put into the output queue,
 *      whereupon it is retrieved, sent to the client and deallocated.
 *      If a QUIT PDU is sent, the function terminates.
 * Params: thread_data_oq = client pointer containing the following values.
 *      sockfd = integer with the socket file descriptor for communicating
 *               with the client.
 *      send_queue = queue containing the pdu_data structs containing the data
 *                   to send.
 *      queue_mutex = mutex for synchronizing access to the queue.
 *      queue_cond = condition variable for synchronizing access to the queue.
 * Returns: NULL.
 * Notes:
 */
void *handle_client_output(void *thread_data_oq);

/*
 * handle_client_input: Waits for data to be read from the client, whereupon it
 *      acts according to what data was received. If a MESS or CHNICK PDU is
 *      received, the respective MESS or UCHNICK PDUs are propagated to all
 *      connected clients. 
 *      If a QUIT or faulty PDU is received or if the client closes its side of
 *      the socket connection, the function terminates.
 * Params: cli = client pointer containing the following values.
 *      sockfd = integer with the socket file descriptor for communicating
 *               with the client.
 *      send_queue = queue containing the pdu_data structs containing the data
 *                   to send.
 *      queue_mutex = mutex for synchronizing access to the queue.
 *      queue_cond = condition variable for synchronizing access to the queue.
 * Returns:
 * Notes: Implements a rudimentary spam filter.
 */
void handle_client_input(client *cli);

#endif /* CLIENTS_H_ */

