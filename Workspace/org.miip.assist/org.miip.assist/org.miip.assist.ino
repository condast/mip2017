#include <Adafruit_NeoPixel.h>

#include <ArduinoJson.h>
#include <StackArray.h>

int load;

void setup() {
  Serial.begin(9600);
  Serial.println( "Setup Radar");
  setup_Web();
  setup_Pixel();
  setup_Interrupt();
  Serial.println( "Setup Complete, Setting up Remote");
  pixelSetup();
  Serial.println( "Remote Data Received");
  load = 0;
}

void loop() {
  if ( getFlank()) {
    clearFlank();
    load = ( load + 1 ) % 120;
    Serial.println( load );
    if ( load % 10 == 0 ) {
      LogPrintln( "SETUP PIXEL");
      pixelSetup();
    }
  }
  loop_Pixel();
}
