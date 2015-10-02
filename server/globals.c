/*
 * globals.c
 * Written by Joakim Sandman, September 2015.
 * Last update: 1/10-15.
 * Lab 1: Chattserver, Datakommunikation och datornät HT15.
 *
 * globals.c contains global variables and functions for using them.
 */

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
//#include <netinet/in.h>
//#include <arpa/inet.h>
//#include <endian.h>
//#include <netdb.h>
/* --- Functions --- */
//#include <stdarg.h>

/* --- Local headers --- */
#include "globals.h"

pthread_mutex_t clients_mutex = PTHREAD_MUTEX_INITIALIZER;
client *clients[255]; // Dynamic list not necessary since protocol
// limits number of clients to 255.
// add/remove client funcs

pthread_mutex_t nrof_clients_mutex = PTHREAD_MUTEX_INITIALIZER;
uint8_t nrof_clients = 0;

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

