/*
 * clients.c
 * Written by Joakim Sandman, October 2015.
 * Last update: 7/10-15.
 * Lab 1: Chattserver, Datakommunikation och datornät HT15.
 *
 * clients.c contains functions for handling client input and output.
 */

/* --- Standard headers --- */
#include <stdlib.h>
#include <stdio.h>
//#include <time.h> /* -lrt (sometimes for glibc < 2.17) */
//#include <math.h> /* -lm */
#include <string.h>
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
#include <unistd.h>
#include <errno.h>
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
#include "pdu.h"
#include "queue.h"
#include "clients.h"

/*
 * init_new_client: Initializes the client struct and creates a new thread for
 *      sending output to the client. Then wait for JOIN PDU from the client
 *      followed by adding it to the connected clients array. Unless the JOIN
 *      PDU is late or faulty, in case the connection is closed.
 * Params: thread_data_cli = client pointer containing the following value.
 *      sockfd = integer with the socket file descriptor for communicating
 *               with the new client.
 * Returns: NULL.
 * Notes:
 */
void *init_new_client(void *thread_data_cli)
{
    /* Extract thread data */
    client *cli = (client *) thread_data_cli;

    /* Initialize thread attribute to detached */
    pthread_attr_t attr;
    pthread_attr_init(&attr);
    pthread_attr_setdetachstate(&attr, PTHREAD_CREATE_DETACHED);

    /* Initialize client structure data */
    pthread_mutex_init(&cli->queue_mutex, NULL);
    pthread_cond_init(&cli->queue_cond, NULL);
    cli->send_queue = queue_empty();
    //queue_setFreeFunc(cli->send_queue, free);

    /* Create thread for handling the client output queue */
    pthread_t thread_oq;
    if (0 != pthread_create(&thread_oq, &attr, handle_client_output,
                            (void *) cli))
    {
        fprintf(stderr, "ERROR: Failed to create thread oq!\n");
        return NULL;
    }

    int err;
    uint8_t header[4] = {0}; /* JOIN PDU header */

    /* Setup select() to monitor the client socket, with a timeout */
    struct timeval timeout = {3, 0};
    struct timeval count_down;
    fd_set changed_fds;
    fd_set read_fds;
    FD_ZERO(&read_fds);
    FD_SET(cli->sockfd, &read_fds);

    /* Wait for JOIN PDU and close connection if too much time passes */
    for (int i = 0; i < 3; i++) /* Allow limited number of faulty packages */
    {
        count_down = timeout; /* Reset timeout period for monitoring */
        changed_fds = read_fds; /* Reset file descriptors to monitor */
        if (select(cli->sockfd + 1, &changed_fds, NULL, NULL, &count_down) < 0)
        {
            perror("select (join)");
            continue;
        }
        else /* FD_ISSET(cli->sockfd, &read_fds) */
        {
            err = recv(cli->sockfd, header, sizeof(header), MSG_DONTWAIT);
            if (err < 0)
            {
                /* select() timed out and no JOIN was received */
                if (EAGAIN == errno || EWOULDBLOCK == errno)
                {
                    perror("recv (join block)");
                    break; /* Terminate client connection */
                }
                else
                {
                    perror("recv (join)");
                    continue;
                }
            }
            // check header
            if (JOIN_OP == header[0])
            {
                int nick_len = header[1];//int?????????
                cli->nick = malloc(nick_len + 1);
                if (NULL == cli->nick)
                {
                    perror("malloc (nick)");
                    break; /* Terminate client connection */
                }
                size_t pad = pad_length(nick_len);
                uint8_t buf[nick_len + pad]; /* Client nickname buffer */
                err = recv(cli->sockfd, buf, sizeof(buf), MSG_DONTWAIT);
                if (err < 0)
                {
                    perror("recv (nick)");
                    break; /* Terminate client connection */
                }
                /* Set nick in client */
                err = snprintf(cli->nick, nick_len + 1, "%s", buf);
                if (err < 0) // ignore if trunc (expected)
                {
                    perror("snprintf (nick)");
                    break; /* Terminate client connection */
                }
                //verify
                // JOIN PDU is verified!
                // check if nick already used
                // uint8_t nicks[] = get_nicks_pdu(cli);
                uint8_t nicks[] = {NICKS_OP, 1, 0, 4, 'S', 'i', 'r', '\0'};
                uint8_t *nicks_copy = malloc(sizeof(nicks));
                memcpy(nicks_copy, nicks, sizeof(nicks));//watch sizeof!!!
                pdu_data *nicks_pdu = malloc(sizeof(pdu_data));
                nicks_pdu->len = sizeof(nicks);//watch sizeof!!!
                nicks_pdu->pdu = nicks_copy;
                enqueue(cli, nicks_pdu);
                
/*                pdu_data nicks_pdu = {sizeof(nicks), nicks};*/
/*                enqueue(cli, &nicks_pdu); // ???????????????????????mutex?*/
                // send UJOIN PDU and sign up on list
                int fail = add_client(cli);
                if (fail)
                {
                    // quit w/ msg full server!
                    fprintf(stderr, "ERROR: Failed to add client!\n");
                    break;
                }
                handle_client_input(cli);
                //
                //send(cli->sockfd, nicks, 8, 0);//select???????
                //for EVER {}
                fprintf(stderr, "ERROR: client left!\n");
                break;
            }
            else /* Not a JOIN PDU */
            {
                //return quit and err msg???? and err msg to others????
                uint8_t quit[] = {QUIT_OP, 0, 0, 0};
                send(cli->sockfd, quit, 4, 0);//select???????
                fprintf(stderr, "ERROR: NOT JOIN!\n");
                break; /* Terminate client connection */
            }
        }
    }

    /* Close the connection, free all thread resources and exit thread */
    close(cli->sockfd);
    pthread_attr_destroy(&attr);
    pthread_mutex_lock(&cli->queue_mutex);
    queue_free(cli->send_queue);
    pthread_mutex_unlock(&cli->queue_mutex);
    pthread_mutex_destroy(&cli->queue_mutex);
    pthread_cond_destroy(&cli->queue_cond);
    free(cli);
    return NULL;
}

