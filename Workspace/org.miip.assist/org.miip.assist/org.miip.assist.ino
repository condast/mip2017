#include <Adafruit_NeoPixel.h>
#include <ArduinoJson.h>
#include "WebClient.h"
#include "Interrupts.h"
#include "NeoPixel.h"
#include "Logger.h"

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

Interrupts interrupt;
WebClient webClient( NAME, TOKEN );
Logger logger;
NeoPixel neoPixel;

void setup() {
  Serial.begin(9600);
  Serial.println(F("Setup MIIP Human Assist Radar"));
  webClient.setup();
  neoPixel.setup();
  interrupt.setup();
  Serial.println(F("Setup Complete, Setting up Pixels"));
  bool result = neoPixel.update();
  Serial.print(F("Pixel Data Received: "));
  Serial.println( result );
  load = 0;
}

void loop() {
  neoPixel.loop();
  
  if ( interrupt.getSecondsFlank()) {
    interrupt.clearSecondsFlank();
    load = ( load + 1 ) % 120;
    int balance = load % REFRESH;
    //Serial.println( balance );
    switch ( balance ) {
      case 0:
        neoPixel.update();
        break;
      case 1:
        //logger.setup();
        Serial.println( "LOGGER SETUP COMPLETE" );
        break;
      default:
         break;
    }
  }
}
