
# Makefile
# Written by Joakim Sandman, September 2015.
# Last update: 27/9-15.
#
# Makefile compiles the program 'server'.
# Run: make
# Clean: make clean

CC = gcc
CFLAGS = -std=gnu99 -Wall -pedantic -g -pthread
LDFLAGS = -lpthread -lrt

OBJ = server.o globals.o pdu.o name_server.o

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

#dlist.o: dlist.c dlist.h
#	$(CC) $(CFLAGS) -c dlist.c

.PHONY: clean
clean:
	rm -f *.o server

