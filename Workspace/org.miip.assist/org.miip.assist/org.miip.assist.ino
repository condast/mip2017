#include <ArduinoJson.h>
#include <StackArray.h>

void setup() {
  Serial.begin(9600);
  Serial.println( "Setup Radar");
  //setup_Web();
  setup_Pixel();
  setup_Interrupt();
  Serial.println( "Setup Complete, Setting up Remote");
  pixelSetup();
  Serial.println( "Remote Data Received");
}

void loop() {
  loop_Pixel();
}
