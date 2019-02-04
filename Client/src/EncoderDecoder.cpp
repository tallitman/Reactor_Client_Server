//
// Created by tal on 23/12/18.
//

#include "../include/EncoderDecoder.h"
#include "../include/Translator.h"
#include <boost/lexical_cast.hpp>

EncoderDecoder::EncoderDecoder() : bytes(){

}


/**
 * encodes the given message to bytes array
 *
 * @param line the message to encode
 * @return the encoded bytes in vector char
 */
std::vector<char> EncoderDecoder::encode(std::string line) {
    std::vector<char> ans;
    short opcode = 0;
    std::string command = line.substr(0, line.find(' '));
    Translator translator;
    opcode = translator.commandTranslate(command);
    char bytesArr[2];
    shortToBytes(opcode, bytesArr);
    ans.push_back(bytesArr[0]);
    ans.push_back(bytesArr[1]);
    if (opcode == REGISTER || opcode == LOGIN || opcode == STAT)
        encodeStandart(line.substr(line.find(' ') + 1, line.size() - 1), ans);
    if (opcode == FOLLOW)
        encodeFollow(line.substr(line.find(' ') + 1, line.size() - 1), ans);
    if (opcode == POST )
        encodePost(line.substr(line.find(' ') + 1, line.size() - 1), ans);
    if( opcode == PM)
        encodePM(line.substr(line.find(' ') + 1, line.size() - 1), ans);
    return ans;

}

void EncoderDecoder::encodeStandart(std::string line, std::vector<char> &ans) {
    std::stringstream ss(line);
    std::string temp;
    bool sendAll = true;
    int counter = 0;
    while (getline(ss, temp, ' ') && sendAll) // delimiter as space
    {
        if (counter == 0 || counter == 1) {
            std::copy(temp.begin(), temp.end(), std::back_inserter(ans));
            ans.push_back('\0');
        }
    }
}


void EncoderDecoder::encodeFollow(std::string line, std::vector<char> &ans) {
    std::stringstream ss(line);
    std::string temp;
    bool sendAll = true;
    int counter = 0;
    while (getline(ss, temp, ' ') && sendAll) // delimiter as space
    {
        if (counter == 0) {
            std::copy(temp.begin(), temp.end(), std::back_inserter(ans));
            counter++;
        } else if (counter == 1) {
            short myShort = boost::lexical_cast<short>(temp);
           // char *bytesArr = new char[2];
            char bytesArr[2];
            shortToBytes(myShort, bytesArr);
            ans.push_back(bytesArr[0]);
            ans.push_back(bytesArr[1]);
           // delete bytesArr;
            counter++;
        } else if (counter > 1) {
            std::copy(temp.begin(), temp.end(), std::back_inserter(ans));
            ans.push_back('\0');
        }
    }
}

void EncoderDecoder::encodePost(std::string line, std::vector<char> &ans) {
    std::copy(line.begin(), line.end(), std::back_inserter(ans));
    ans.push_back('\0');
}

void EncoderDecoder::encodePM(std::string line, std::vector<char> &ans) {
    std::stringstream ss(line);
    std::string temp;
    int counter = 0;
    while (getline(ss, temp, ' ') && counter==0) // delimiter as space
    {
            std::copy(temp.begin(), temp.end(), std::back_inserter(ans));
            ans.push_back('\0');
            counter++;
    }
   line= line.substr(line.find(' ') + 1, line.size() - 1);
    std::copy(line.begin(), line.end(), std::back_inserter(ans));
    ans.push_back('\0');
}
/**
 * add the next byte to the decoding process
 *
 * @param nextByte the next byte to consider for the currently decoded
 * message
 * @return true if the message were valid.
 */
