/*
 * server.c
 * Written by Joakim Sandman, September 2015.
 *
 * server.c is a chat server <description>.
 *
 * Last update: D(D)/M(M)-YY.
 * Version: v0.0.
 * Version notes:
 * Todo:
 */

#include <stdio.h>
#include <stdlib.h>
#include <time.h>
//#include <math.h> /* -lm */
#include <string.h>
//#include <stdbool.h>
//#include <stdint.h>
#include <inttypes.h>
//#include <limits.h>
//#include <errno.h>
//#include <pthread.h> /* -lpthread */
//#include <unistd.h>
//#include <fcntl.h>
#include <sys/types.h>
//#include <sys/stat.h>
//#include <dirent.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>

char *name_server_address = "itchy.cs.umu.se";
int name_server_port = 1337;
//int portno = 51515; // func to search up.

void error(char *msg)
{
    perror(msg);
    //free_all();
    exit(EXIT_FAILURE);
}

void connect_to_name_server()
{
    int sockfd;
    int n;
    struct sockaddr_in serv_addr;
    struct hostent *server;
    uint8_t buffer[8];

    sockfd = socket(AF_INET, SOCK_DGRAM, 0);
    if (sockfd < 0)
    {
        error("ERROR opening socket");
    }
    server = gethostbyname(name_server_address);
    if (server == NULL)
    {
        fprintf(stderr,"ERROR, no such host");
        exit(0);
    }
    bzero((char *) &serv_addr, sizeof(serv_addr));
    serv_addr.sin_family = AF_INET;
    bcopy((char *)server->h_addr,
          (char *)&serv_addr.sin_addr.s_addr,
          server->h_length);
    serv_addr.sin_port = htons(name_server_port);
    if (connect(sockfd,&serv_addr,sizeof(serv_addr)) < 0)
    {
        error("ERROR connecting");
    }
    bzero(buffer,8);
    buffer[0] = 0;
    buffer[1] = 3;
    buffer[4] = 's';
    buffer[5] = 'i';
    buffer[6] = 'r';
/*    fromlen = sizeof(struct sockaddr_in);*/
    n = sendto(sockfd,buffer,8, 0,NULL,0);//strlen(buffer)
    if (n < 0)
    {
        error("ERROR writing to socket");
    }
    bzero(buffer,4);
    n = recvfrom(sockfd,buffer,4, 0,NULL,NULL);
    if (n < 0)
    {
        error("ERROR reading from socket");
    }

    uint16_t ID = (buffer[2] << 8) | (buffer[3] && 0xFF);
    //bzero(buffer,4);
    buffer[0] = 2;
    while(1)
    {
        n = sendto(sockfd,buffer,4, 0,NULL,0);
        if (n < 0)
        {
            error("ERROR writing to socket");
        }
        sleep(6);
    }

    return;
}

int main(int argc, char *argv[])
{
    double runtime2 = 0;
    clock_t start_time2, end_time2;
    start_time2 = clock();

    struct timespec start_time, end_time;
    clock_gettime(CLOCK_MONOTONIC_RAW, &start_time); /* Start timer */

    connect_to_name_server();

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

    clock_gettime(CLOCK_MONOTONIC_RAW, &end_time); /* End timer */
    double runtime = (end_time.tv_sec - start_time.tv_sec)
                     + (end_time.tv_nsec - start_time.tv_nsec)/1000000000.0;
    fprintf(stderr, "\nRuntime: %.2f sec.\n", runtime);

    end_time2 = clock();
    runtime2 = (double) (end_time2 - start_time2)/CLOCKS_PER_SEC;
    fprintf(stderr, "Runtime: %.2f sec.\n", runtime2);

    //getchar();
    return 0;
}

