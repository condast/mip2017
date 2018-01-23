boolean logger = false;

void setLogger( boolean choice ){
  logger = choice;
}

void print( String msg ){
  if( !logger )
    return;
  Serial.print( msg );
  logMessage( msg );
}

void println( String msg ){
  if( !logger )
    return;
  Serial.println( msg );
  logMessage( msg );
}

