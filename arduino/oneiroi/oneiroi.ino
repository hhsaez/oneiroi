#include <Servo.h>

#include "SerialStream.h"
#include "CommandDispatcher.h"

oneiroi::CommandDispatcher dispatcher;

int counter = 0;

// Servos
int HEAD_SERVO_PIN = 10;
Servo headServo;
float angle = 180.0;

// Sensors
int SENSOR_INTERVAL = 100;
int MIN_DISTANCE = 0;
int MAX_DISTANCE = 255;
int TRIG_PIN = 13;
int ECHO_PIN = 12;

unsigned long prevMillis;
unsigned long currentMillis;

int computeDistance() {
  int duration, distance;

  digitalWrite(TRIG_PIN, HIGH);
  delayMicroseconds(1000);
  digitalWrite(TRIG_PIN, LOW);

  duration = pulseIn(ECHO_PIN, HIGH);
  distance = (duration / 2) / 29.1;

  if (distance <= 0) {
    // "0" means no signal (lowest distance is 2cm)
    // assuming more than 3m
    return MAX_DISTANCE;
  }

  return distance;
}

void doScan() {
  headServo.write( 0 );
  delay( 1000 );
  
  headServo.write( 90 );
  delay( 1000 );
  
  headServo.write( 180 );
  delay( 1000 );
  
  headServo.write( 90 );
  delay( 1000 );
  
  Serial.println( "Nothing" );
}

void setup() {
  Serial.begin(9600);
  
  headServo.attach(HEAD_SERVO_PIN);
  headServo.write( 90 );
  delay( 1000 );
//  
//  pinMode(TRIG_PIN, OUTPUT);
//  pinMode(ECHO_PIN, INPUT);
//  
//  headServo.attach(HEAD_SERVO_PIN);
//  headServo.write(angle);
//  
//  prevMillis = millis();
//  currentMillis = prevMillis;
}

char buffer[ 1024 ];
int i = 0;
char c;

void loop() {
//  currentMillis = millis();
//  if (prevMillis - currentMillis > SENSOR_INTERVAL) {
//    int distance = computeDistance();
//    Serial.write(distance);
//    prevMillis = currentMillis;
//  }
//
//  if (Serial.peek() != -1) {
//    do {
//      angle = 180.0 - (Serial.read() / 255.0) * 180.0;
//    } while (Serial.peek() != -1);
//    
//    headServo.write(angle);
//  }

//  if ( Serial.peek() != -1 ) {
//    c = Serial.read();
//    if ( c == '\n' ) {
//      buffer[ i ] = '\0';
//      Serial.println( buffer );
//      i = 0;
//    }
//    else {
//      buffer[ i++ ] = c;
//    }
//  }

  while ( Serial.peek() != -1 ) {
    c = Serial.read();
    if ( c == '\n' ) {
      buffer[ i ] = '\0';
      delay( 3000 );
      if ( strcmp( buffer, "handshake" ) == 0 ) {
        Serial.println( "Hello, Dave" );
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

//  Serial.println( "Processing line" );
//
//  char buffer[ 1024 ];
//  int i = 0;
//  char c;
//  do {
//    while ( Serial.peek() < 0 ) {
//      delay( 10 );
//    }
//    
//    c = Serial.read();
//    buffer[ i++ ] = c != '\n' ? c : '\0';
//  } while ( c != '\n' );
//  
//  Serial.println(buffer);
}




