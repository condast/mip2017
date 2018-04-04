#ifndef NeoPixel_h
#define NeoPixel_h

#include "Arduino.h"

class NeoPixel {

    enum Choices {
      RADAR,
      COLOUR_WIPE_RED,
      COLOUR_WIPE_GREEN,
      COLOUR_WIPE_BLUE,
      THEATER_CHASE_RED,
      THEATER_CHASE_WHITE,
      THEATER_CHASE_BLUE,
      RAINBOW,
      RAINBOW_CYCLE,
      RAINBOW_THEATRE_CHASE,
      ALL
    };

  private: enum Choices choice = ALL;
    int counter;
    boolean update( JsonObject& root );
    void colorPixel( byte index, byte red, byte green, byte blue, byte transparency );
    void show_Radar();
    void colorWipe(uint32_t c, uint8_t wait);
    void rainbow(uint8_t wait);
    void rainbowCycle(uint8_t wait);
    void theaterChase(uint32_t c, uint8_t wait);
    void theaterChaseRainbow(uint8_t wait);
    uint32_t Wheel(byte WheelPos);
    
  public: NeoPixel(void);
    void setup_Pixel();
    boolean update_Pixel();
    void loop_Pixel();
};

#endif
