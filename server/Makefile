
# Makefile
# Written by Joakim Sandman, September 2015.
# Last update: 1/10-15.
#
# Makefile compiles the program 'server'.
# Run: make
# Clean: make clean

CC = gcc
CFLAGS = -std=gnu99 -Wall -pedantic -g -pthread
LDFLAGS = -lpthread -lrt

OBJ = server.o globals.o pdu.o name_server.o doorman.o clients.o queue.o

all: $(OBJ)
	$(CC) $(LDFLAGS) -o server $(OBJ)

server.o: server.c server.h
	$(CC) $(CFLAGS) -c server.c

globals.o: globals.c globals.h
	$(CC) $(CFLAGS) -c globals.c

pdu.o: pdu.c pdu.h
	$(CC) $(CFLAGS) -c pdu.c

name_server.o: name_server.c name_server.h
	$(CC) $(CFLAGS) -c name_server.c

doorman.o: doorman.c doorman.h
	$(CC) $(CFLAGS) -c doorman.c

clients.o: clients.c clients.h
	$(CC) $(CFLAGS) -c clients.c

queue.o: queue.c queue.h
	$(CC) $(CFLAGS) -c queue.c

.PHONY: clean
clean:
	rm -f *.o server

