boolean logger = false;

void setLogger( boolean choice ){
  logger = choice;
}

void LogPrint( String msg ){
  if( !logger )
    return;
  Serial.print( msg );
  logMessage( msg );
}

void LogPrintln( String msg ){
  if( !logger )
    return;
  Serial.println( msg );
  logMessage( msg );
}

