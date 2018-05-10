#ifndef NeoPixel_h
#define NeoPixel_h

#include "Arduino.h"

<<<<<<< HEAD
=======
#define PIN 6
#define LEDS 24

// This is for Trinket 5V 16MHz, you can remove these three lines if you are not using a Trinket
#if defined (__AVR_ATtiny85__)
if (F_CPU == 16000000) clock_prescale_set(clock_div_1);
#endif
// End of trinket special code

>>>>>>> branch 'master' of https://github.com/condast/mip2017.git
class NeoPixel {

<<<<<<< HEAD
=======
    /**
       Pixel Data object
    */
    struct PixelData {
      int index;
      bool end;
      int choice;
      int options;
    };

>>>>>>> branch 'master' of https://github.com/condast/mip2017.git
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
<<<<<<< HEAD
=======
    bool enable;
    PixelData data;
>>>>>>> branch 'master' of https://github.com/condast/mip2017.git
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
