#include <Servo.h>

// Servos
Servo headServo;
int HEAD_SERVO_PIN = 10;
int HEAD_LEFT_ANGLE = 180;
int HEAD_CENTER_ANGLE = 90;
int HEAD_RIGHT_ANGLE = 0;

// Sensors
int SENSOR_INTERVAL = 100;
int MIN_DISTANCE = 0;
int MAX_DISTANCE = 255;
int TRIG_PIN = 13;
int ECHO_PIN = 12;

// Utility
int i = 0;

int computeDistance() 
{
  int duration, distance;

  digitalWrite( TRIG_PIN, HIGH );
  delayMicroseconds( 1000 );
  digitalWrite( TRIG_PIN, LOW );

  duration = pulseIn( ECHO_PIN, HIGH );
  distance = ( duration / 2.0 ) / 29.1;
  return distance;
}

void doScan() {
  headServo.write( HEAD_LEFT_ANGLE );
  delay( 1000 );
  int left = computeDistance();
  
  headServo.write( HEAD_RIGHT_ANGLE );
  delay( 1000 );
  int right = computeDistance();
  
  headServo.write( HEAD_CENTER_ANGLE );
  delay( 1000 );
  int center = computeDistance();
  
  Serial.print( left );
  Serial.print( " " );
  Serial.print( center );
  Serial.print( " " );
  Serial.println( right );
}

void setup() 
{
  Serial.begin( 9600 );
  
  headServo.attach( HEAD_SERVO_PIN );
  headServo.write( HEAD_CENTER_ANGLE );
  delay( 1000 );
  
  pinMode( TRIG_PIN, OUTPUT );
  pinMode( ECHO_PIN, INPUT );

  i = 0;
}

void loop() 
{
  char buffer[ 1024 ];
  while ( Serial.peek() != -1 ) {
    char c = Serial.read();
    if ( c == '\n' ) {
      buffer[ i ] = '\0';
      delay( 3000 );
      if ( strcmp( buffer, "handshake" ) == 0 ) {
        Serial.println( "Hello, Dave. How are you today?" );
      }
      else if ( strcmp( buffer, "scan" ) == 0 ) {
        doScan();
      }
      else {
        Serial.println( buffer );
      }
      i = 0;
    }
    else {
      buffer[ i++ ] = c;
    }
  }
}

