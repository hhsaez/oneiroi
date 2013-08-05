#ifndef ONEIROI_CONSOLE_COMMAND_CONNECT_
#define ONEIROI_CONSOLE_COMMAND_CONNECT_

#include "ShellCommand.hpp"

namespace oneiroi {

	class ConnectCommand : public ShellCommand {
	public:
		ConnectCommand( void );
		virtual ~ConnectCommand( void );

		virtual ReturnType execute( Shell *shell, std::istream &args ) override;
	};

}

#endif

