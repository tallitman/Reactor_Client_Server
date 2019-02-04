CFLAGS  = -c -Wall -Weffc++ -std=c++11 -Iinclude
LDFLAGS:=-lboost_system -lpthread

all: BGSclient
	g++ -o bin/BGSclient bin/Task.o bin/connectionHandler.o bin/BGSclient.o bin/EncoderDecoder.o bin/Translator.o  $(LDFLAGS)
	@echo "Build success"

BGSclient: bin/connectionHandler.o bin/BGSclient.o  bin/Task.o bin/EncoderDecoder.o bin/Translator.o

bin/connectionHandler.o: src/connectionHandler.cpp
	g++ $(CFLAGS) -o bin/connectionHandler.o src/connectionHandler.cpp

bin/BGSclient.o: src/BGSclient.cpp
	g++ $(CFLAGS) -o bin/BGSclient.o src/BGSclient.cpp

bin/Task.o: src/Task.cpp
	g++ $(CFLAGS) -o bin/Task.o src/Task.cpp

bin/EncoderDecoder.o: src/EncoderDecoder.cpp
	g++ $(CFLAGS) -o bin/EncoderDecoder.o src/EncoderDecoder.cpp

bin/Translator.o: src/Translator.cpp
	g++ $(CFLAGS) -o bin/Translator.o src/Translator.cpp

.PHONY: clean
clean:
	rm -f bin/*
