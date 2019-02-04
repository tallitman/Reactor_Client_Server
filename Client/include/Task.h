//
// Created by tal on 21/12/18.
//

#ifndef DOUBLETHREADCLIENT_TASK_H
#define DOUBLETHREADCLIENT_TASK_H
#include <iostream>
#include <mutex>
#include <condition_variable>
#include "../include/connectionHandler.h"


class Task{
private:
    int _id;
    ConnectionHandler& connectionHandler;
    std::mutex & _mutex;
    std::condition_variable& _cv;
    bool tryToLogout= false;
public:
    Task(int id, ConnectionHandler& connectionHandler, std::mutex& mutex,std::condition_variable& cv);
    void receive();
    void send();
};


#endif //DOUBLETHREADCLIENT_TASK_H
