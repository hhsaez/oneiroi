#ifndef ONEIROI_ARDUINO_COMMAND_DISPATCHER_
#define ONEIROI_ARDUINO_COMMAND_DISPATCHER_

#include "Arduino.h"

namespace oneiroi {

	class CommandDispatcher {
	public:
		CommandDispatcher( void );
		~CommandDispatcher( void );

		void poll( void );
	};

}

#endif

