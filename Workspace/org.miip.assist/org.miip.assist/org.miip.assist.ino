#include <Adafruit_NeoPixel.h>
#include <ArduinoJson.h>
#include "Logger.h"
#include "NeoPixel.h"
#include "WebClient.h"

#define REFRESH 3

int load;

WebClient webClient;
Logger logger;
NeoPixel neoPixel;

void setup() {
  Serial.begin(9600);
  Serial.println(F("Setup MIIP Human Assist Radar"));
  webClient.setup();
  neoPixel.setup();
  setup_Interrupt();
  Serial.println(F("Setup Complete, Setting up Pixels"));
  neoPixel.update();
  Serial.println(F("Pixel Data Received"));
  load = 0;
}

void loop() {
  if ( getFlank()) {
    clearFlank();
    load = ( load + 1 ) % 120;
    int balance = load % REFRESH;
    //Serial.println( balance );
    switch ( balance ) {
      case 0:
        neoPixel.update();
        break;
      case 1:
        //logger.setup();
        //Serial.println( "LOGGER SETUP COMPLETE" );
        break;
      default:
        //Serial.println( "READING RADAR" );
        neoPixel.loop();
        //logger.logPrintln( "LOOPED PIXELS" );
        //Serial.println( "LOOP RADAR COMPLETE" );
        break;
    }
  }
}
