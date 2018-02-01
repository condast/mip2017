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
#define HTTP_11 " HTTP/1.1";
#define ACCEPT "Accept: */*"
#define CONTEXT_LENGTH "Content-Length: "
#define CONTENT_TYPE "Content-Type: application/x-www-form-urlencoded"

WebClient::WebClient( String name, int tkn ) {
  id = name;
  token = tkn;
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
  Serial.print( "Connected: "); Serial.println( result );
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
JsonObject& WebClient::requestSetup() {
  connecting();
  boolean result = sendHttp( REQ_SETUP, false, "" );
  if ( !result )
    return;
  Serial.println( "SETUP RECEIVED: TRUE" );
  JsonObject& root = processJsonRequest( 5, 40);
  disconnecting();
  return root;
}

/**
    Send a request for radar data
*/
JsonArray& WebClient::requestRadar( int leds ) {
  connecting();
  boolean result = sendHttp( REQ_RADAR, true, String( leds ));
  Serial.print( "RADAR SENT: " ); Serial.println( result );
  if ( !result )
    return;
  JsonArray& root = processJsonArray(leds, 5, 130);
  disconnecting();
  return root;
}

/**
   Send a request log message
*/
JsonObject& WebClient::requestLog() {
  connecting();
  boolean result = sendHttp( REQ_LOG, true, "" );
  //Serial.print( "LOG SETUP RECEIVED: " ); Serial.println( result );
  if ( !result )
    return;
  JsonObject& root = processJsonRequest(1, 10);
  disconnecting();
  return root;
}

/**
   send a log message
*/
JsonObject& WebClient::logMessage( String message ) {
  connecting();
  Serial.println( "log request: ");
  boolean result = sendHttp( REQ_LOG, true, message );
  if ( !result )
    return;
  JsonObject& root = processJsonRequest(1, 40);
  Serial.print( "LOG MESSAGE: " ); Serial.println( message );
  disconnecting();
  return root;
}

/**
   Translate to the correct REST path
*/
String WebClient::requeststr( int request ) {
  String retval = "update";
  switch ( request ) {
    case REQ_SETUP:
      retval = "setup";
      break;
    case REQ_RADAR:
      retval = "radar";
      break;
    case REQ_LOG:
      retval = "log";
      break;
    default:
      break;
  }
  return retval;
}

boolean WebClient::sendHttp( int request, boolean post, String msg ) {
  if (!isconnected )
    return false;

  // Make a HTTP request:
  String str = post ? "POST " : "GET ";
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
  Serial.println( str );
  client.println( str);

  str = HOST;
  str += CONDAST_URL;
  client.println( str);
  if ( post ) {
    client.println( ACCEPT );
    client.print( CONTEXT_LENGTH ); client.println( msg.length() );
    client.println( CONTENT_TYPE );
  }
  if (client.println() == 0) {
    Serial.println(F("Failed to send request"));
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
    Serial.println(F("Invalid response"));
    return false;
  }
  return true;
}

/**
   Process the clients request. for the data and buffer size see:
   http://arduinojson.org/assistant/
*/
JsonObject& WebClient::processJsonRequest( int data, int buffer) {
  // Allocate JsonBuffer
  // Use arduinojson.org/assistant to compute the capacity.
  const size_t capacity = JSON_OBJECT_SIZE(data) + buffer;
  DynamicJsonBuffer jsonBuffer(capacity);

  // Parse JSON object
  JsonObject& root = jsonBuffer.parseObject(client);
  if (!root.success())
    Serial.println(F("Parsing failed!"));
  return root;
}

/**
   Process the clients request. for the data and buffer size see:
   http://arduinojson.org/assistant/
*/
JsonArray& WebClient::processJsonArray( int size, int data, int buffer) {
  // Allocate JsonBuffer
  // Use arduinojson.org/assistant to compute the capacity.
  const size_t capacity = JSON_ARRAY_SIZE(size) + size * JSON_OBJECT_SIZE(data) + buffer;
  DynamicJsonBuffer jsonBuffer(capacity);

  // Parse JSON object
  JsonArray& root = jsonBuffer.parseArray(client);
  if (!root.success())
    Serial.println(F("Parsing failed!"));

  return root;
}

void WebClient::loop_Web() {
  // if there are incoming bytes available
  // from the server, read them and print them:
  if (!client.connected()) {
    connecting();
  }
}
