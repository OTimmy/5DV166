
# Makefile
# Written by Joakim Sandman, September 2015.
# Last update: 8/9-15.
#
# Makefile compiles the program server.c.
# Run: make
# Clean: make clean

CC=gcc
CFLAGS=-Wall -pedantic -g -std=gnu99
LDFLAGS=-lpthread -lrt

all: server.o dlist.o
	$(CC) $(LDFLAGS) -o server server.o dlist.o

server.o: server.c
	$(CC) $(CFLAGS) -c server.c

dlist.o: dlist.c dlist.h
	$(CC) $(CFLAGS) -c dlist.c

clean:
	rm -f *.o server

