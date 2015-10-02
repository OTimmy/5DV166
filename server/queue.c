/*
 * queue.c
 * Written by Joakim Sandman, September 2015.
 *
 * queue.c supplies the implementation of a queue (as a directed linked list).
 *
 * Last update: 28/9-15.
 * Version: v0.5.
 * Version notes:
 * Todo: Names (package/lib prefix, snake_case), safety checks.
 */

#include <stdlib.h>
#include <stdbool.h>

#include "queue.h"

/* ============ Interface ============ */

/*
 * queue_empty: Creates a new empty queue.
 * Params:
 * Returns: Pointer to the new queue.
 * Notes: The queue must be deallocated with queue_free.
 */
queue *queue_empty(void)
{
    queue *q = malloc(sizeof(queue));
    q->front = malloc(sizeof(sgl_link));
    q->front->value = NULL;
    q->front->next = NULL;
    q->back = q->front;
    q->free_func = NULL;
    return q;
}

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
void queue_setFreeFunc(queue *q, free_data_func f)
{
    q->free_func = f;
    return;
}

/*
 * queue_isEmpty: Checks if a queue is empty.
 * Params: q = pointer to queue.
 * Returns: TRUE if the queue is empty, FALSE otherwise.
 * Notes:
 */
bool queue_isEmpty(queue *q)
{
    return (NULL == q->front->next);
}

/*
 * queue_enqueue: Enqueues a new link at the back of the queue, with the
 *      given data.
 * Params: q = pointer to queue.
 *         d = data to enqueue.
 * Returns:
 * Notes:
 */
void queue_enqueue(queue *q, data d)
{
    queue_pos newlink = malloc(sizeof(sgl_link));
    newlink->value = d;
    newlink->next = NULL;
    q->back->next = newlink;
    q->back = newlink;
    return;
}

/*
 * queue_dequeue: Dequeues a link from the front of the queue.
 * Params: q = pointer to queue.
 * Returns:
 * Notes: Only deallocates the link unless a memory handler has been set, in
 *        which case the data will be freed as well.
 */
void queue_dequeue(queue *q)
{
    queue_pos oldlink = q->front->next;
    q->front->next = q->front->next->next;
    if (NULL != q->free_func)
    {
        q->free_func(oldlink->value);
    }
    free(oldlink);
    return;
}

/*
 * queue_front: Gets the data from the front of the queue.
 * Params: q = pointer to queue.
 * Returns: The data from the front of the queue.
 * Notes:
 */
data queue_front(queue *q)
{
    return q->front->next->value;
}

/*
 * queue_free: Deallocates the memory of the queue.
 * Params: q = pointer to queue.
 * Returns:
 * Notes: Only deallocates the links unless a memory handler has been set, in
 *        which case the data of each link will be freed as well.
 */
void queue_free(queue *q)
{
    if (NULL == q)
    {
        return;
    }
    queue_pos nextlink = q->front->next;
    queue_pos currlink;
    if (NULL != q->free_func)
    {
        while (NULL != nextlink)
        {
            currlink = nextlink;
            nextlink = currlink->next;
            q->free_func(currlink->value);
            free(currlink);
        }
    }
    else
    {
        while (NULL != nextlink)
        {
            currlink = nextlink;
            nextlink = currlink->next;
            free(currlink);
        }
    }
    free(q->front);
    free(q);
    return;
}

