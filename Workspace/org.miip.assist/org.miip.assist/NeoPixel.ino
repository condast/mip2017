#include <Adafruit_NeoPixel.h>
#ifdef __AVR__
#include <avr/power.h>
#endif

#define PIN 6
#define LEDS 24

/**
   Pixel Data object
*/

// Parameter 1 = number of pixels in strip
// Parameter 2 = Arduino pin number (most are valid)
// Parameter 3 = pixel type flags, add together as needed:
//   NEO_KHZ800  800 KHz bitstream (most NeoPixel products w/WS2812 LEDs)
//   NEO_KHZ400  400 KHz (classic 'v1' (not v2) FLORA pixels, WS2811 drivers)
//   NEO_GRB     Pixels are wired for GRB bitstream (most NeoPixel products)
//   NEO_RGB     Pixels are wired for RGB bitstream (v1 FLORA pixels, not v2)
//   NEO_RGBW    Pixels are wired for RGBW bitstream (NeoPixel RGBW products)
Adafruit_NeoPixel strip = Adafruit_NeoPixel(LEDS, PIN, NEO_GRB + NEO_KHZ800);

// IMPORTANT: To reduce NeoPixel burnout risk, add 1000 uF capacitor across
// pixel power leads, add 300 - 500 Ohm resistor on first pixel's data input
// and minimize distance between Arduino and first pixel.  Avoid connecting
// on a live circuit...if you must, connect GND first.

int counter;

NeoPixel::NeoPixel() {
  choice = RADAR;
};

void NeoPixel::setup() {

  strip.begin();
  strip.show(); // Initialize all pixels to 'off'
  counter = 0;
  enable = true;
}

/**
   Update the radar. This call returns the options of the radar
*/
bool NeoPixel::update( ) {
  webClient.connect();

  //Serial.println(F("REQUEST SETUP"));
  bool result = webClient.sendHttp( WebClient::SETUP, false, F(""));
  PixelData data;
  if ( !result ) {
    webClient.disconnect();
    return false;
  }
  //Serial.println(F("SETUP RECEIVED: TRUE"));
  size_t capacity = JSON_OBJECT_SIZE(5) + 40;
  DynamicJsonBuffer jsonBuffer(capacity);

  // Parse JSON object
  JsonObject& root = jsonBuffer.parseObject(webClient.client);
  if (!root.success()) {
    Serial.println(F("Parsing failed!"));
    jsonBuffer.clear();
    webClient.disconnect();
    return false;
  }

  enable = root[F("enb")];
  data.index = root[F("i")];
  data.end = root[F("end")];
  data.choice = root[F("ch")];
  data.options = root[F("o")];
  //data.transparancy = root[F("t")];
  //Serial.print(F("PIXEL DATA ")); Serial.println(data.options);
  //Serial.print(F("ENABLE ")); Serial.println( enable);
  jsonBuffer.clear();
  webClient.disconnect();
  choice = static_cast<Choices>(data.choice );
  //Serial.print(F("NEOPIXEL SETUP ")); Serial.println(choice);
}
/**
    Send a request for radar data of a single LED in the ring
*/
bool NeoPixel::requestRadar() {
  //Serial.print("TOKEN: " ); Serial.println( token );
  //Serial.println( "SHOW RADAR: ");
  webClient.connect();
  int leds = ( strip.numPixels() == 0 ) ? LEDS : strip.numPixels();
  String str = F("&leds=");
  str += String( leds );
  //Serial.print( F("LEDS:")); Serial.println( str );
  boolean result = webClient.sendHttp( WebClient::RADAR, false, str);
  if ( !result ) {
    webClient.disconnect();
    return false;
  }
  size_t capacity = JSON_OBJECT_SIZE(6) + 40;
  DynamicJsonBuffer jsonBuffer(capacity);

  // Parse JSON object
  JsonObject& root = jsonBuffer.parseObject( webClient.client);
  if (!root.success()) {
    Serial.println(F("Parsing failed!"));
    jsonBuffer.clear();
    webClient.disconnect();
    return false;
  }
  colorPixel( root["a"],
              root["r"],
              root["g"],
              root["b"], 
              root["t"] );
  jsonBuffer.clear();
  webClient.disconnect();
  return true;
}

