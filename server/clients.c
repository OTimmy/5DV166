/*
 * clients.c
 * Written by Joakim Sandman, October 2015.
 * Last update: 9/11-15.
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
 *      PDU is late or faulty, in which case the connection is closed.
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

    /* Initialize client structure data */
    pthread_mutex_init(&cli->queue_mutex, NULL);
    pthread_cond_init(&cli->queue_cond, NULL);
    cli->send_queue = queue_empty();
    cli->nick = NULL;

    /* Create thread for handling the client output queue */
    pthread_t thread_oq;
    if (0 != pthread_create(&thread_oq, NULL, handle_client_output,
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
                /* Read and verify JOIN PDU */
                size_t nick_len = header[1];
                size_t pad = pad_length(nick_len);
                size_t join_len = 4 + nick_len + pad;
                uint8_t join[join_len]; /* JOIN PDU buffer */
                /* Wait for rest of pdu, this could block indefinitely and
                   cause "hidden clients" that use resources (e.g. threads)
                   without properly joining the server */
                err = recv(cli->sockfd, join, join_len, MSG_WAITALL);
                if (0 == err) /* Client disconnected */
                {
                    break; /* Exit thread */
                }
                else if (err < 0)
                {
                    perror("recv (nick)");
                    break; /* Terminate client connection */
                }
                if (!verify_join(join, join_len))
                {
                    enqueue(cli, server_mess("Invalid JOIN PDU!"
                                             " (faulty padding or no name)"));
                    break; /* Terminate client connection */
                }

                /* Initiate and set nick in client */
                cli->nick = malloc(nick_len + 1);
                if (NULL == cli->nick)
                {
                    perror("malloc (nick)");
                    exit(EXIT_FAILURE);
                }
                err = snprintf(cli->nick, nick_len + 1, "%s", &join[4]);
                if (err < 0) /* Ignore potential truncation (expected) */
                {
                    perror("snprintf (nick)");
                    break; /* Terminate client connection */
                }

                /* Check if nick is already used */
                if (nick_used(cli->nick))
                {
                    enqueue(cli, server_mess("Nick taken, try another."));
                    break; /* Terminate client connection */
                }

                /* Add client to server */
                pdu_data *nicks_pdu = get_nicks_pdu(cli);
                if (NULL == nicks_pdu)
                {
                    break; /* Terminate client connection */
                }
                enqueue(cli, nicks_pdu);
                if (!add_client(cli))
                {
                    enqueue(cli, server_mess("Server is full!"));
                    break;
                }

                /* Listen to client */
                handle_client_input(cli);
                /* Terminating client */
                remove_client(cli);
                err = 0; /* To prevent error message */
                break;
            }
            else /* Not a JOIN PDU */
            {
                enqueue(cli, server_mess("Incorrect JOIN OP code!"));
                break; /* Terminate client connection */
            }
        }
    }
    if (0 != err) /* Terminate client connection */
    {
        enqueue(cli, server_mess("Error while joining!"));
        enqueue(cli, &quit_pdu);
    }
    if (0 != pthread_join(thread_oq, NULL)) /* Wait for output thread to end */
    {
        fprintf(stderr, "ERROR: Failed to join with thread oq!\n");
    }

    /* Close the connection, free all thread resources and exit thread */
    shutdown(cli->sockfd, SHUT_WR); /* Kinda flushes the socket output */
    close(cli->sockfd);
    free(cli->nick);

    queue_setFreeFunc(cli->send_queue, free_pdu_data);
    pthread_mutex_lock(&cli->queue_mutex);
    queue_free(cli->send_queue);
    pthread_mutex_unlock(&cli->queue_mutex);

    pthread_mutex_destroy(&cli->queue_mutex);
    pthread_cond_destroy(&cli->queue_cond);
    free(cli);
    return NULL;
}

/*
 * free_pdu_data: Deallocates the dynamically allocated memory of pd.
 * Params: pd = pdu_data struct to deallocate.
 * Returns:
 * Notes: Handles NULL input.
 */
void free_pdu_data(void *pd)
{
    if (NULL != pd)
    {
        free(((pdu_data *) pd)->pdu);
        free((pdu_data *) pd);
    }
    return;
}

