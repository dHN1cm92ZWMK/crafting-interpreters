CC=gcc
CFLAGS=-I.
DEPS = main.h

%.o: %.c $(DEPS)
	$(CC) -c -o $@ $< $(CFLAGS)

main: main.o
	$(CC) -o main main.o
