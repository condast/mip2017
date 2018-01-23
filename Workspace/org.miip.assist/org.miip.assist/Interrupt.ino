#define SECONDS 10

//Create a sempahore
boolean lock = false;

int int_counter = 0;
boolean flank;

/**
   Set timer0 to increment with steps of 10 Hz
*/
void setup_Interrupt() {
  lock = false;
  flank = false;
  cli();//stop interrupts

  //set timer2 interrupt at 2kHz
  TCCR2A = 0;// set entire TCCR2A register to 0
  TCCR2B = 0;// same for TCCR2B
  TCNT2  = 0;//initialize counter value to 0
  // set compare match register for 10 Hz increments
  OCR2A = 156;// = (16*10^6) / (10*102) - 1 (must be <256)
  // turn on CTC mode
  TCCR2A |= (1 << WGM21);
  // Set CS00 and CS02 bits for 1024 prescaler
  TCCR2B |= (1 << CS20) | (1 << CS21) | (1 << CS22);
  // enable timer compare interrupt
  TIMSK2 |= (1 << OCIE2A);

  sei();
}

boolean getLock() {
  return lock;
}

void setLock( boolean lck ) {
  lock = lck;
}

int getCounter() {
  return int_counter;
}

boolean getFlank() {
  return flank;
}

void clearFlank(){
  flank = false;
}

//Activate interrupt Timer2
ISR(TIMER2_COMPA_vect) {
  int_counter++;
  int_counter %= SECONDS;
  if ( int_counter > 0 )
    return;
  flank = true;
}
