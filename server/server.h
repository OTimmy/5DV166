/*
 * server.h
 * Written by Joakim Sandman, September 2015.
 * Last update: 8/10-15.
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
 * parse_arguments: Parse the input arguments, including the options
 *      (preceded by -) and set the corresponding values.
 * Params: argc = the number of arguments in argv.
 *         argv = array of strings from command line input.
 * Returns:
 * Notes: Can alter the value of this files global variables.
 */
void parse_arguments(int argc, char *argv[]);

/*
 * is_uint16: Determines if the string represents an integer in [0, 65535].
 * Params: str = string to evaluate.
 * Returns: TRUE if the string represents an uint16, FALSE otherwise.
 * Notes:
 */
int is_uint16(const char *str);

#endif /* SERVER_H_ */

