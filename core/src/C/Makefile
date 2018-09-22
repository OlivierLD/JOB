TARGET := libjob-io.so
OBJS := job.o
CC := gcc

# prefix with -m32 to compile for linux32
CFLAGS := -std=gnu99 -fPIC -g -ffast-math
CFLAGS += -I$(shell dirname $(shell realpath $(shell which javac)))/../include
CFLAGS += -I$(shell dirname $(shell realpath $(shell which javac)))/../include/linux
# CFLAGS += -I$(shell dirname $(shell realpath $(shell which javac)))/../../../Headers
LDFLAGS := -shared

$(TARGET): $(OBJS)
	$(CC) $(CFLAGS) $(LDFLAGS) $^ -o $@

job.h:
	javah -classpath ../../build/classes/main -o job.h job.io.JOBNativeInterface

clean:
	rm -f $(TARGET) $(OBJS)

.PHONY: job.h clean
