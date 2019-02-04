//
// Created by tal on 21/12/18.
//

#include "../include/Translator.h"

std::string Translator::opcodeTranslate(short &opcode) {
    std::string ans;
    if (opcode == 1) ans = "REGISTER";
    else if (opcode == 2) ans = "LOGIN";
    else if (opcode == 3) ans = "LOGOUT";
    else if (opcode == 4) ans = "FOLLOW";
    else if (opcode == 5) ans = "POST";
    else if (opcode == 6) ans = "PM";
    else if (opcode == 7) ans = "USERLIST";
    else if (opcode == 8) ans = "STAT";
    else if (opcode == 9) ans = "NOTIFICATION";
    else if (opcode == 10) ans = "ACK";
    else if (opcode == 11) ans = "ERROR";
    return ans;
}

short Translator::commandTranslate(std::string &command) {
    short ans;
    if (command == "REGISTER") ans = 1;
    else if (command == "LOGIN") ans = 2;
    else if (command == "LOGOUT") ans = 3;
    else if (command == "FOLLOW") ans = 4;
    else if (command == "POST") ans = 5;
    else if (command == "PM") ans = 6;
    else if (command == "USERLIST") ans = 7;
    else if (command == "STAT") ans = 8;
    else if (command == "NOTIFICATION") ans = 9;
    else if (command == "ACK") ans = 10;
    else if (command == "ERROR") ans = 11;
    return ans;
}
