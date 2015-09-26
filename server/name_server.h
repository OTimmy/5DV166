/*
 * name_server.h
 * Written by Joakim Sandman, September 2015.
 * Last update: 16/9-15.
 * Lab 1: Chattserver, Datakommunikation och datornät HT15.
 *
 * name_server.h is the header file for the name_server.c file.
 */

#ifndef NAME_SERVER_H_
#define NAME_SERVER_H_

/* --- Standard headers --- */
#include <stdlib.h>
#include <stdio.h>
#include <time.h> /* -lrt (sometimes for glibc < 2.17) */
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
//#include <fcntl.h> /* File control */
//#include <sys/stat.h> /* Stat function */
/* --- Signals and threads --- */
//#include <signal.h>
//#include <setjmp.h>
//#include <pthread.h> /* -lpthread */
/* --- Sockets --- */
//#include <sys/socket.h>
//#include <netinet/in.h>
//#include <netdb.h>
/* --- Functions --- */
//#include <stdarg.h>

/*
 * compare: Compares two values and determines which is bigger.
 * Params: p1 = pointer to first value.
 *         p2 = pointer to second value.
 * Returns: < 0 if (p1 < p2), == 0 if (p1 == p2) and > 0 if (p1 > p2).
 * Notes:
 */
void register_at_name_server(char *ns_name, char *ns_port, pdu_reg reg);
int connect_to_name_server(char *ns_name, char *ns_port);

#endif /* NAME_SERVER_H_ */

