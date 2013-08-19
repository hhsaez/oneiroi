#include <Servo.h>

#define ACTION_HANDSHAKE "handshake"
#define ACTION_SCAN "scan"
#define ACTION_TURN_LEFT "left"
#define ACTION_TURN_RIGHT "right"
#define ACTION_MOVE_FORWARD "forward"
#define ACTION_MOVE_BACKWARD "backward"

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

// Motors
int MAX_SPEED = 255;
int MIN_SPEED = 150;
int ENA = 5;
int IN1 = 2;
int IN2 = 3;
int ENB = 6;
int IN3 = 4;
int IN4 = 7;

// utility
int i = 0;
int distanceLeft = 0;
int distanceRight = 0;
int distanceCenter = 0;

void setup() 
{
  Serial.begin( 9600 );
  
  // setup motors
  pinMode( ENA, OUTPUT );
  pinMode( ENB, OUTPUT );
  pinMode( IN1, OUTPUT );
  pinMode( IN2, OUTPUT );
  pinMode( IN3, OUTPUT );
  pinMode( IN4, OUTPUT );
  runMotors();
  
  // setup servos
  headServo.attach( HEAD_SERVO_PIN );
  headServo.write( HEAD_CENTER_ANGLE );
  delay( 1000 );
  
  // setup sensors
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
      dispatchAction( buffer );
      i = 0;
    }
    else {
      buffer[ i++ ] = c;
    }
  }
}

void dispatchAction( const char *action )
{
  if ( strcmp( action, ACTION_HANDSHAKE ) == 0 ) {
    handshake();
  }
  else if ( strcmp( action, ACTION_SCAN ) == 0 ) {
    scan();
  }
  else if ( strcmp( action, ACTION_TURN_LEFT ) == 0 ) {
    turnLeft();
  }
  else if ( strcmp( action, ACTION_TURN_RIGHT ) == 0 ) {
    turnRight();
  }
  else if ( strcmp( action, ACTION_MOVE_FORWARD ) == 0 ) {
    moveForward();
  }
  else if ( strcmp( action, ACTION_MOVE_BACKWARD ) == 0 ) {
    moveBackward();
  }
  else {
    unknownAction( action );
  }
}

void handshake() 
{
  Serial.println( "Hello, Dave. How are you today?" );
}

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

void scan() {
  headServo.write( HEAD_LEFT_ANGLE );
  delay( 500 );
  distanceLeft = computeDistance();
  
  headServo.write( HEAD_RIGHT_ANGLE );
  delay( 1000 );
  distanceRight = computeDistance();
  
  headServo.write( HEAD_CENTER_ANGLE );
  delay( 500 );
  distanceCenter = computeDistance();
  
  Serial.print( distanceLeft );
  Serial.print( " " );
  Serial.print( distanceCenter );
  Serial.print( " " );
  Serial.println( distanceRight );
}

void stopMotors() 
{
  digitalWrite( ENA, LOW );
  digitalWrite( ENB, LOW );
}

void runMotors()
{
  digitalWrite( ENA, HIGH );
  digitalWrite( ENB, HIGH );
}

void motorsForward() {
  digitalWrite(IN1, LOW);
  digitalWrite(IN2, HIGH);
  digitalWrite(IN3, LOW);
  digitalWrite(IN4, HIGH);
}

void motorsBackward() {
  digitalWrite(IN1, HIGH);
  digitalWrite(IN2, LOW);
  digitalWrite(IN3, HIGH);
  digitalWrite(IN4, LOW);
}

void motorsTurnLeft() {
  digitalWrite(IN1, LOW);
  digitalWrite(IN2, HIGH);
  digitalWrite(IN3, HIGH);
  digitalWrite(IN4, LOW);
}

void motorsTurnRight() {
  digitalWrite(IN1, HIGH);
  digitalWrite(IN2, LOW);
  digitalWrite(IN3, LOW);
  digitalWrite(IN4, HIGH);
}

void turnLeft()
{ 
  stopMotors();
  motorsTurnLeft();
  runMotors();
  analogWrite( ENA, MAX_SPEED );
  analogWrite( ENB, MAX_SPEED );
  delay( 250 );
  stopMotors();
  Serial.println( "Action completed" );
}

void turnRight()
{
  stopMotors();
  motorsTurnRight();
  runMotors();
  analogWrite( ENA, MAX_SPEED );
  analogWrite( ENB, MAX_SPEED );
  delay( 250 );
  stopMotors();
  Serial.println( "Action completed" );
}

void moveForward()
{
  stopMotors();
  motorsForward();
  runMotors();
  analogWrite( ENA, MAX_SPEED );
  analogWrite( ENB, MAX_SPEED );
  delay( 250 );
  stopMotors();
  Serial.println( "Action completed" );
}

void moveBackward()
{
  stopMotors();
  motorsBackward();
  runMotors();
  analogWrite( ENA, MAX_SPEED );
  analogWrite( ENB, MAX_SPEED );
  delay( 250 );
  stopMotors();
  Serial.println( "Action completed" );
}

void unknownAction(String action)
{
  headServo.write( HEAD_CENTER_ANGLE - 30 );
  delay( 100 );
  headServo.write( HEAD_CENTER_ANGLE + 30 );
  delay( 100 );
  headServo.write( HEAD_CENTER_ANGLE );
  delay( 100 );

  Serial.print( "Unknown action: " );
  Serial.println( action );
}


