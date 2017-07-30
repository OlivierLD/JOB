TARGET := libjavahard-io.so
OBJS := jhard.o
CC := gcc

# prefix with -m32 to compile for linux32
CFLAGS := -std=gnu99 -fPIC -g -ffast-math
CFLAGS += -I$(shell dirname $(shell realpath $(shell which javac)))/../include
CFLAGS += -I$(shell dirname $(shell realpath $(shell which javac)))/../include/linux
LDFLAGS := -shared

$(TARGET): $(OBJS)
	$(CC) $(CFLAGS) $(LDFLAGS) $^ -o $@

jhard.h:
	javah -classpath .. -o jhard.h jhard.io.JHardNativeInterface

clean:
	rm -f $(TARGET) $(OBJS)

.PHONY: jhard.h clean
