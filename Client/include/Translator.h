//
// Created by tal on 21/12/18.
//

#ifndef DOUBLETHREADCLIENT_TRANSLATOR_H
#define DOUBLETHREADCLIENT_TRANSLATOR_H


#include <iostream>

class Translator {
private:

public:
    std::string opcodeTranslate(short& opcode);
    short commandTranslate(std::string& command);

};


#endif //DOUBLETHREADCLIENT_TRANSLATOR_H
