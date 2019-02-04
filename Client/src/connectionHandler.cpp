#include "../include/connectionHandler.h"
#include "../include/EncoderDecoder.h"

using boost::asio::ip::tcp;
using namespace std;
using std::cin;
using std::cout;
using std::cerr;
using std::endl;
using std::string;

//ConnectionHandler::ConnectionHandler(string host, short port) : host_(host), port_(port), io_service_(),
//                                                                socket_(io_service_) {
//
//}
ConnectionHandler::ConnectionHandler(string host, short port,EncoderDecoder& encoderDecoder) : host_(host), port_(port), io_service_(),
                                                                socket_(io_service_),connected(false),encoderDecoder(encoderDecoder) {

}


ConnectionHandler::~ConnectionHandler() {
    close();
}

bool ConnectionHandler::connect() {
    std::cout << "Starting connect to "
              << host_ << ":" << port_ << std::endl;
    try {
        tcp::endpoint endpoint(boost::asio::ip::address::from_string(host_), port_); // the server endpoint
        boost::system::error_code error;
        socket_.connect(endpoint, error);
        connected = true;
        if (error)
            throw boost::system::system_error(error);
    }
    catch (std::exception &e) {
        std::cerr << "Connection failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}

bool ConnectionHandler::getBytes(char bytes[], unsigned int bytesToRead) {
    size_t tmp = 0;
    boost::system::error_code error;
    try {
        while (!error && bytesToRead > tmp) {
            tmp += socket_.read_some(boost::asio::buffer(bytes + tmp, bytesToRead - tmp), error);
        }
        if (error)
            throw boost::system::system_error(error);
    } catch (std::exception &e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}

bool ConnectionHandler::sendBytes(const char bytes[], int bytesToWrite) {
    int tmp = 0;
    boost::system::error_code error;
    try {
        while (!error && bytesToWrite > tmp) {
            tmp += socket_.write_some(boost::asio::buffer(bytes + tmp, bytesToWrite - tmp), error);
        }
        if (error)
            throw boost::system::system_error(error);
    } catch (std::exception &e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}

bool ConnectionHandler::getLine(std::string &line) {
    char nextByte;
    bool ans=true;
    bool isDone=false;
    while(ans==true && !isDone){
        ans=getBytes(&nextByte, 1);
        isDone= encoderDecoder.decodeNextByte(line,nextByte);
    }
    return ans;


}

bool ConnectionHandler::sendLine(std::string &line) {
    vector<char> chars = encoderDecoder.encode(line);
    //char *bytesToSend = new char[chars.size()];
    char bytesToSend[chars.size()];
    int charsSize=chars.size();
    for (int i = 0; i < charsSize; i++) {
        bytesToSend[i] = chars.at(i);
    }

    bool ans= sendBytes(bytesToSend, chars.size());
   // delete bytesToSend;
    return ans;
}

bool ConnectionHandler::getFrameAscii(std::string &frame, char delimiter) {
    char ch;
    // Stop when we encounter the null character. 
    // Notice that the null character is not appended to the frame string.
    try {
        do {
            getBytes(&ch, 1);
            frame.append(1, ch);
        } while (delimiter != ch);
    } catch (std::exception &e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}

bool ConnectionHandler::sendFrameAscii(const std::string &frame, char delimiter) {

    bool result = sendBytes(frame.c_str(), frame.length());
    if (!result) return false;
    return sendBytes(&delimiter, 1);
}

// Close down the connection properly.
void ConnectionHandler::close() {
    try {
        socket_.close();
        connected = false;
    } catch (...) {
        std::cout << "closing failed: connection already closed" << std::endl;
    }

}

bool ConnectionHandler::isConnected() {
    return connected;
}