/*
 * init_new_client: Initializes the client struct and creates a new thread for
 *      sending output to the client. Then wait for JOIN PDU from the client
 *      followed by adding it to the connected clients array. Unless the JOIN
 *      PDU is late or faulty, in case the connection is closed.
 * Params: thread_data_cli = client pointer containing the following value.
 *      sockfd = integer with the socket file descriptor for communicating
 *               with the new client.
 * Returns: NULL.
 * Notes:
 */
void *handle_client_output(void *thread_data_oq)
{
    /* Extract thread data */
    client *cli = (client *) thread_data_oq;

    int err;
    uint8_t *send_array;
    pdu_data *send_data;

    for EVER // select and EMSGSIZE and cond var, notice disconn, when quit or send err
    {
        send_data = NULL;
        pthread_mutex_lock(&cli->queue_mutex);
        if (queue_isEmpty(cli->send_queue))
        {
/*            printf("we're waiting!\n");*/
            pthread_cond_wait(&cli->queue_cond, &cli->queue_mutex);
/*            printf("we're sending again!\n");*/
        }
        else
        {
/*            printf("we're getting stuff!\n");*/
            send_data = queue_front(cli->send_queue);
            queue_dequeue(cli->send_queue);
        }
        pthread_mutex_unlock(&cli->queue_mutex);

        if (NULL != send_data) // small loop?
        {
            send_array = send_data->pdu;
            printf("we're sending %d bytes!\n", (uint32_t)send_data->len);
            err = send(cli->sockfd, send_array, send_data->len, MSG_DONTWAIT);
            free(send_array); // fix free func???
            free(send_data);
            if (err < 0)
            {
                /* select() was mistaken and nothing was sent */
                if (EAGAIN == errno || EWOULDBLOCK == errno)
                {
                    perror("send (out block)");
                    continue; // try again? close conn?
                }
                else
                {
                    perror("send (out)");
                    continue; // try again? close conn?
                }
            }
        }
    }

    /* Close the connection, free all thread resources and exit thread */
/*    close(cli->sockfd);*/
/*    queue_free(cli->send_queue);*/
/*    pthread_mutex_destroy(&cli->queue_mutex);*/
/*    pthread_cond_destroy(&cli->queue_cond);*/
/*    free(cli);*/
    return NULL;
}

