#include "CommandDispatcher.h"

#include <string>

using namespace oneiroi;

CommandDispatcher::CommandDispatcher( void )
{

}

CommandDispatcher::~CommandDispatcher( void )
{

}

void CommandDispatcher::poll( void )
{
	int size = 0;
	char buffer[ 1024 ];
	char c;
	do {
		while ( !Serial.peek() != -1 ) {
			delay( 10 );
		}

		c = Serial.read();
		buffer[ size++ ] = c != '\n' ? c : '\0';
	} while ( c != '\n' );

	Serial.print( "Received: " );
	Serial.println( buffer );
}

