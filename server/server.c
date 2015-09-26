/*
 * server.c
 * Written by Joakim Sandman, September 2015.
 * Last update: 23/9-15.
 * Lab 1: Chattserver, Datakommunikation och datornät HT15.
 *
 * server.c implements a chat server.
 */

/*
 * Compile: gcc -g -std=gnu99 -Wall -pedantic -o <program> <file>.<ext>
 * Memcheck: valgrind --tool=memcheck --leak-check=yes --show-reachable=yes -v ./<program>
 * Run: ./<program>
 */

//                                                          with timeout
// uchnick to changer too? 65507 buffer size udp. non blocking recv for ack?
// existing nick change or deny? wrong PDU quit, message allowed? c tests?
// illasinnade klienter? PDU validering? "händelser" samma ordn? checksum?
// pad func. toByteArray for each PDU struct. how error safe? user friendly?
// notice failing clients.

// yes, yes, select timeout.
// deny, yes, kinda.
// ??, kinda, guess, soon (tm).
// quite safe (don't allways quit), naah.
// yeah.

/* --- Standard headers --- */
#include <stdlib.h>
#include <stdio.h>
#include <time.h> /* -lrt (sometimes for glibc < 2.17) */
//#include <math.h> /* -lm */
#include <string.h>
//#include <sys/wait.h>
/* --- Data types --- */
//#include <stdbool.h>
//#include <stdint.h> /* Subset of inttypes.h */
#include <inttypes.h> /* Fixed width integers */
#include <sys/types.h>
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
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
/* --- Functions --- */
//#include <stdarg.h>

/* --- Local headers --- */
#include "server.h"
#include "name_server.h"

char *name_server_address = "itchy.cs.umu.se";
int name_server_port = 1337;
//int portno = 51515; // func to search up. if bind fails.
uint8_t nrof_clients = 0;
int client_connection_port = 51515;
//struct client clients[255]; // Dynamic list not necessary since protocol
// limits number of clients to 255.
//message_queue;

void error(char *msg)
{
    perror(msg);
    //free_all();
    exit(EXIT_FAILURE);
}

/*void connect_to_name_server()*/
/*{*/
/*    int sockfd;*/
/*    int n;*/
/*    struct sockaddr_in serv_addr;*/
/*    struct hostent *server;*/
/*    uint8_t buffer[8];*/

/*    sockfd = socket(AF_INET, SOCK_DGRAM, 0);*/
/*    if (sockfd < 0)*/
/*    {*/
/*        error("ERROR opening socket");*/
/*    }*/
/*    server = gethostbyname(name_server_address);//getaddrinfo*/
/*    if (server == NULL)*/
/*    {*/
/*        fprintf(stderr,"ERROR, no such host");*/
/*        exit(0);*/
/*    }*/
/*    bzero((char *) &serv_addr, sizeof(serv_addr));//memset*/
/*    serv_addr.sin_family = AF_INET;*/
/*    bcopy((char *)server->h_addr,//memcpy*/
/*          (char *)&serv_addr.sin_addr.s_addr,*/
/*          server->h_length);*/
/*    serv_addr.sin_port = htons(name_server_port);*/
/*    if (connect(sockfd,&serv_addr,sizeof(serv_addr)) < 0)*/
/*    {*/
/*        error("ERROR connecting");*/
/*    }*/
/*    bzero(buffer,8);*/
/*    buffer[0] = 0;*/
/*    buffer[1] = 3;*/
/*    buffer[4] = 's';*/
/*    buffer[5] = 'i';*/
/*    buffer[6] = 'r';*/
//    fromlen = sizeof(struct sockaddr_in);
/*    n = sendto(sockfd,buffer,8, 0,NULL,0);//strlen(buffer)*/
/*    if (n < 0)*/
/*    {*/
/*        error("ERROR writing to socket");*/
/*    }*/
/*    bzero(buffer,4);*/
/*    n = recvfrom(sockfd,buffer,4, 0,NULL,NULL);*/
/*    if (n < 0)*/
/*    {*/
/*        error("ERROR reading from socket");*/
/*    }*/

