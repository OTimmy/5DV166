/*
 * clients.c
 * Written by Joakim Sandman, October 2015.
 * Last update: 4/11-15.
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

/*fix!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
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
    pthread_mutex_init(&cli->queue_mutex_exit, NULL);
    pthread_cond_init(&cli->queue_cond, NULL);
    pthread_cond_init(&cli->queue_cond_exit, NULL);
    cli->send_queue = queue_empty();

    /* Create thread for handling the client output queue */
    pthread_t thread_oq;
    if (0 != pthread_create(&thread_oq, &attr, handle_client_output,// DONT LET ESCAPE!!!!!!!!!
                            (void *) cli))
    {
        fprintf(stderr, "ERROR: Failed to create thread oq!\n");
        return NULL;
    }

    int err = -1;
    uint8_t header[2] = {0}; /* Informative part of JOIN PDU header */

    /* Setup select() to monitor the client socket, with a timeout */
    struct timeval timeout = {2, 0};
    struct timeval count_down;
    fd_set changed_fds;
    fd_set read_fds;
    FD_ZERO(&read_fds);
    FD_SET(cli->sockfd, &read_fds);

    /* Wait for JOIN PDU and close connection if too much time passes */
    for (int i = 0; i < 3; i++) /* Allow limited number of receive hickups */
    {
        count_down = timeout; /* Reset timeout period for monitoring */
        changed_fds = read_fds; /* Reset file descriptors to monitor */
        if (select(cli->sockfd + 1, &changed_fds, NULL, NULL, &count_down) < 0)
        {
            perror("select (join)");
            continue; /* Try again */
        }
        else /* FD_ISSET(cli->sockfd, &read_fds) */
        {
            err = recv(cli->sockfd, header, sizeof(header),
                       MSG_DONTWAIT | MSG_PEEK);
            if (0 == err) /* Client disconnected */
            {
                break; /* Exit thread */
            }
            else if (err < 0)
            {
                /* select() timed out or fooled by faulty packet */
                if (EAGAIN == errno || EWOULDBLOCK == errno)
                {
                    perror("recv (join block)");
                    continue; /* Try again */
                }
                else
                {
                    perror("recv (join)");
                    continue; /* Try again */
                }
            }
            else if (err < sizeof(header)) /* Not all bytes were received */
            {
                continue; /* Try again */
            }
            if (JOIN_OP == header[0]) /* Check OP code */
            {
                size_t nick_len = header[1];
                size_t pad = pad_length(nick_len);
                size_t join_len = 4 + nick_len + pad;
                uint8_t join[join_len]; /* JOIN PDU buffer */
                err = recv(cli->sockfd, join, join_len, MSG_WAITALL);//wait but comment!!!!???
                if (0 == err) /* Client disconnected */
                {
                    break; /* Exit thread */
                }
                else if (err < 0)
                {
                    perror("recv (nick)");
                    break; /* Terminate client connection */
                }

/*                int read = 0;*/
/*                while (read < join_len) // recv_all() with timeout????sizeof!!!!handle err!!!*/
/*                {*/
/*                    read += recv(cli->sockfd, &join[read], join_len - read, 0);*/
/*                }*/

                if (!verify_join(join, join_len))
                {
                    //errmsg
                    break; /* Terminate client connection */
                }

                /* Initiate and set nick in client */
                cli->nick = malloc(nick_len + 1);
                if (NULL == cli->nick)
                {
                    perror("malloc (nick)");
                    break; /* Terminate client connection */
                }
                err = snprintf(cli->nick, nick_len + 1, "%s", &join[4]);
                if (err < 0) // ignore if trunc (expected)
                {
                    perror("snprintf (nick)");
                    break; /* Terminate client connection */
                }

                if (nick_used(cli->nick))
                {
                    //errmsg
                    break; /* Terminate client connection */
                }
                // JOIN PDU is verified!
                // uint8_t nicks[] = get_nicks_pdu(cli);
/*                uint8_t nicks[] = {NICKS_OP, 1, 0, 4, 'S', 'i', 'r', '\0'};*/
/*                uint8_t *nicks_copy = malloc(sizeof(nicks));*/
/*                memcpy(nicks_copy, nicks, sizeof(nicks));//watch sizeof!!!*/
/*                pdu_data *nicks_pdu = malloc(sizeof(pdu_data));*/
/*                nicks_pdu->len = sizeof(nicks);//watch sizeof!!!*/
/*                nicks_pdu->pdu = nicks_copy;*/
/*                enqueue(cli, nicks_pdu);*/
                pdu_data *nicks_pdu = get_nicks_pdu(cli);
                if (NULL == nicks_pdu)
                {
                    break; /* Terminate client connection */
                }
                enqueue(cli, nicks_pdu);
                // send UJOIN PDU and sign up on list
                if (!add_client(cli))
                {
                    // quit w/ msg full server!
                    fprintf(stderr, "ERROR: Failed to add client!\n");
                    break;
                }
                handle_client_input(cli);
/*                fprintf(stderr, "ERROR: client left!\n");*/
                err = 0;//?????????????????????????
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
    if (0 != err) /* Terminate client connection */
    {
        //enqueue(cli, server_mess("Error while joining!"));
        //enqueue(cli, &quit_pdu);
    }
//handle cli a thread,, this thread killed, below done in one of others.//mallocs
/*    enqueue();*/
    // remove client: uleave to all, remove cli,//mutex// signal, wait (out sig when quit)
    pthread_mutex_lock(&clients_mutex);//mutex lower!!!!

    size_t nick_len = strlen(cli->nick);
    size_t pad = pad_length(nick_len);
    uint8_t uleave[8 + nick_len + pad];
    memset(uleave, 0, sizeof(uleave));
    uleave[0] = ULEAVE_OP;
    uleave[1] = nick_len;

    uint32_t unix_time = htonl(time(NULL));
    size_t i = 4;
    memcpy(&uleave[i], &unix_time, sizeof(unix_time));
    i += sizeof(unix_time);
    memcpy(&uleave[i], cli->nick, nick_len);

    for (int i = 0; i < 255; i++)
    {
        if (cli == clients[i])
        {
            clients[i] = NULL;
            decr_nrof_clients();
            enqueue(cli, &quit_pdu);
        }
        else if (NULL != clients[i])
        {
            uint8_t *uleave_copy = malloc(sizeof(uleave));
            memcpy(uleave_copy, uleave, sizeof(uleave));
            pdu_data *uleave_pdu = malloc(sizeof(pdu_data));
            uleave_pdu->len = sizeof(uleave);
            uleave_pdu->pdu = uleave_copy;
            enqueue(clients[i], uleave_pdu);
        }
    }
printf("we got this far!!      ");
    pthread_mutex_unlock(&clients_mutex);
/*    pthread_mutex_lock(&cli->queue_mutex_exit);*/
    pthread_cond_signal(&cli->queue_cond);
/*    pthread_cond_wait(&cli->queue_cond_exit, &cli->queue_mutex_exit);*/
/*    pthread_mutex_unlock(&cli->queue_mutex_exit);*/
sleep(5);
printf("Gettttttttttttttterrrrrrrrrrrrr\n");//sometimes not executed!!!!!
    /* Close the connection, free all thread resources and exit thread */
    close(cli->sockfd);
    free(cli->nick);
    pthread_attr_destroy(&attr);
    //queue_setFreeFunc(cli->send_queue, free); // make freefunc!!!!!!!
    pthread_mutex_lock(&cli->queue_mutex);
    queue_free(cli->send_queue);
    pthread_mutex_unlock(&cli->queue_mutex);

    pthread_mutex_destroy(&cli->queue_mutex);
    pthread_cond_destroy(&cli->queue_cond);
    pthread_mutex_destroy(&cli->queue_mutex_exit);
    pthread_cond_destroy(&cli->queue_cond_exit);
    printf("Get to da choppah!!!  ");
    free(cli);
    printf("Get to da chopererererererer!!!\n");
    fprintf(stderr, "CLIENT TERMINATED!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n");
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

    for EVER // EMSGSIZE, notice disconn, when quit or send err??????????????
    {
        send_data = dequeue(cli); /* Blocking call */
/*        pthread_mutex_lock(&cli->queue_mutex);*/
/*        if (queue_isEmpty(cli->send_queue))*/
/*        {*/
/*            printf("we're waiting!\n");*/
/*            pthread_cond_wait(&cli->queue_cond, &cli->queue_mutex);*/
/*            printf("we're sending again!\n");*/
/*        }*/
/*        else*/
/*        {*/
/*            printf("we're getting stuff!\n");*/
/*            send_data = queue_front(cli->send_queue);*/
/*            queue_dequeue(cli->send_queue);*/
/*        }*/
/*        pthread_mutex_unlock(&cli->queue_mutex);*/
//QUIT!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        if (NULL != send_data) // small loop?
        {
            send_array = send_data->pdu;
            printf("we're sending %d bytes!\n", (uint32_t)send_data->len);//12bytes? MESS?
/*            printf("MESS: %s\n", send_array);*/
            err = send(cli->sockfd, send_array, send_data->len, MSG_NOSIGNAL);//MSG_DONTWAIT);
            if (QUIT_OP == send_array[0])
            {
/*                printf("quitting sender!!!!!!!!!!!!!!!!!!!!!!!!!\n");*/
                fflush(stdout);
                break;
            }
            else
            {
                free(send_array);
                free(send_data); //select needed to free???????????????????????
            }
            if (err < 0)
            {
                /* select() was mistaken and nothing was sent */
/*                if (EAGAIN == errno || EWOULDBLOCK == errno)*/
/*                {*/
/*                    perror("send (out block)");*/
/*                    continue; // try again? close conn?*/
/*                }*/
/*                else*/
/*                {*/
                    perror("send (out)");
                    break; // try again? close conn? depend on error!!!!!!!!!
/*                }*/
                // if error cancel other threads (close or shutdown socket)
            }
        }
    }// if send quit or cannot send(error)
    pthread_mutex_lock(&cli->queue_mutex_exit);
    pthread_cond_signal(&cli->queue_cond_exit);
    pthread_mutex_unlock(&cli->queue_mutex_exit);
    printf("quitteddddddddddddd----------------------------!\n");
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
    int err;
    int communicating = 1;
    //uint8_t *send_array;
    uint8_t header[4] = {0}; /* PDU header */
    uint8_t nick_len = strlen(cli->nick);
    size_t nick_pad = pad_length(nick_len);
    size_t nick_size = nick_len + nick_pad;
    int spam_count = 0;
    uint32_t spam_time = time(NULL);
    uint32_t spam_time2;

    while (communicating)
    {
        err = recv(cli->sockfd, header, sizeof(header), MSG_WAITALL);
        if (0 == err) /* Client disconnected */
        {
            //communicating = 0;//if used as flag later
            break; /* Exit thread */
        }
        else if (err < 0)
        {
            perror("recv (in)");
            continue; /* Try again */
        }
        switch (header[0]) /* Check OP code */
        {
        case MESS_OP:
            ; /* Dummy line to please gcc */
            // check header
            uint8_t more_header[8] = {0}; /* Rest of MESS PDU header */
            err = recv(cli->sockfd, more_header, sizeof(more_header),
                       MSG_WAITALL);
            if (0 == err) /* Client disconnected */
            {
                communicating = 0;
                break; /* Exit thread */
            }
            if (err < 0)
            {
                perror("recv (in msg head)");
                communicating = 0; /* Terminate client connection */
                break; // try again? close conn?
            }
            // check more header
            uint16_t mess_len = (more_header[0] << 8) | (more_header[1] & 0xFF);
            size_t mess_pad = pad_length(mess_len);
            size_t mess_size = mess_len + mess_pad;
            uint8_t *message = malloc(mess_size);
/*            uint8_t message[mess_size];*/
            // recv rest (not if len 0)
            err = recv(cli->sockfd, message, mess_size, MSG_WAITALL);
            if (0 == err && mess_size > 0) /* Client disconnected */
            {
                communicating = 0;
                break; /* Exit thread */
            }
            else if (err < 0)
            {
                perror("recv (in more head)");
                communicating = 0; /* Terminate client connection */
/*                free(message);*/
                break; // try again? close conn?
            }
            // verify (len)
/*            uint8_t mess[12 + mess_size];*/
            uint8_t *mess = malloc(12 + mess_size);
            memcpy(&mess[0], header, sizeof(header));
            memcpy(&mess[4], more_header, sizeof(more_header));
            memcpy(&mess[12], message, mess_size);
            if (!verify_mess(mess, 12 + mess_size))
            {
                //errmsg
                communicating = 0; /* Terminate client connection */
                free(mess);
                break;
            }
            free(mess);
            // join / memcpy? to send_array?
            size_t send_array_len = 12 + mess_size + nick_size;
            uint8_t *send_array = malloc(send_array_len);
/*            uint8_t send_array[send_array_len];*/
            memset(send_array, 0, send_array_len);
            send_array[0] = MESS_OP;
            send_array[2] = nick_len;
            send_array[4] = more_header[0];
            send_array[5] = more_header[1];
            memcpy(&send_array[12], message, mess_len);
            memcpy(&send_array[12 + mess_size], cli->nick, nick_len);
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
                    
                    uint8_t *send_array_copy = malloc(send_array_len);
                    memcpy(send_array_copy, send_array, send_array_len);
                    pdu_data *msg_pdu = malloc(sizeof(pdu_data));
                    msg_pdu->len = send_array_len;
                    msg_pdu->pdu = send_array_copy;
                    
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
            free(send_array);
            // unlock
            pthread_mutex_unlock(&clients_mutex);
            break;
        case CHNICK_OP:
            ; /* Dummy line to please gcc */
            // check header
            uint8_t new_len = header[1];
            size_t new_pad = pad_length(new_len);
            size_t new_size = new_len + new_pad;
            uint8_t *new_nick = malloc(new_size);
/*            uint8_t new_nick[new_size];*/
            // recv rest (not if len 0)
            err = recv(cli->sockfd, new_nick, new_size, MSG_WAITALL);
            if (0 == err && new_size > 0) /* Client disconnected */
            {
                communicating = 0;
                break; /* Exit thread */
            }
            else if (err < 0)
            {
                perror("recv (in new nick)");
                communicating = 0; /* Terminate client connection */
/*                free(new_nick);*/
                break;
            }
            // verify
/*            uint8_t new[4 + new_size];*/
            uint8_t *new = malloc(4 + new_size);
            memcpy(&new[0], header, sizeof(header));
            memcpy(&new[4], new_nick, new_size);
            if (!verify_chnick(new, 4 + new_size))
            {
                //errmsg
                communicating = 0; /* Terminate client connection */
                free(new);
                break;
            }
            free(new);
            // join / memcpy? to send_array?
            size_t uchnick_len = 8 + nick_size + new_size;
            uint8_t *uchnick = malloc(uchnick_len);
/*            uint8_t send_array[send_array_len];*/
            memset(uchnick, 0, uchnick_len);
            uchnick[0] = UCHNICK_OP;
            uchnick[1] = nick_len;
            uchnick[2] = new_len;
            memcpy(&uchnick[8], cli->nick, nick_len);
            memcpy(&uchnick[8 + nick_size], new_nick, new_len);
            nick_len = new_len;
            nick_pad = new_pad;
            nick_size = new_size;
            // lock
            pthread_mutex_lock(&clients_mutex);
            /* Change nick */
            free(cli->nick);
            cli->nick = malloc(new_len + 1);
            if (NULL == cli->nick)
            {
                perror("malloc (new nick)");
                communicating = 0; /* Terminate client connection */
                break;
            }
            err = snprintf(cli->nick, new_len + 1, "%s", new_nick);
            if (err < 0) // ignore if trunc (expected)
            {
                perror("snprintf (new nick)");
                communicating = 0; /* Terminate client connection */
                break;
            }
            free(new_nick);
            // create / build uchnick
            uint32_t unix_time2 = htonl(time(NULL));
            memcpy(&uchnick[4], &unix_time2, sizeof(unix_time2));
            // enqueue + signal loop
            for (int i = 0; i < 255; i++)
            {
                if (NULL != clients[i])
                {
                    uint8_t *uchnick_copy = malloc(uchnick_len);
                    memcpy(uchnick_copy, uchnick, uchnick_len);
                    pdu_data *uchnick_pdu = malloc(sizeof(pdu_data));
                    uchnick_pdu->len = uchnick_len;
                    uchnick_pdu->pdu = uchnick_copy;
                    enqueue(clients[i], uchnick_pdu);
                }
            }
            free(uchnick);
            // unlock
            pthread_mutex_unlock(&clients_mutex);
            break;
        case QUIT_OP:
            // check header else msg?
            if (0 != header[1] || 0 != header[2] || 0 != header[3])
            {
                //errmsg
            }
            communicating = 0; /* Exit thread */
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

        /* Simple spam filter */
        spam_time2 = time(NULL);
        if ((spam_time2 - spam_time) > 0)
        {
            spam_count = 0;
        }
        else
        {
            spam_count++;
        }
        if (spam_count > 4) /* If 5 or more messages in one second */
        {
            // spam msg
            printf("SPAMMMMMM\n\n\n");
            break; /* Terminate client connection */
        }
        spam_time = spam_time2;
    }
    // kill sender
    // remove_client()?????????

    /* Close the connection, free all thread resources and exit thread */

    return;
}

