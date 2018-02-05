Logger::Logger(){};


void Logger::setLogger( boolean choice ) {
  logger = choice;
}

void Logger::logPrint( String msg ) {
  if ( !logger )
    return;
  Serial.print(msg );
  logger = webClient.logMessage( msg );
}

void Logger::logPrintln( String msg ) {
  if ( !logger )
    return;
  Serial.println( msg );
  logger = webClient.logMessage( msg );
}

/**
   Get the waypoints for this vessel
*/
boolean Logger::setup( ) {
  logger = webClient.requestLog();
  Serial.print( "SETUP Logger: " );Serial.println( logger );
  return logger;
}

