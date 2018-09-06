#include <Adafruit_NeoPixel.h>
#include <ArduinoJson.h>
#include "Logger.h"
#include "NeoPixel.h"
#include "WebClient.h"

#define REFRESH 3

const char NAME[]  = "Miip";
const int TOKEN = 1025;

    /**
       Pixel Data object
    */
    struct PixelData {
      char* remarks;
      int index;
      boolean end;
      int choice;
      int options;
    };


int load;

WebClient webClient( NAME, TOKEN );
Logger logger;
NeoPixel neoPixel;

void setup() {
  Serial.begin(9600);
  Serial.println( "Setup MIIP Human Assist Radar");
  webClient.setup();
  neoPixel.setup();
  setup_Interrupt();
  Serial.println( "Setup Complete, Setting up Pixels");
  neoPixel.update();
  Serial.println( "Pixel Data Received");
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
        Serial.println( "READING RADAR" );
        neoPixel.loop();
        //logger.logPrintln( "LOOPED PIXELS" );
        Serial.println( "LOOP RADAR COMPLETE" );
        break;
    }
  }
}
