#include <SPI.h>
#include <Ethernet2.h>

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

#define CONDAST_URL "www.condast.com"
//#define CONDAST_PORT 8080

//#define LOCAL_HOST "localhost"
#define CONDAST_PORT 10080

#define MAC { 0x90, 0xA2, 0xDA, 0x11, 0x04, 0x17 }
#define CONTEXT "/miip2017/sa/"

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

#define HOST "Host: "
#define CONNECTION_CLOSE "Connection: close"
#define HTTP_11 " HTTP/1.1";

// Enter a MAC address for your controller below.
// Newer Ethernet shields have a MAC address printed on a sticker on the shield
byte mac[] = MAC;//e.g { 0x90, 0xA2, 0xDA, 0x11, 0x12, 0x3A };

// Initialize the Ethernet client library
// with the IP address and port of the server
// that you want to connect to (port 80 is default for HTTP):
EthernetClient client;

enum request {
  SETUP,
  LOG,
  RADAR
};

void setup_Web() {

  // start the Ethernet connection:
  Serial.println( "SETUP WEB CLIENT "); 
  if (Ethernet.begin(mac) == 0) {
    Serial.println("Failed to configure Ethernet using DHCP");
    // try to congifure using IP address instead of DHCP:
    Ethernet.begin(mac, ip);
  }
  // give the Ethernet shield a second to initialize:
  Serial.println("WEB CLIENT...");
  delay(1000);
  Serial.println("connecting...");
  connecting();
}

boolean isconnected;

boolean connecting() {
  // if you get a connection, report back via serial:

  int result = (client.connect(server, CONDAST_PORT));
  //Serial.print( "Connected: ");
  //Serial.println( result );
  if ( isconnected && ( result < 0)) {
    Serial.println("Connection failed: " + result);
  }
  isconnected = ( result >= 0);
  return isconnected;
}

void disconnecting() {
  isconnected = false;
  client.stop();
}

/**
   Send a request setup message
*/
String requestSetup( String name, int token ) {
  connecting();
  String str = sendHttp( SETUP, name, token, "" );
  Serial.println( "SETUP: " );
  Serial.println( str );
  disconnecting();
  return str;
}

/**
 * send a log message
 */
String logMessage( String message ){
  connecting();
  String str = sendHttp( LOG, NAME, TOKEN, message );
  disconnecting();
  boolean logger = str.equals( "true");
  Serial.println( "logging: " + str );
  setLogger( logger );
  return str;  
}

String requeststr( int request ) {
  String retval = "update";
  switch ( request ) {
    case SETUP:
      retval = "setup";
      break;
    case RADAR:
      retval = "radar";
      break;
    default:
      break;
  }
  return retval;
}

String sendHttp( int request, String id, int token, String msg ) {
  if (!isconnected )
    return "";
  // Make a HTTP request:
  String str = "GET ";
  str += CONTEXT;
  str +=  requeststr( request );
  str += "?id=";
  str += id;
  str += "&token=";
  str += token;
  if ( msg.length() > 0 ) {
    str += "&";
    str += msg;
  }
  str += HTTP_11 ;
  //Serial.println( str );
  client.println( str);

  str = HOST;
  str += CONDAST_URL;
  client.println( str);
  client.println( CONNECTION_CLOSE );
  client.println();
  //Serial.println( str );
  return processRequest();
}

String processRequest() {
  // Serial.print(" PROCESSING: ");
  //Serial.print( client.available() );
  String retval = "";

  //Extra check for wireless. Sometimes the connection is pending
  int counter = 0;
  while ( !client.available() && ( counter < 2500)) {
    counter++;
  }
  //  Serial.println();
  while (client.available()) {
    char c = client.read();
    retval += c;

    // if the server's disconnected, stop the client:
    if (!client.connected()) {
      //Serial.println();
      if (!isconnected ) {
        Serial.println("disconnecting.");
        isconnected = false;
      }
      client.stop();
      break;
    }
  }
  //Serial.println( retval );
  int json = retval.indexOf("[");
  if ( json <= 0) {
    //Serial.println( "DONE" );
    return retval;
  }
  retval = retval.substring( json, retval.length());
  //Serial.println( "DONE" );
  return retval;
}

void loop_Web() {
  // if there are incoming bytes available
  // from the server, read them and print them:
  if (!client.connected()) {
    connecting();
  }
}