/*
 * handle_client_output: Waits for data to be put into the output queue,
 *      whereupon it is retrieved, sent to the client and deallocated.
 *      If a QUIT PDU is sent, the function terminates.
 * Params: thread_data_oq = client pointer containing the following values.
 *      sockfd = integer with the socket file descriptor for communicating
 *               with the client.
 *      send_queue = queue containing the pdu_data structs containing the data
 *                   to send.
 *      queue_mutex = mutex for synchronizing access to the queue.
 *      queue_cond = condition variable for synchronizing access to the queue.
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

    for EVER
    {
        send_data = dequeue(cli); /* Blocking call */
        if (NULL != send_data)
        {
            send_array = send_data->pdu;
            err = send(cli->sockfd, send_array, send_data->len, MSG_NOSIGNAL);
            if (QUIT_OP == send_array[0])
            {
                break; /* Exit thread */
            }
            else
            {
                free(send_array);
                free(send_data);
            }
            if (err < 0)
            {
                perror("send (out)");
                shutdown(cli->sockfd, SHUT_RDWR); /* Wake input thread */
                break; /* Exit thread */
            }
        }
    }
    return NULL;
}

/*
 * handle_client_input: Waits for data to be read from the client, whereupon it
 *      acts according to what data was received. If a MESS or CHNICK PDU is
 *      received, the respective MESS or UCHNICK PDUs are propagated to all
 *      connected clients. 
 *      If a QUIT or faulty PDU is received or if the client closes its side of
 *      the socket connection, the function terminates.
 * Params: cli = client pointer containing the following values.
 *      sockfd = integer with the socket file descriptor for communicating
 *               with the client.
 *      send_queue = queue containing the pdu_data structs containing the data
 *                   to send.
 *      queue_mutex = mutex for synchronizing access to the queue.
 *      queue_cond = condition variable for synchronizing access to the queue.
 * Returns:
 * Notes: Implements a rudimentary spam filter.
 */
