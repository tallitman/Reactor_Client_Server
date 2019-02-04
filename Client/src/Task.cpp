#include <iostream>
#include "../include/Task.h"
#include <boost/algorithm/string.hpp>
Task::Task (int id, ConnectionHandler& connectionHandler, std::mutex& mutex,std::condition_variable& cv): _id(id), connectionHandler(connectionHandler), _mutex(mutex),_cv(cv){}

//Send messages to server
void Task:: send() {
    while (true) {
        const short bufsize = 1024;
        char buf[bufsize];
        std::cin.getline(buf, bufsize);
        std::string line(buf);
        if(!connectionHandler.sendLine(line)) break;
        if(line=="LOGOUT"){
            std::unique_lock<std::mutex> lk(_mutex);
            _cv.wait(lk);
        }
        if (!connectionHandler.isConnected())
        {
            break;
        }
    }
}

//Receive incoming messages from server.
void Task:: receive(){
    while (true) {
        std::string incomingMessage;
        if (!connectionHandler.getLine(incomingMessage)) {
            break;
        }
        std::cout << incomingMessage << std::endl;
        boost::trim_right(incomingMessage);
        std::unique_lock<std::mutex> lk(_mutex);
        if (incomingMessage == "ACK 3") {
            connectionHandler.close();
            lk.unlock();
            _cv.notify_all();
            break;
        }
        lk.unlock();
        _cv.notify_all();
    }
}
