#include <stdio.h>
#include <string.h>

WebClient::WebClient() {}

void WebClient::setup() {

  id = ID;
  token = TOKEN;
  host = CONDAST_URL;
  port = CONDAST_PORT;
  context = CONTEXT;
  // Enter a MAC address for your controller below.
  // Newer Ethernet shields have a MAC address printed on a sticker on the shield
  byte mac[] = MAC;//e.g { 0x90, 0xA2, 0xDA, 0x11, 0x12, 0x3A };

  Serial.print(F("SERVER ADDRESS:")); server.printTo( Serial );
  Serial.println();
  Serial.print(F("IP ADDRESS:")); server.printTo( Serial );

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
}

bool WebClient::connect() {
  //Serial.print(F("Connecting to: ")); Serial.print( server ); Serial.print(F(":")); Serial.println( port );
  bool result = client.connect(server, port);
  //Serial.print(F("Connected: ")); Serial.println( result );
  if ( !result) {
    Serial.print(F("Connection failed: ")); Serial.println( result );
    client.stop();
  }
  return result;
}

void WebClient::disconnect() {
  Serial.print(F("Disconnecting: "));
  client.stop();
  Serial.println(F("Complete "));
}

/**
    Send a request for radar data
*/
WebClient::RadarData WebClient::requestRadar( int leds ) {
  //Serial.print("TOKEN: " ); Serial.println( token );
  connect();
  //Serial.print( "LEDS:" ); Serial.println( leds );
  boolean result = sendHttp( RADAR, false, String( leds ));
  RadarData data;
  if ( !result ) {
    disconnect();
    return data;
  }
  size_t capacity = JSON_OBJECT_SIZE(5) + 40;
  DynamicJsonBuffer jsonBuffer(capacity);

  // Parse JSON object
  JsonObject& root = jsonBuffer.parseObject(client);
  if (!root.success()) {
    Serial.println(F("Parsing failed!"));
    jsonBuffer.clear();
    disconnect();
    return data;
  }
  data.index = root["a"];
  data.red = root["r"];
  data.green = root["g"];
  data.blue = root["b"];
  data.transparency = root["t"];
  //Serial.print( "RADAR DATA Available for index " ); Serial.println( data.index );
  jsonBuffer.clear();
  disconnect();
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
  connect();
  //Serial.print( "log request: "); Serial.println( message );
  boolean result = sendHttp( LOG, false, urlencode( message ));
  if ( !result ) {
    disconnect();
    return false;
  }
  //PixelData data = getPixelData();
  disconnect();
  return false;// ( data.options && 1 ) > 0;
}

/**
   Translate to the correct REST path
*/
String WebClient::requestStr( int request ) {
  String str;
  switch ( request ) {
    case RADAR:
      str = F("radar");
      break;
    case LOG:
      str = F("log");
      break;
    default:
      str = F("setup");
      break;
  }
  //Serial.print( "PREPARE REQUEST: " ); Serial.println( request ); Serial.println( " " ); Serial.println( req_str );
  return str;
}

void WebClient::requestService( int request ) {
  String str;
  switch ( request ) {
    case RADAR:
      client.print( F("radar"));
      break;
    case LOG:
      client.print( F("log"));
      break;
    default:
      client.print(F("setup"));
      break;
  }
  //Serial.print( "PREPARE REQUEST: " ); Serial.println( request ); Serial.println( " " ); Serial.println( req_str );
}

boolean WebClient::sendHttp( int request, String message ) {
  String msg = message;
  if ( msg.length() > 0 ) {
    msg = F("&msg=");
    msg += message;
  }
  return sendHttp( request, false, msg );
}

boolean WebClient::sendHttp( int request, boolean post, String attrs ) {
  if ( client.connected()) {
    Serial.print(F("REQUEST ")); requestStr( request ); Serial.print(F(" ")); Serial.print(attrs ); Serial.println();
    //logRequest( request, post, attrs );

    // Make a HTTP request:
    client.print( post ? F("POST ") : F( "GET "));
    client.print( context );
    requestService( request );
    client.print(F("?id=" ));
    client.print( id );
    client.print(F("&token="));
    client.print( token );
    if ( !post && ( attrs.length() > 0 )) {
      client.print( attrs );
    }
    client.println(F(" HTTP/1.1" ));

    client.print(F("Host: "));
    client.println( host );
    client.println(F("Connection: close\r\n"));
    if ( post && ( attrs.length() > 0 )) {
      client.println( F("Accept: */*"));
      client.println( F("Content-Type: application/x-www-form-urlencoded ; charset=UTF-8" ));
      client.print( F("Content-Length: "));
      client.println( attrs.length() );
      client.println();
      client.println( urlencode( attrs ));
    }
    client.println();
    return processResponse( request );
  }
  return false;
}

/**
   Handle the response, by taking away header info and such
*/
bool WebClient::processResponse( int request ) {
  // Check HTTP status
  char status[32] = {"\0"};
  client.setTimeout(HTTP_TIMEOUT);
  client.readBytesUntil('\r', status, sizeof(status));
  char http_ok[32] = {"\0"};
  strcpy( http_ok, "HTTP/1.1 200 OK");
  if (strcmp(status, http_ok) != 0) {
    Serial.print(F( "Unexpected response (" )); requestStr( request); Serial.print(F( "):" ));
    Serial.println(status);
    return false;
  }

  // Skip HTTP headers
  char endOfHeaders[] = "\r\n\r\n";
  if (!client.find(endOfHeaders)) {
    Serial.println( F( "Invalid response (" )); requestStr( request); Serial.print(F( "):" ));
    return false;
  }
  return true;
}

void WebClient::loop() {
  // if there are incoming bytes available
  // from the server, read them and print them:
  if (!client.connected()) {
    connect();
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
