/*
 * name_server.h
 * Written by Joakim Sandman, September 2015.
 * Last update: 28/9-15.
 * Lab 1: Chattserver, Datakommunikation och datornät HT15.
 *
 * name_server.h is the header file for the name_server.c file.
 */

#ifndef NAME_SERVER_H_
#define NAME_SERVER_H_

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
#include "globals.h"

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
void *register_at_name_server(void *thread_data_ns);

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
int connect_to_name_server(char *ns_name, char *ns_port);

#endif /* NAME_SERVER_H_ */

