#include <Adafruit_NeoPixel.h>
#include <ArduinoJson.h>
#include "Logger.h"
#include "NeoPixel.h"
#include "WebClient.h"

#define REFRESH 3

const char NAME[]  = "Miip";
const int TOKEN = 1025;
const char* MIIP_CONTEXT = "miip2017/sa/";

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
  webClient.setup_Web();
  neoPixel.setup_Pixel();
  setup_Interrupt();
  Serial.println( "Setup Complete, Setting up Pixels");
  neoPixel.update_Pixel();
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
        neoPixel.update_Pixel();
        break;
      case 1:
        //logger.setup();
        //Serial.println( "LOGGER SETUP COMPLETE" );
        break;
      default:
        //Serial.println( "READING RADAR" );
        //neoPixel.loop_Pixel();
        //logger.logPrintln( "LOOPED PIXELS" );
        //Serial.println( "LOOP RADAR COMPLETE" );
        break;
    }
  }
}
