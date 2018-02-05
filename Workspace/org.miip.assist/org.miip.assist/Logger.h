#ifndef Logger_h
#define Logger_h

#include "Arduino.h"

class Logger {

  private: boolean logger = false;
      
  public: Logger(void);
    void setLogger( boolean choice );
    void logPrint( String msg );
    void logPrintln( String msg );
    boolean setup();
};

#endif