bool EncoderDecoder::decodeNextByte(std::string &line, char &nextByte) {
    bool ans = false;
    if (len < 2 && decodeOPCode == 0) {
        pushByte(nextByte);
        if (len == 2) {
            decodeOPCode = bytesToShort(bytes);
            bytes.clear();
            len = 0;
        }
    } else {
        switch (decodeOPCode) {
            case NOTIFICATION:
                ans = decodeNotification(line, nextByte);
                break;
            case ACK:
                ans = decodeAck(line, nextByte);
                break;
            case ERROR:
                ans = decodeError(line, nextByte);
                break;
            case 0:
                break;
        }
    }
    //reset for next operation
    if (ans == true) {
        decodeOPCode = 0;
        senderOpCode = -1;
        numOfUsers = -1;
        numOfPosts=-1;
        numFollowing=-1;
        counterDecodeOp = 0;

    }
    return ans;
}
bool EncoderDecoder::decodeNotification(std::string &line, char &nextByte) {
    if (len < 1 && senderOpCode == -1) {
        senderOpCode = nextByte;
        bytes.clear();
        len = 0;
        line.append("NOTIFICATION ");
        if (senderOpCode == 1)
            line.append("Public ");
        else
            line.append("PM ");
        return false;
    }

    if (counterDecodeOp < 2) {
        if (nextByte == '\0') {
            popString(line);
            counterDecodeOp++;
            if (counterDecodeOp == 2)
                return true;
        } else
            pushByte(nextByte);
    }
    return false;

}



bool EncoderDecoder::decodeAck(std::string &line, char &nextByte) {
    bool ans = false;
    if (len < 2 && senderOpCode == -1) {
        pushByte(nextByte);
        if (len == 2) {
            senderOpCode = bytesToShort(bytes);
            bytes.clear();
            len = 0;
            line.append("ACK ");
            line.append(boost::lexical_cast<std::string>(senderOpCode) + " ");
            if (senderOpCode != FOLLOW && senderOpCode != USERLIST && senderOpCode != STAT)
                return true;
        } else
            return ans;
    } else {
        switch ( senderOpCode) {
        case 4: case 7: //follow
               ans= decodeFollowUserlist(line, nextByte);
                break;
            case 8: {
                ans=decodeStat(line,nextByte);

                break;
            }
            default: {
                return true;
            }

        }
    }
    return ans;
}


bool EncoderDecoder::decodeError(std::string &line, char &nextByte) {
    if (len < 2 && senderOpCode == -1) {
        pushByte(nextByte);
        if (len == 2) {
            senderOpCode = bytesToShort(bytes);
            bytes.clear();
            len = 0;
            line.append("ERROR ");
            line.append(boost::lexical_cast<std::string>(senderOpCode) + " ");
            return true;
        }
        return false;
    }
    return false;
}

short EncoderDecoder::bytesToShort1(char *bytesArr) {
    short result = (short) ((bytesArr[0] & 0xff) << 8);
    result += (short) (bytesArr[1] & 0xff);
    return result;
}

short EncoderDecoder::bytesToShort(std::vector<char> bytesArr) {
    short result = (short) ((bytesArr.at(0) & 0xff) << 8);
    result += (short) (bytesArr.at(1) & 0xff);
    return result;
}

void EncoderDecoder::shortToBytes(short num, char *bytesArr) {
    bytesArr[0] = ((num >> 8) & 0xFF);
    bytesArr[1] = (num & 0xFF);
}

void EncoderDecoder::pushByte(char &nextByte) {
    bytes.push_back(nextByte);
    len++;
}

void EncoderDecoder::popString(std::string &line) {
    for (int i = 0; i < len; i++) {
        if (bytes.at(i) != '\0')
            line.append(1, bytes.at(i));
    }
    char ch = ' ';
    line.append(1, ch);
    bytes.clear();
    len = 0;
}

bool EncoderDecoder::decodeFollowUserlist(std::string &line, char &nextByte) {
    if (len < 2 && numOfUsers == -1) {
        pushByte(nextByte);
        if (len == 2) {
            numOfUsers = bytesToShort(bytes);
            bytes.clear();
            len = 0;
            line.append(boost::lexical_cast<std::string>(numOfUsers) + " ");
            if (numOfUsers == 0)
                return true;
        }

    } else {
        if (counterDecodeOp < numOfUsers) {
            if (nextByte == '\0') {
                popString(line);
                counterDecodeOp++;
                if (counterDecodeOp == numOfUsers)
                    return true;
            } else
                pushByte(nextByte);
        }
    }
    return false;
}



bool EncoderDecoder::decodeStat(std::string &line, char &nextByte) {
    if (len < 6 && decodeStatShort == -1) {
        pushByte(nextByte);
        if (len %2==0) {
            decodeStatShort = bytesToShort(bytes);
            bytes.clear();
            line.append(boost::lexical_cast<std::string>(decodeStatShort) + " ");
            decodeStatShort=-1;
            if(len==6) {
                len=0;
                return true;
            }
        }
    }

    return false;
}

