//
// Created by tal on 23/12/18.
//

#ifndef DOUBLETHREADCLIENT_ENCODERDECODER_H
#define DOUBLETHREADCLIENT_ENCODERDECODER_H


#include <string>
#include <vector>


class EncoderDecoder {
public:
    EncoderDecoder();
    std::vector<char> encode(std::string line);
    bool decodeNextByte(std::string& line, char& nextByte);
private:
    short bytesToShort1(char* bytesArr);
    short bytesToShort(std::vector<char> bytesArr);
    void shortToBytes(short num, char* bytesArr);
    void popString(std::string& line);
    void encodeStandart(std::string line,std::vector<char>& ans);
    void encodeFollow(std::string line,std::vector<char>& ans);
    void encodePost(std::string line,std::vector<char>& ans);
    void encodePM(std::string line,std::vector<char>& ans);
    bool decodeNotification(std::string& line, char& nextByte);
    bool decodeAck(std::string& line, char& nextByte);
    bool decodeError(std::string& line, char& nextByte);
    bool decodeFollowUserlist(std::string& line, char& nextByte);
    bool decodeStat(std::string &basic_string, char &byte);
    void pushByte(char& nextByte);
    std::vector<char> bytes;
    //for decode use
    int len=0;
    short decodeOPCode=0;
    short senderOpCode=-1;
    short numOfUsers=-1;
    short numOfPosts=-1;
    short numFollowing=-1;
    short decodeStatShort=-1;
    const char del = '\0';
    int counterDecodeOp=0;
    const short REGISTER= 1;
    const short LOGIN= 2;
    const short LOGOUT= 3;
    const short FOLLOW= 4;
    const short POST= 5;
    const short PM= 6;
    const short USERLIST= 7;
    const short STAT= 8;
    static constexpr short NOTIFICATION= 9, ACK=10,ERROR= 11;



};


#endif //DOUBLETHREADCLIENT_ENCODERDECODER_H