/*
 * init_new_client: Initializes the client struct and creates a new thread for
 *      sending output to the client. Then wait for JOIN PDU from the client
 *      followed by adding it to the connected clients array. Unless the JOIN
 *      PDU is late or faulty, in case the connection is closed.
 * Params: thread_data_cli = client pointer containing the following value.
 *      sockfd = integer with the socket file descriptor for communicating
 *               with the new client.
 * Returns: NULL.
 * Notes:
 */
void handle_client_input(client *cli)
{
    /* Extract thread data */
    //client *cli = (client *) thread_data_oq;

    int err;
    int communicating = 1;
    //uint8_t *send_array;
    uint8_t header[4] = {0}; /* PDU header */
    uint8_t nick_len = strlen(cli->nick); // checked earlier for len
    size_t nick_pad = pad_length(nick_len);
    int spam_count = 0;
    uint32_t spam_time = time(NULL);;
    uint32_t spam_time2;

    while (communicating)
    {
        err = recv(cli->sockfd, header, sizeof(header), 0);
        if (0 == err) /* Client disconnected */
        {
            //communicating = 0;
            break; /* Exit thread */
        }
        else if (err < 0)
        {
            perror("recv (in)");
            continue; // try again? close conn?
        }
        switch (header[0]) /* Check OP code */
        {
        case MESS_OP:
            // check header
            ;
            uint8_t more_header[8] = {0}; /* Rest of MESS PDU header */
            err = recv(cli->sockfd, more_header, sizeof(more_header), 0);
            if (err < 0)
            {
                perror("recv (in msg head)");
                communicating = 0; /* Terminate client connection */
                break; // try again? close conn?
            }
            // check more header
            uint16_t mess_len = (more_header[0] << 8) | (more_header[1] & 0xFF);
            size_t mess_pad = pad_length(mess_len);
            uint8_t *message = malloc(mess_len + mess_pad);
            //memset 0?
            // recv rest
            err = recv(cli->sockfd, message, mess_len + mess_pad, 0);
            if (err < 0)
            {
                perror("recv (in more head)");
                communicating = 0; /* Terminate client connection */
                free(message);
                break; // try again? close conn?
            }
            // verify (len)
            // join / memcpy? to send_array?
            size_t send_array_len = 12 + mess_len + mess_pad + nick_len + nick_pad;
            uint8_t *send_array = malloc(send_array_len);
            memset(send_array, 0, send_array_len);
            send_array[0] = MESS_OP;
            send_array[2] = nick_len;
            send_array[4] = more_header[0];
            send_array[5] = more_header[1];
            memcpy(&send_array[12], message, mess_len);
            memcpy(&send_array[12 + mess_len + mess_pad], cli->nick, nick_len);
            free(message);
            // lock
            pthread_mutex_lock(&clients_mutex);
            // create / build msg
            uint32_t unix_time = htonl(time(NULL));
            memcpy(&send_array[8], &unix_time, sizeof(unix_time));
            send_array[3] = get_checksum(send_array, send_array_len);
            
/*            for (int i = 12; i < 12 + mess_len; i++)*/
/*            {*/
/*                printf("%c", send_array[i]);*/
/*            }*/
/*            printf("\n");*/
/*            for (int i = 12 + mess_len + mess_pad; i < 12 + mess_len + mess_pad + nick_len; i++)*/
/*            {*/
/*                printf("%c", send_array[i]);*/
/*            }*/
/*            printf("\n");*/
/*            printf("%d clients\n", get_nrof_clients());*/
            
            // enqueue + signal loop (propagate pdu)
            for (int i = 0; i < 255; i++) // exit when >num cli
            {
                if (NULL != clients[i])
                {
/*                    uint8_t send_array_copy[send_array_len]; // malloc?*/
/*                    memcpy(send_array_copy, send_array, send_array_len);*/
/*                    pdu_data msg_pdu = {send_array_len, send_array_copy};*/
/*                    printf("%d\n", (int)send_array_len);*/
/*                    for (int i = 12; i < 12 + mess_len; i++)*/
/*                    {*/
/*                        printf("%c", send_array_copy[i]);*/
/*                    }*/
/*                    printf("\n");*/
/*                    enqueue(clients[i], &msg_pdu); // nocopyneeded????yes??*/
                    
/*                    uint8_t *send_array_copy = malloc(sizeof(send_array)); // malloc to begin with???*/
/*                    memcpy(send_array_copy, send_array, send_array_len);*/
                    pdu_data *msg_pdu = malloc(sizeof(pdu_data));
                    msg_pdu->len = send_array_len;
                    msg_pdu->pdu = send_array;
                    
/*                    printf("\n%d OP\n", send_array[0]);*/
/*                    printf("%d nick len\n", send_array[2]);*/
/*                    printf("%d checksum\n", send_array[3]);*/
/*                    printf("%d mess len\n", send_array[5]);*/
/*                    printf("%d size\n", (uint32_t)send_array_len);*/
/*                    printf("%d size\nmess: ", (uint32_t)msg_pdu->len);*/
/*                    for (int i = 12; i < 12 + mess_len; i++)*/
/*                    {*/
/*                        printf("%c", send_array[i]);*/
/*                    }*/
/*                    printf("\nname: ");*/
/*                    for (int i = 12 + mess_len + mess_pad; i < 12 + mess_len + mess_pad + nick_len; i++)*/
/*                    {*/
/*                        printf("%c", send_array[i]);*/
/*                    }*/
/*                    printf("\n\n");*/
                    
                    enqueue(clients[i], msg_pdu);
                }
            }
/*            free(send_array);*/
            // unlock
            pthread_mutex_unlock(&clients_mutex);
            break;
        case CHNICK_OP:
            // check header
            // recv rest
            // verify
            // join / memcpy? to send_array?
            // lock
            // create / build uchnick
            // enqueue + signal loop
            // unlock
            break;
        case QUIT_OP:
            // check header
            // lock
            // create / build uleave
            // remove_client() + signal???
            // unlock
            break;
        default:
            //return quit and err msg???? and err msg to others????
            ;
            uint8_t quit[] = {QUIT_OP, 0, 0, 0}; //enqueue???
            send(cli->sockfd, quit, sizeof(quit), 0);//select???????
            communicating = 0; /* Terminate client connection */
            // break; /* Terminate client connection */
        }
        spam_time2 = time(NULL);
        if ((spam_time2 - spam_time) > 0)
        {
            spam_count = 0;
        }
        else
        {
            spam_count++;
        }
        if (spam_count > 2)
        {
            // spam msg
            break; /* Terminate client connection */
        }
        spam_time = spam_time2;
    }
    // kill sender
    // remove_client()?????????

    /* Close the connection, free all thread resources and exit thread */
/*    close(cli->sockfd);*/
/*    queue_free(cli->send_queue);*/
/*    pthread_mutex_destroy(&cli->queue_mutex);//???????????????????????*/
/*    pthread_cond_destroy(&cli->queue_cond);*/
/*    free(cli);*/
    return;
}

