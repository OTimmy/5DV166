/*
 * pdu.h
 * Written by Joakim Sandman, September 2015.
 * Last update: 5/11-15.
 * Lab 1: Chattserver, Datakommunikation och datornät HT15.
 *
 * pdu.h is the header file for the pdu.c file.
 * It also defines some PDU related macros and data types.
 */

#ifndef PDU_H_
#define PDU_H_

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
//#include <arpa/inet.h>
//#include <endian.h>
//#include <netdb.h>
/* --- Functions --- */
//#include <stdarg.h>

/* Server - Name server */
#define REG_OP 0
#define ACK_OP 1
#define ALIVE_OP 2
#define NOTREG_OP 100
/* Client - Name server (not used by server) */
#define GETLIST_OP 3
#define SLIST_OP 4
/* Server - Client & Client - Server */
#define MESS_OP 10
#define QUIT_OP 11
/* Client - Server */
#define JOIN_OP 12
#define CHNICK_OP 13
/* Server - Client */
#define UJOIN_OP 16
#define ULEAVE_OP 17
#define UCHNICK_OP 18
#define NICKS_OP 19

/* Struct containing the data needed to send a PDU */
typedef struct {
    size_t len;
    uint8_t *pdu;
} pdu_data;

/* Struct containing the data for a REG PDU */
typedef struct {
    uint8_t op;
    uint8_t name_len;
    uint16_t tcp_port;
    char *name;
} pdu_reg;

/*
 * pad_length: Calculates how many bytes are needed as padding to make the
 *      given number of bytes divisible by 4 (to fit evenly in 4 byte words).
 * Params: len = current length of the data to pad.
 * Returns: Number of bytes needed to make the length divisible by 4.
 * Notes:
 */
size_t pad_length(size_t len);

/*
 * reg_arr_size: Calculates the length needed for an array to store the data
 *      contained in a pdu_reg struct, including padding to make the array
 *      length divisible by 4 (to fit evenly in 4 byte words).
 * Params: reg = pdu_reg struct containing the data to be stored.
 * Returns: Length needed for an array to store the data, including padding.
 * Notes:
 */
size_t reg_arr_size(pdu_reg reg);

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
void reg_to_array(uint8_t reg_array[], pdu_reg reg, size_t array_len);

/*
 * verify_join: Verifies that the join PDU is valid.
 * Params: pdu = array of the join PDU to verify.
 *         len = length of the array in bytes.
 * Returns: TRUE if the PDU is valid, FALSE otherwise.
 * Notes: len must be >= 4 + pdu[1].
 */
bool verify_join(uint8_t pdu[], size_t len);

/*
 * verify_mess: Verifies that the mess PDU is valid.
 * Params: pdu = array of the mess PDU to verify.
 *         len = length of the array in bytes.
 * Returns: TRUE if the PDU is valid, FALSE otherwise.
 * Notes: len must be >= 12 + (pdu[4] << 8 | pdu[5]).
 */
bool verify_mess(uint8_t pdu[], size_t len);

/*
 * verify_chnick: Verifies that the chnick PDU is valid.
 * Params: pdu = array of the chnick PDU to verify.
 *         len = length of the array in bytes.
 * Returns: TRUE if the PDU is valid, FALSE otherwise.
 * Notes: len must be >= 4 + pdu[1].
 */
bool verify_chnick(uint8_t pdu[], size_t len);

/*
 * get_checksum: Calculates the checksum of an array of bytes in 8-bit ones'
 *      complement form. This means that all bytes are added up, and whenever
 *      the sum surpasses 255 (>255), the sum is subtracted by 255 (-255).
 *      The checksum is then the bitwise compliment of that sum.
 * Params: bytes = byte array to calculate checksum for.
 *         len = length of the byte array.
 * Returns: The checksum of the (first len bytes of the) given array.
 * Notes:
 */
uint8_t get_checksum(uint8_t bytes[], int len);

#endif /* PDU_H_ */

