/*
 * queue.h
 * Written by Joakim Sandman, September 2015.
 *
 * queue.h supplies the interface for a queue (as a directed linked list).
 *
 * Last update: 28/9-15.
 * Version: v0.5.
 * Version notes:
 * Todo: Merge.
 */

#ifndef QUEUE_H_
#define QUEUE_H_

#include <stdbool.h>

/* ============ Data and function types ============ */

#ifndef DATA_
#define DATA_
/* Data (pointer) to store in queue. */
typedef void * data;
#endif

#ifndef FREE_DATA_FUNC_
#define FREE_DATA_FUNC_
/* Free function (pointer) that deallocates the dynamically allocated memory
   of the data. Must handle (ignore) unreadable (NULL) input. */
typedef void (*free_data_func)(data);
#endif

#ifndef SGL_LINK_
#define SGL_LINK_
/* Single direction link. */
typedef struct sgl_link {
    struct sgl_link *next;
    data value;
} sgl_link;
#endif

/* Queue (as a directed singly linked list). */
typedef struct {
    sgl_link *front; /* Sentinel link pointing to the front of the queue */
    sgl_link *back; /* Last element in the queue */
    free_data_func free_func;
} queue;

/* Position in queue. */
typedef sgl_link * queue_pos;

/* ============ Interface ============ */

/*
 * queue_empty: Creates a new empty queue.
 * Params:
 * Returns: Pointer to the new queue.
 * Notes: The queue must be deallocated with queue_free.
 */
queue *queue_empty(void);

/*
 * queue_setFreeFunc: Installs a memory handler for the queue, allowing it to
 *      free the data contained in the links when they are removed.
 * Params: q = pointer to queue.
 *         f = pointer to a function, taking 1 data argument, that deallocates
 *             the dynamically allocated memory of that data. Must handle
 *             (ignore) unreadable (NULL) input.
 * Returns:
 * Notes:
 */
void queue_setFreeFunc(queue *q, free_data_func f);

/*
 * queue_isEmpty: Checks if a queue is empty.
 * Params: q = pointer to queue.
 * Returns: TRUE if the queue is empty, FALSE otherwise.
 * Notes:
 */
bool queue_isEmpty(queue *q);

/*
 * queue_enqueue: Enqueues a new link at the back of the queue, with the
 *      given data.
 * Params: q = pointer to queue.
 *         d = data to enqueue.
 * Returns:
 * Notes:
 */
void queue_enqueue(queue *q, data d);

/*
 * queue_dequeue: Dequeues a link from the front of the queue.
 * Params: q = pointer to queue.
 * Returns:
 * Notes: Only deallocates the link unless a memory handler has been set, in
 *        which case the data will be freed as well.
 */
void queue_dequeue(queue *q);

/*
 * queue_front: Gets the data from the front of the queue.
 * Params: q = pointer to queue.
 * Returns: The data from the front of the queue.
 * Notes:
 */
data queue_front(queue *q);

/*
 * queue_free: Deallocates the memory of the queue.
 * Params: q = pointer to queue.
 * Returns:
 * Notes: Only deallocates the links unless a memory handler has been set, in
 *        which case the data of each link will be freed as well.
 */
void queue_free(queue *q);

#endif /* QUEUE_H_ */

