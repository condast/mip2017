#ifndef WebClient_h
#define WebClient_h

#include "Arduino.h"
#include <SPI.h>
#include <Ethernet2.h>

class WebClient {
    enum request {
      REQ_SETUP,
      REQ_LOG,
      REQ_RADAR
    };

  private: String id;
    int token;
    boolean update( JsonObject& root );

    // Initialize the Ethernet client library
    // with the IP address and port of the server
    // that you want to connect to (port 80 is default for HTTP):
    EthernetClient client;
    boolean connecting();
    void disconnecting();
    JsonObject& processJsonRequest( int data, int buffer);
    JsonArray& processJsonArray( int size, int data, int buffer);
    boolean sendHttp( int request, boolean post, String msg );
    String requeststr( int request );

  public: WebClient( String id, int token );
    JsonObject& requestSetup();
    JsonObject& requestLog();
    JsonArray&  requestRadar( int leds );
    JsonObject& logMessage( String message );
    void setup_Web();
    void loop_Web();
};

#endif
