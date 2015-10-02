/*
 * doorman.h
 * Written by Joakim Sandman, October 2015.
 * Last update: 1/10-15.
 * Lab 1: Chattserver, Datakommunikation och datornät HT15.
 *
 * doorman.h is the header file for the doorman.c file.
 */

#ifndef DOORMAN_H_
#define DOORMAN_H_

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
void *handle_connecting_clients(void *thread_data_dm);

/*
 * bind_client_conn_port: Creates a socket where client connections are to be
 *      accepted and binds it to the given port.
 * Params: conn_port = string representing the port number where the server
 *                     accepts connections.
 * Returns: The socket file descriptor associated with the port.
 * Notes: When finished using it, the socket should be closed to free the
 *      file descriptor.
 */
int bind_client_conn_port(char *conn_port);

#endif /* DOORMAN_H_ */