void handle_client_input(client *cli)
{
    int err;
    int communicating = 1;
    uint8_t header[4] = {0}; /* PDU header */
    uint8_t nick_len = strlen(cli->nick);
    size_t nick_pad = pad_length(nick_len);
    size_t nick_size = nick_len + nick_pad;
    int spam_count = 0;
    uint32_t spam_time = time(NULL);
    uint32_t spam_time2;

    while (communicating)
    {
        /* Wait for PDU header */
        err = recv(cli->sockfd, header, sizeof(header), MSG_WAITALL);
        if (0 == err) /* Client disconnected */
        {
            break; /* Exit thread */
        }
        else if (err < 0)
        {
            perror("recv (in)");
            break; /* Terminate client connection */
        }
        switch (header[0]) /* Check OP code */
        {
        case MESS_OP:
            ; /* Dummy line to please gcc */
            /* Read rest of MESS header */
            uint8_t more_header[8] = {0}; /* Rest of MESS PDU header */
            err = recv(cli->sockfd, more_header, sizeof(more_header),
                       MSG_WAITALL);
            if (0 == err) /* Client disconnected */
            {
                communicating = 0; /* Exit thread */
                break;
            }
            if (err < 0)
            {
                perror("recv (in msg head)");
                communicating = 0; /* Terminate client connection */
                break;
            }

            /* Read rest of MESS PDU */
            uint16_t mess_len = (more_header[0] << 8) | (more_header[1] & 0xFF);
            size_t mess_pad = pad_length(mess_len);
            size_t mess_size = mess_len + mess_pad;
            uint8_t *message = malloc(mess_size);
            if (NULL == message)
            {
                perror("malloc (message)");
                exit(EXIT_FAILURE);
            }
            err = recv(cli->sockfd, message, mess_size, MSG_WAITALL);
            if (0 == err && mess_size > 0) /* Client disconnected */
            {
                communicating = 0; /* Exit thread */
                break;
            }
            else if (err < 0)
            {
                perror("recv (in msg)");
                communicating = 0; /* Terminate client connection */
                free(message);
                break;
            }

            /* Verify MESS PDU */
            uint8_t *mess = malloc(12 + mess_size);
            if (NULL == mess)
            {
                perror("malloc (mess)");
                exit(EXIT_FAILURE);
            }
            memcpy(&mess[0], header, sizeof(header));
            memcpy(&mess[4], more_header, sizeof(more_header));
            memcpy(&mess[12], message, mess_size);
            if (!verify_mess(mess, 12 + mess_size))
            {
                mass_server_kick_mess(cli->nick, "invalid MESS PDU.");
                communicating = 0; /* Terminate client connection */
                free(message);
                free(mess);
                break;
            }
            free(mess);

            /* Build MESS PDU to send */
            size_t send_array_len = 12 + mess_size + nick_size;
            uint8_t *send_array = malloc(send_array_len);
            if (NULL == send_array)
            {
                perror("malloc (send_array)");
                exit(EXIT_FAILURE);
            }
            memset(send_array, 0, send_array_len);
            send_array[0] = MESS_OP;
            send_array[2] = nick_len;
            send_array[4] = more_header[0];
            send_array[5] = more_header[1];
            memcpy(&send_array[12], message, mess_len);
            memcpy(&send_array[12 + mess_size], cli->nick, nick_len);
            free(message);
            pthread_mutex_lock(&clients_mutex); /* For total order */
            uint32_t unix_time = htonl(time(NULL));
            memcpy(&send_array[8], &unix_time, sizeof(unix_time));
            send_array[3] = get_checksum(send_array, send_array_len);

            /* Propagate message to all clients */
            for (int i = 0; i < 255; i++)
            {
                if (NULL != clients[i])
                {
                    uint8_t *send_array_copy = malloc(send_array_len);
                    memcpy(send_array_copy, send_array, send_array_len);
                    pdu_data *msg_pdu = malloc(sizeof(pdu_data));
                    msg_pdu->len = send_array_len;
                    msg_pdu->pdu = send_array_copy;
                    enqueue(clients[i], msg_pdu);
                }
            }
            free(send_array);
            pthread_mutex_unlock(&clients_mutex);
            break;
        case CHNICK_OP:
            ; /* Dummy line to please gcc */
            /* Read rest of CHNICK PDU */
            uint8_t new_len = header[1];
            size_t new_pad = pad_length(new_len);
            size_t new_size = new_len + new_pad;
            char *new_nick = malloc(new_size);
            if (NULL == new_nick)
            {
                perror("malloc (new_nick)");
                exit(EXIT_FAILURE);
            }
            err = recv(cli->sockfd, new_nick, new_size, MSG_WAITALL);
            if (0 == err && new_size > 0) /* Client disconnected */
            {
                communicating = 0; /* Exit thread */
                break;
            }
            else if (err < 0)
            {
                perror("recv (in new_nick)");
                communicating = 0; /* Terminate client connection */
                free(new_nick);
                break;
            }

            /* Verify CHNICK PDU */
            uint8_t *new = malloc(4 + new_size);
            if (NULL == new)
            {
                perror("malloc (new)");
                exit(EXIT_FAILURE);
            }
            memcpy(&new[0], header, sizeof(header));
            memcpy(&new[4], new_nick, new_size);
            if (!verify_chnick(new, 4 + new_size))
            {
                mass_server_kick_mess(cli->nick, "invalid CHNICK PDU.");
                communicating = 0; /* Terminate client connection */
                free(new_nick);
                free(new);
                break;
            }
            free(new);

            /* Check if nick is already used */
            if (nick_used(new_nick))
            {
                enqueue(cli, server_mess("Nick taken, try another."));
                free(new_nick);
                break;
            }

            /* Build UCHNICK PDU to send */
            size_t uchnick_len = 8 + nick_size + new_size;
            uint8_t *uchnick = malloc(uchnick_len);
            if (NULL == uchnick)
            {
                perror("malloc (uchnick)");
                exit(EXIT_FAILURE);
            }
            memset(uchnick, 0, uchnick_len);
            uchnick[0] = UCHNICK_OP;
            uchnick[1] = nick_len;
            uchnick[2] = new_len;
            memcpy(&uchnick[8], cli->nick, nick_len);
            memcpy(&uchnick[8 + nick_size], new_nick, new_len);
            pthread_mutex_lock(&clients_mutex); /* For total order */
            uint32_t unix_time2 = htonl(time(NULL));
            memcpy(&uchnick[4], &unix_time2, sizeof(unix_time2));

            /* Change nick */
            char *temp = cli->nick;
            cli->nick = malloc(new_len + 1);
            if (NULL == cli->nick)
            {
                perror("malloc (new nick)");
                exit(EXIT_FAILURE);
            }
            err = snprintf(cli->nick, new_len + 1, "%s", new_nick);
            if (err < 0) /* Ignore potential truncation (expected) */
            {
                perror("snprintf (new nick)");
                enqueue(cli, server_mess("Server failed to change nick!"));
                free(cli->nick);
                cli->nick = temp;
                free(new_nick);
            }
            else
            {
                free(new_nick);
                free(temp);
                nick_len = new_len;
                nick_pad = new_pad;
                nick_size = new_size;

                /* Propagate uchnick to all clients */
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
            }
            free(uchnick);
            pthread_mutex_unlock(&clients_mutex);
            break;
        case QUIT_OP:
            if (0 != header[1] || 0 != header[2] || 0 != header[3])
            {
                mass_server_kick_mess(cli->nick, "invalid QUIT PDU.");
            }
            communicating = 0; /* Exit thread */
            break;
        default:
            mass_server_kick_mess(cli->nick, "incorrect OP code.");
            communicating = 0; /* Terminate client connection */
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
        if (spam_count > 4) /* If 5 or more messages in the same second */
        {
            mass_server_kick_mess(cli->nick, "spamming!");
            break; /* Terminate client connection */
        }
        spam_time = spam_time2;
    }
    return;
}

