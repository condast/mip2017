#ifndef Logger_h
#define Logger_h

#include "Arduino.h"

class Logger {

  private: boolean logger = false;
    boolean update( JsonObject& root );
    
  public: Logger(void);
    void setLogger( boolean choice );
    void logPrint( String msg );
    void logPrintln( String msg );
    boolean setup();
};

#endif

