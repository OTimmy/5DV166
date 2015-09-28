/*
 * pdu.c
 * Written by Joakim Sandman, September 2015.
 * Last update: 29/9-15.
 * Lab 1: Chattserver, Datakommunikation och datornät HT15.
 *
 * pdu.c contains functions for using the PDU data types.
 */

/* --- Standard headers --- */
//#include <stdlib.h>
//#include <stdio.h>
//#include <time.h> /* -lrt (sometimes for glibc < 2.17) */
//#include <math.h> /* -lm */
#include <string.h>
//#include <sys/wait.h>
/* --- Data types --- */
//#include <stdbool.h>
//#include <stdint.h> /* Subset of inttypes.h */
//#include <inttypes.h> /* Fixed width integers */
//#include <sys/types.h>
//#include <limits.h>
#include <stddef.h>
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
#include <arpa/inet.h>
//#include <endian.h>
//#include <netdb.h>
/* --- Functions --- */
//#include <stdarg.h>

/* --- Local headers --- */
#include "pdu.h"

/*
 * pad_length: Calculates how many bytes are needed as padding to make the
 *      given number of bytes divisible by 4 (to fit evenly in 4 byte words).
 * Params: len = current length of the data to pad.
 * Returns: Number of bytes needed to make the length divisible by 4.
 * Notes:
 */
size_t pad_length(size_t len)
{
    return ((4 - len % 4) % 4);
}

/*
 * reg_arr_size: Calculates the length needed for an array to store the data
 *      contained in a pdu_reg struct, including padding to make the array
 *      length divisible by 4 (to fit evenly in 4 byte words).
 * Params: reg = pdu_reg struct containing the data to be stored.
 * Returns: Length needed for an array to store the data, including padding.
 * Notes:
 */
size_t reg_arr_size(pdu_reg reg)
{
    size_t len = sizeof(reg.op)
                + sizeof(reg.name_len)
                + sizeof(reg.tcp_port)
                + strlen(reg.name);
    len += pad_length(len);
    return len;
}

/*
 * reg_to_array: Serializes the data in a pdu_reg struct into an array (in
 *      network byte order) and pads the rest of the array with zeros.
 * Params: reg_array = array to fill with data and padding.
 *         reg = pdu_reg struct containing the data to be serialized.
 *         array_len = length of the array in bytes.
 * Returns:
 * Notes: The result will be stored in the reg_array argument after the
 *      function returns. This array must be allocated prior to sending it as
 *      an argument to this function.
 */
void reg_to_array(uint8_t reg_array[], pdu_reg reg, size_t array_len)
{
    size_t i = 0;
    size_t name_len = strlen(reg.name);
    memcpy(&reg_array[i], &reg.op, sizeof(reg.op));
    i += sizeof(reg.op);
    memcpy(&reg_array[i], &reg.name_len, sizeof(reg.name_len));
    i += sizeof(reg.name_len);
    reg.tcp_port = htons(reg.tcp_port);
    memcpy(&reg_array[i], &reg.tcp_port, sizeof(reg.tcp_port));
    i += sizeof(reg.tcp_port);
    memcpy(&reg_array[i], reg.name, name_len);
    i += name_len;
    memset(&reg_array[i], 0, array_len - i);
    return;
}

