/*
 * server.h
 * Written by Joakim Sandman, September 2015.
 * Last update: 28/9-15.
 * Lab 1: Chattserver, Datakommunikation och datornät HT15.
 *
 * server.h is the header file for the server program.
 */

#ifndef SERVER_H_
#define SERVER_H_

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

/*
 * compare: Compares two values and determines which is bigger.
 * Params: p1 = pointer to first value.
 *         p2 = pointer to second value.
 * Returns: < 0 if (p1 < p2), == 0 if (p1 == p2) and > 0 if (p1 > p2).
 * Notes:
 */
//int compare(const void *p1, const void *p2);

/* ===== WORK IN PROGRESS ===== */
/*
 * fatal_error: Exits the program (with unsuccessful exit status) after
 *      printing some error messages.
 * Params: pmsg = string to print with perror, denoting what failed.
 *         msg = string to print to stderr, describing the issue. E.g.
 *               "Usage: %s [-t type] start1 [start2 ...] name\n". (%s=argv[0])
 * Returns:
 * Notes: Frees dynamically allocated resources given function.
 */
void fatal_error(char *pmsg, char *msg);

#endif /* SERVER_H_ */

