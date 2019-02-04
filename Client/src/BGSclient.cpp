#include <iostream>
#include <thread>
#include <mutex>

#include "../include/connectionHandler.h"
#include "../include/Task.h"
#include "../include/EncoderDecoder.h"
int main (int argc, char *argv[]) {
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);
    EncoderDecoder encoderDecoder;
    ConnectionHandler connectionHandler(host, port,encoderDecoder);
    if (!connectionHandler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }

    std::mutex mutex;
    std::condition_variable cv;
    Task readFromServer(1, connectionHandler,mutex,cv);
    Task sendToServer(2, connectionHandler,mutex,cv);
    std::thread inThread(&Task::receive, &readFromServer);
    std::thread outThread(&Task::send, &sendToServer);
    //close threads
    inThread.join();
    outThread.join();
    return 0;
}