/*    uint16_t ID = (buffer[2] << 8) | (buffer[3] && 0xFF);*/
/*    //bzero(buffer,4);*/
/*    buffer[0] = 2;*/
/*    while(1)*/
/*    {*/
/*        n = sendto(sockfd,buffer,4, 0,NULL,0);*/
/*        if (n < 0)*/
/*        {*/
/*            error("ERROR writing to socket");*/
/*        }*/
/*        sleep(6);//20/nrof_send_per_timeout (integer div)*/
/*    }//keep alive til NOTREG received*/

/*    return;*/
/*}*/

int main(int argc, char *argv[])
{
    struct timespec start_time, end_time;
    clock_gettime(CLOCK_MONOTONIC_RAW, &start_time); /* Start timer */

    double proc_runtime = 0;
    clock_t proc_start_time, proc_end_time;
    proc_start_time = clock();  /* Start process timer */

    char *name;
    if (1 < argc)
    {
        name = argv[1];
    }
    else
    {
        name = "Anti-SkyNet";
        //name = "John Connor";
        //name = "Joshua"; // The only winning move is not to play.
    }
    //init
    //find portno
    //listen
    pdu_reg reg = {0, strlen(name), 51515, name}; //other portno
    register_at_name_server("itchy.cs.umu.se", "1337", reg);

/*    int sockfd, newsockfd, portno, clilen, n;*/
/*    char buffer[256];*/
/*    struct sockaddr_in serv_addr, cli_addr;*/
/*    struct hostent *server;*/
/*    if (argc < 2) {*/
/*        fprintf(stderr,"ERROR, no port provided");*/
/*        exit(1);*/
/*    }*/
/*    sockfd = socket(AF_INET, SOCK_STREAM, 0);*/
/*    if (sockfd < 0)*/
/*        error("ERROR opening socket");*/
/*    */
/*    bzero((char *) &serv_addr, sizeof(serv_addr));*/
/*    portno = atoi(argv[1]);*/
/*    serv_addr.sin_family = AF_INET;*/
/*    serv_addr.sin_port = htons(portno);*/
/*    serv_addr.sin_addr.s_addr = INADDR_ANY;*/
/*    */
/*    if (bind(sockfd, (struct sockaddr *) &serv_addr, sizeof(serv_addr)) < 0)*/
/*        error("ERROR on binding");*/
/*    listen(sockfd,5);*/
/*    clilen = sizeof(cli_addr);*/
/*    newsockfd = accept(sockfd, (struct sockaddr *) &cli_addr, &clilen);*/
/*    if (newsockfd < 0)*/
/*        error("ERROR on accept");*/
/*    */
/*    bzero(buffer,256);*/
/*    n = read(newsockfd,buffer,255);*/
/*    if (n < 0) error("ERROR reading from socket");*/
/*    printf("Here is the message: %s",buffer);*/
/*    n = write(newsockfd,"I got your message",18);*/
/*    if (n < 0) error("ERROR writing to socket");*/

    proc_end_time = clock(); /* End process timer */
    proc_runtime = (double) (proc_end_time - proc_start_time)/CLOCKS_PER_SEC;
    fprintf(stderr, "\nProcess runtime: %.2f sec.\n", proc_runtime);

    clock_gettime(CLOCK_MONOTONIC_RAW, &end_time); /* End timer */
    double runtime = (end_time.tv_sec - start_time.tv_sec)
                     + (end_time.tv_nsec - start_time.tv_nsec)/1000000000.0;
    fprintf(stderr, "\nRuntime: %.2f sec.\n", runtime);

    //getchar();
    return 0;
}

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
void fatal_error(char *pmsg, char *msg)
{
    fprintf(stderr, "\nWarning: ERROR occurred, couldn't finish program!\n");
    fprintf(stderr, msg);
    perror(pmsg);
    //free_all(); // conditional to preprocessor directive (as timers?)
    exit(EXIT_FAILURE);
}

