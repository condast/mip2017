#define BUFFER_SIZE 300

JsonArray& parseArray( String str ) {
  if ((str.length() < 2 ) || !str.startsWith("[")) {
    str = "[]";
    //Serial.print( "JSON ARRAY: " );
    //Serial.println( str );
  }
  //Serial.print( "JSON ARRAY: " );
  //Serial.println( str );
  StaticJsonBuffer<BUFFER_SIZE> jsonBuffer;
  return jsonBuffer.parseArray(str);
}
