Logger::Logger(){};

/**
   Get the waypoints for this vessel
*/
boolean Logger::update( JsonObject& root ) {
  Serial.println( "UPDATE Logger: ");

  if (!root.success()) {
    Serial.println("parse Log Object() failed");
    return false;
  }
  logger = root["o"];
  Serial.print( "LOG OPTIONS " ); Serial.println( logger );
  return true;
}

void Logger::setLogger( boolean choice ) {
  logger = choice;
}

void Logger::logPrint( String msg ) {
  if ( !logger )
    return;
  Serial.print( msg );
  JsonObject& root = webClient.logMessage( msg );
  update( root );
}

void Logger::logPrintln( String msg ) {
  if ( !logger )
    return;
  Serial.println( msg );
  JsonObject& root = webClient.logMessage( msg );
  update( root );
}

/**
   Get the waypoints for this vessel
*/
boolean Logger::setup( ) {
  JsonObject& root = webClient.requestLog();
  Serial.println( "SETUP Logger: ");
  return update( root );
}

