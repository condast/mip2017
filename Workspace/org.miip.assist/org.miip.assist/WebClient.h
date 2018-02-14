#ifndef WebClient_h
#define WebClient_h

#include "Arduino.h"
#include <SPI.h>
#include <Ethernet2.h>

#define CONDAST_URL "www.condast.com"
//#define CONDAST_PORT 8080

//#define LOCAL_HOST "localhost"
#define CONDAST_PORT 10080

#define MAC { 0x90, 0xA2, 0xDA, 0x11, 0x04, 0x17 }
#define CONTEXT "/miip2017/sa/"


class WebClient {
    enum request {
      REQ_SETUP = 0,
      REQ_LOG = 1,
      REQ_RADAR = 2
    };

  public: WebClient( String id, int token );
    /**
       Pixel Data object
    */
    struct PixelData {
      int index;
      boolean end;
      int choice;
      int options;
    };

    /**
       Radar Data object
    */
    struct RadarData {
      byte red;
      byte green;
      byte blue;
      byte index;
      byte transparency;
    };

    PixelData requestSetup();
    boolean requestLog();
    RadarData requestRadar( int leds );
    boolean logMessage( String message );
    void setup_Web();
    void loop_Web();
  private: char id[30];
    char token[6] = {'\0'};
    boolean update( JsonObject& root );

    // Initialize the Ethernet client library
    // with the IP address and port of the server
    // that you want to connect to (port 80 is default for HTTP):
    EthernetClient client;
    boolean connecting();
    void disconnecting();
    boolean sendHttp( int request, boolean post, String msg );
    void requeststr( int request );
    void printResponse();
    PixelData getPixelData();
    String urldecode(String str);
    String urlencode(String str);
    unsigned char h2int(char c);
};

#endif
