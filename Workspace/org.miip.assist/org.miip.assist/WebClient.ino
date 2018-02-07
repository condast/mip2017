#include <stdio.h>
#include <string.h>

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

// Enter a MAC address for your controller below.
// Newer Ethernet shields have a MAC address printed on a sticker on the shield
byte mac[] = MAC;//e.g { 0x90, 0xA2, 0xDA, 0x11, 0x12, 0x3A };


IPAddress server(10, 30, 8, 74); // de Stadstuin / kantoor
IPAddress ip(10, 30, 8, 74); // de Stadstuin / kantoor

//IPAddress server(192, 168, 2, 104); // Kenniscentrum WiFi RH Marine kas
//IPAddress ip(192, 168, 0, 177);

#define HOST "Host: "
#define CONNECTION_CLOSE "Connection: close"
#define HTTP_11 " HTTP/1.1"
#define ACCEPT "Accept: */*"
#define CONTEXT_LENGTH "Content-Length: "
#define CONTENT_TYPE "Content-Type: application/x-www-form-urlencoded ; charset=UTF-8"

WebClient::WebClient( String name, int tkn ) {
  strcpy( id, name.c_str() );
  sprintf( token, "%d", tkn );
}

void WebClient::setup_Web() {

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

boolean WebClient::connecting() {
  // if you get a connection, report back via serial:

  int result = (client.connect(server, CONDAST_PORT));
  //Serial.print( "Connected: "); Serial.println( result );
  if ( isconnected && ( result < 0)) {
    Serial.println("Connection failed: " + result);
  }
  isconnected = ( result >= 0);
  return isconnected;
}

void WebClient::disconnecting() {
  isconnected = false;
  client.println( CONNECTION_CLOSE );
  client.stop();
}

/**
   Send a request setup message
*/
WebClient::PixelData WebClient::requestSetup() {
  connecting();
  //Serial.println( "REQUEST SETUP" );
  boolean result = sendHttp( REQ_SETUP, false, "" );
  PixelData data;
  if ( !result )
    return data;
  //Serial.println( "SETUP RECEIVED: TRUE" );
  data = getPixelData();
  disconnecting();
  return data;
}

/**
   Retriieve the pixel data
*/
WebClient::PixelData WebClient::getPixelData() {
  size_t capacity = JSON_OBJECT_SIZE(5) + 40;
  DynamicJsonBuffer jsonBuffer(capacity);

  // Parse JSON object
  JsonObject& root = jsonBuffer.parseObject(client);
  PixelData data;
  if (!root.success()) {
    Serial.println(F("Parsing failed!"));
    jsonBuffer.clear();
    return data;
  }

  data.index = root["i"];
  data.end = root["e"];
  data.choice = root["ch"];
  data.options = root["o"];
  //Serial.print( "PIXEL DATA " ); Serial.println(data.options);
  jsonBuffer.clear();
  return data;
}

/**
    Send a request for radar data
*/
WebClient::RadarData WebClient::requestRadar( int leds ) {
  //Serial.print("TOKEN: " ); Serial.println( token );
  connecting();
  //Serial.print( "LEDS:" ); Serial.println( leds );
  boolean result = sendHttp( REQ_RADAR, false, String( leds ));
  RadarData data;
  if ( !result )
    return data;
  size_t capacity = JSON_OBJECT_SIZE(5) + 40;
  DynamicJsonBuffer jsonBuffer(capacity);

  // Parse JSON object
  JsonObject& root = jsonBuffer.parseObject(client);
  if (!root.success()) {
    Serial.println(F("Parsing failed!"));
    jsonBuffer.clear();
    disconnecting();
    return data;
  }
  data.index = root["a"];
  data.red = root["r"];
  data.green = root["g"];
  data.blue = root["b"];
  data.transparency = root["t"];
  //Serial.print( "RADAR DATA Available for index " ); Serial.println( data.index );
  jsonBuffer.clear();
  disconnecting();
  return data;
}

/**
   Send a request log message
*/
boolean WebClient::requestLog() {
  return logMessage("");
}

/**
   send a log message
*/
boolean WebClient::logMessage( String message ) {
  connecting();
  //Serial.print( "log request: "); Serial.println( message );
  boolean result = sendHttp( REQ_LOG, false, urlencode( message ));
  if ( !result )
    return false;
  PixelData data = getPixelData();
  disconnecting();
  return ( data.options && 1 ) > 0;
}

/**
   Translate to the correct REST path
*/
void WebClient::requeststr( int request ) {
  switch ( request ) {
    case REQ_RADAR:
      client.print( "radar" );
      break;
    case REQ_LOG:
      client.print( "log" );
      break;
    default:
      client.print( "setup" );
      break;
  }
}

boolean WebClient::sendHttp( int request, boolean post, String msg ) {
  if (!isconnected )
    return false;

  // Make a HTTP request:
  client.print( post ? "POST " : "GET ");
  client.print( CONTEXT );
  requeststr( request );
  client.print( "?id=" );
  client.print( id );
  client.print( "&token=");
  client.print( token );
  if ( !post && ( msg.length() > 0 )) {
    client.print( "&msg=" );
    client.print( msg );
  }
  client.print( HTTP_11 );
  //Serial.print( "SEND REQUEST: " ); Serial.println( send_str );
  client.println();

  client.print( HOST );
  client.print( CONDAST_URL);
  client.println( CONNECTION_CLOSE );
  if ( post && ( msg.length() > 0 )) {
    client.println( ACCEPT );
    client.println( CONTENT_TYPE );
    client.print( CONTEXT_LENGTH ); client.println( msg.length() );
    client.println();
    client.println( msg );
  } else if (client.println() == 0) {
    Serial.print("Failed to send request ");
    return false;
  }

  // Check HTTP status
  char status[32] = {0};
  client.readBytesUntil('\r', status, sizeof(status));
  if (strcmp(status, "HTTP/1.1 200 OK") != 0) {
    Serial.print(F("Unexpected response: "));
    Serial.println(status);
    return false;
  }

  // Skip HTTP headers
  char endOfHeaders[] = "\r\n\r\n";
  if (!client.find(endOfHeaders)) {
    Serial.print(F("Invalid response"));;
    return false;
  }
  return true;
}

void WebClient::printResponse() {
  while (client.available()) {
    char c = client.read();
    Serial.print(c);
  }
  Serial.println();
  Serial.flush();
}

/**
   Process the clients request. for the data and buffer size see:
   http://arduinojson.org/assistant/
*/
/*
  JsonObject& WebClient::processJsonRequest( int data, int buffer) {
  // Allocate JsonBuffer
  // Use arduinojson.org/assistant to compute the capacity.
  size_t capacity = JSON_OBJECT_SIZE(data) + buffer;
  DynamicJsonBuffer jsonBuffer(capacity);

  // Parse JSON object
  Serial.println( "PARSING ARRAY");
  JsonObject& root = jsonBuffer.parseObject(client);
  if (!root.success()){
    Serial.println(F("Parsing failed!"));
    jsonBuffer.clear();
  }
  return root;
  }
*/

/**
   Process the clients request. for the data and buffer size see:
   http://arduinojson.org/assistant/
*/
/*
  JsonArray& WebClient::processJsonArray( int size, int data, int buffer) {
  // Allocate JsonBuffer
  // Use arduinojson.org/assistant to compute the capacity.
  const size_t capacity = JSON_ARRAY_SIZE(size) + size * JSON_OBJECT_SIZE(data) + buffer;
  DynamicJsonBuffer jsonBuffer(capacity);

  // Parse JSON object
  JsonArray& root = jsonBuffer.parseArray(client, 0);
  root.prettyPrintTo( Serial );
  if (!root.success())
    Serial.println(F("Parsing array failed!"));

  return root;
  }
*/

void WebClient::loop_Web() {
  // if there are incoming bytes available
  // from the server, read them and print them:
  if (!client.connected()) {
    connecting();
  }
}

/*
  ESP8266 Hello World urlencode by Steve Nelson
  URLEncoding is used all the time with internet urls. This is how urls handle funny characters
  in a URL. For example a space is: %20
  These functions simplify the process of encoding and decoding the urlencoded format.

  It has been tested on an esp12e (NodeMCU development board)
  This example code is in the public domain, use it however you want.
  Prerequisite Examples:
  https://github.com/zenmanenergy/ESP8266-Arduino-Examples/tree/master/helloworld_serial
*/
String WebClient::urldecode(String str) {
  String encodedString = "";
  char c;
  char code0;
  char code1;
  for (int i = 0; i < str.length(); i++) {
    c = str.charAt(i);
    if (c == '+') {
      encodedString += ' ';
    } else if (c == '%') {
      i++;
      code0 = str.charAt(i);
      i++;
      code1 = str.charAt(i);
      c = (h2int(code0) << 4) | h2int(code1);
      encodedString += c;
    } else {

      encodedString += c;
    }
    yield();
  }

  return encodedString;
}

String WebClient::urlencode(String str)
{
  String encodedString = "";
  char c;
  char code0;
  char code1;
  char code2;
  for (int i = 0; i < str.length(); i++) {
    c = str.charAt(i);
    if (c == ' ') {
      encodedString += '+';
    } else if (isalnum(c)) {
      encodedString += c;
    } else {
      code1 = (c & 0xf) + '0';
      if ((c & 0xf) > 9) {
        code1 = (c & 0xf) - 10 + 'A';
      }
      c = (c >> 4) & 0xf;
      code0 = c + '0';
      if (c > 9) {
        code0 = c - 10 + 'A';
      }
      code2 = '\0';
      encodedString += '%';
      encodedString += code0;
      encodedString += code1;
      //encodedString+=code2;
    }
    yield();
  }
  return encodedString;

}

unsigned char WebClient::h2int(char c)
{
  if (c >= '0' && c <= '9') {
    return ((unsigned char)c - '0');
  }
  if (c >= 'a' && c <= 'f') {
    return ((unsigned char)c - 'a' + 10);
  }
  if (c >= 'A' && c <= 'F') {
    return ((unsigned char)c - 'A' + 10);
  }
  return (0);
}
