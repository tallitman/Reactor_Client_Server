cmake_minimum_required(VERSION 3.12)

project(DoubleThreadClient)


SET(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11 -pthread")
find_package(Boost 1.58.0 COMPONENTS system filesystem REQUIRED)
include_directories(${Boost_INCLUDE_DIRS})
add_executable(Client src/connectionHandler.cpp include/connectionHandler.h src/BGSclient.cpp src/Task.cpp include/Task.h src/Translator.cpp include/Translator.h src/EncoderDecoder.cpp include/EncoderDecoder.h)
target_link_libraries(Client ${Boost_LIBRARIES})
