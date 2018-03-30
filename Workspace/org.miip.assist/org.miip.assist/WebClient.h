#ifndef WebClient_h
#define WebClient_h

#include "Arduino.h"
#include <SPI.h>
#include <Ethernet.h>

#define ID F("org.miip2017.human.assist")
#define TOKEN 4096
#define CONDAST_URL F("www.condast.com")

/*
  Web client

  This sketch connects to a website (http://www.google.com)
  using an Arduino Wiznet Ethernet shield.

  Circuit:
   Ethernet shield attached to pins 10, 11, 12, 13

  created 18 Dec 2009
  by David A. Mellis
  modified 9 Apr 2012
  by Tom Igoe, based on work by Adrian McEwen
*/

//SERVER
// Set the static IP address to use if the DHCP fails to assign
//char server[] = CONDAST_URL;    // name address for Google (using DNS)
//IPAddress ip(79, 170, 90, 5);

// Netgear / thuis
//IPAddress server(10, 0, 0, 5);
//IPAddress ip(10, 0, 0, 5);

//RH Marine Werkplaats
//IPAddress server(192, 168, 10, 100);
//IPAddress ip(192, 168, 10, 100);

IPAddress server(10, 30, 8, 74); // de Stadstuin / kantoor
IPAddress ip(10, 30, 8, 74); // de Stadstuin / kantoor

//IPAddress server(192, 168, 2, 104); // Kenniscentrum WiFi RH Marine kas
//IPAddress ip(192, 168, 0, 177);

#define HOST F("Host: ")
#define CONNECTION_CLOSE F("Connection: close")
#define HTTP_11 F(" HTTP/1.1")
#define ACCEPT F("Accept: */*")
#define CONTEXT_LENGTH F("Content-Length: ")
#define CONTENT_TYPE F("Content-Type: application/x-www-form-urlencoded ; charset=UTF-8")

//#define CONDAST_PORT 8080

//#define LOCAL_HOST "localhost"
#define CONDAST_PORT 10080

#define MAC { 0x90, 0xA2, 0xDA, 0x11, 0x04, 0x17 }
#define CONTEXT F("/miip2017/sa/")

const unsigned long HTTP_TIMEOUT = 10000;// max respone time from server

class WebClient {
  public: WebClient();
    enum request {
      UNKNOWN = 0,
      SETUP = 1,
      LOG = 2,
      RADAR = 3
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

    boolean requestLog();
    RadarData requestRadar( int leds );
    boolean logMessage( String message );
    void setup();
    void loop();
    String host;
    int port;
    String context;
    String id;
    int token;

    // Enter a MAC address for your controller below.
    // Newer Ethernet shields have a MAC address printed on a sticker on the shield
    byte mac[6] = {0};

    // Initialize the Ethernet client library
    // with the IP address and port of the server
    // that you want to connect to (port 80 is default for HTTP):
    EthernetClient client;
    boolean connect();
    void disconnect();
    boolean sendHttp( int request, String msg );
    boolean sendHttp( int request, boolean post, String msg );
    String requestStr( int request );
    void requestService( int request );
    bool processResponse( int request );
    String urldecode(String str);
    String urlencode(String str);
    unsigned char h2int(char c);
};

#endif