void NeoPixel::loop() {
  if (!enable ) {
    strip.show();
    return;
  }

  //Serial.print(F("NEO PIXEL: Selecting ")); Serial.println( choice );
  double divide = 50;//data.transparancy/100;
  int cmax = 255 * divide;
  int cmin = 255 * divide;
  switch ( choice ) {
    case RADAR:
      requestRadar();
      break;
    case COLOUR_WIPE_RED:
      colorWipe(strip.Color(cmax, 0, 0), cmin); // Red
      break;
    case COLOUR_WIPE_GREEN:
      colorWipe(strip.Color(0, cmax, 0), cmin); // Green
      break;
    case COLOUR_WIPE_BLUE:
      colorWipe(strip.Color(0, 0, cmax), cmin); // Blue
      break;
    case THEATER_CHASE_RED:
      theaterChase(strip.Color(127, 0, 0), cmin); // White
      break;
    case THEATER_CHASE_WHITE:
      theaterChase(strip.Color(127, 127, 127), cmin); // White
      break;
    case THEATER_CHASE_BLUE:
      theaterChase(strip.Color(0, 0, 127), cmin); // Blue
      break;
    case RAINBOW:
      rainbow(20);
      break;
    case RAINBOW_CYCLE:
      rainbowCycle(20);
      break;
    case RAINBOW_THEATRE_CHASE:
      theaterChaseRainbow(cmin);
      break;
    default:
      switch ( counter ) {
        // Some example procedures showing how to display to the pixels:
        case 0:
          colorWipe(strip.Color(255, 0, 0), 50); // Red
          break;
        case 1:
          colorWipe(strip.Color(0, 255, 0), 50); // Green
          break;
        case 2:
          colorWipe(strip.Color(0, 0, 255), 50); // Blue
          break;
        case 3:
          colorWipe(strip.Color(0, 0, 0 ), 50); // White RGBW
          break;
        case 4:
          // Send a theater pixel chase in...
          theaterChase(strip.Color(127, 127, 127), 50); // White
          break;
        case 5:
          theaterChase(strip.Color(127, 0, 0), 50); // Red
          break;
        case 6:
          theaterChase(strip.Color(0, 0, 127), 50); // Blue

          break;
        case 7:
          rainbow(20);
          break;
        case 8:
          rainbowCycle(20);
          break;
        case 9:
          theaterChaseRainbow(50);
      }
      break;
  }
  counter++;
  counter %= 9;
  //Serial.print("Selected " ); Serial.println( choice );
}

// Fill the dots at the index with the given RGB values
void NeoPixel::colorPixel( byte index, byte red, byte green, byte blue, byte transparancy ) {
  double trn = transparancy * 2.55;
  byte rd = (trn > red) ? 0 : red - trn;
  byte gn = (trn > green) ? 0 : green - trn;
  byte be = (trn > blue) ? 0 : blue - trn;
  //Serial.print( index ); Serial.print(F(": {")); Serial.print( rd ); Serial.print(F(", "));
  //Serial.print( gn ); Serial.print(F(", "));Serial.print( be );
  //Serial.print(F(", "));Serial.print(( byte)trn ); Serial.println(F("}"));
  strip.setPixelColor(index, strip.Color(rd, gn, be ));
  strip.show();
}

// Fill the dots one after the other with a color
void NeoPixel::colorWipe(uint32_t c, uint8_t wait) {
  for (uint16_t i = 0; i < strip.numPixels(); i++) {
    strip.setPixelColor(i, c);
    strip.show();
    delay(wait);
  }
}

void NeoPixel::rainbow(uint8_t wait) {
  uint16_t i, j;

  for (j = 0; j < 256; j++) {
    for (i = 0; i < strip.numPixels(); i++) {
      strip.setPixelColor(i, Wheel((i + j) & 255));
    }
    strip.show();
    delay(wait);
  }
}

// Slightly different, this makes the rainbow equally distributed throughout
void NeoPixel::rainbowCycle(uint8_t wait) {
  uint16_t i, j;

  for (j = 0; j < 256 * 5; j++) { // 5 cycles of all colors on wheel
    for (i = 0; i < strip.numPixels(); i++) {
      strip.setPixelColor(i, Wheel(((i * 256 / strip.numPixels()) + j) & 255));
    }
    strip.show();
    delay(wait);
  }
}

//Theatre-style crawling lights.
void NeoPixel::theaterChase(uint32_t c, uint8_t wait) {
  for (int j = 0; j < 10; j++) { //do 10 cycles of chasing
    for (int q = 0; q < 3; q++) {
      for (uint16_t i = 0; i < strip.numPixels(); i = i + 3) {
        strip.setPixelColor(i + q, c);  //turn every third pixel on
      }
      strip.show();

      delay(wait);

      for (uint16_t i = 0; i < strip.numPixels(); i = i + 3) {
        strip.setPixelColor(i + q, 0);      //turn every third pixel off
      }
    }
  }
}

//Theatre-style crawling lights with rainbow effect
void NeoPixel::theaterChaseRainbow(uint8_t wait) {
  for (int j = 0; j < 256; j++) {   // cycle all 256 colors in the wheel
    for (int q = 0; q < 3; q++) {
      for (uint16_t i = 0; i < strip.numPixels(); i = i + 3) {
        strip.setPixelColor(i + q, Wheel( (i + j) % 255)); //turn every third pixel on
      }
      strip.show();

      delay(wait);

      for (uint16_t i = 0; i < strip.numPixels(); i = i + 3) {
        strip.setPixelColor(i + q, 0);      //turn every third pixel off
      }
    }
  }
}

// Input a value 0 to 255 to get a color value.
// The colours are a transition r - g - b - back to r.
uint32_t NeoPixel::Wheel(byte WheelPos) {
  WheelPos = 255 - WheelPos;
  if (WheelPos < 85) {
    return strip.Color(255 - WheelPos * 3, 0, WheelPos * 3);
  }
  if (WheelPos < 170) {
    WheelPos -= 85;
    return strip.Color(0, WheelPos * 3, 255 - WheelPos * 3);
  }
  WheelPos -= 170;
  return strip.Color(WheelPos * 3, 255 - WheelPos * 3, 0);
}
