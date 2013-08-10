/*
 * Copyright (c) 2013, Hugo Hernan Saez
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

#include "Shell.hpp"
#include "commands/HelpCommand.hpp"
#include "commands/TerminateCommand.hpp"
#include "commands/ConnectCommand.hpp"
#include "commands/ScanCommand.hpp"

#include <iostream>
#include <sstream>

using namespace oneiroi;

Shell::Shell( int argc, char **argv, std::string prompt )
	: _prompt( prompt )	
{
	for ( int i = 1; i < argc; i++ ) {
		_args << argv[ i ] << " ";
	}

	registerCommand( ShellCommandPtr( new HelpCommand() ) );
	registerCommand( ShellCommandPtr( new TerminateCommand() ) );
	registerCommand( ShellCommandPtr( new ConnectCommand() ) );
	registerCommand( ShellCommandPtr( new ScanCommand() ) );
}

Shell::~Shell( void )
{

}

void Shell::registerCommand( ShellCommandPtr command )
{
	_commands[ command->getName() ] = command;
}

void Shell::eachCommand( std::function< void( ShellCommandPtr & ) > callback )
{
	for ( auto it : _commands ) {
		callback( it.second );
	}
}

int Shell::run( void )
{
	char buffer[ 1024 ];
	bool done = false;

	if ( !_args.eof() ) {
		processLine( _args );
	}

	do {
		std::cout << _prompt;
		std::cin.getline( buffer, 1024 );
		std::stringstream args;
		args << buffer;

		ShellCommand::ReturnType ret = processLine( args );
		if ( ret == ShellCommand::ReturnType::TERMINATE ) {
			done = true;
		}
	} while ( !done );

	return 0;
}

ShellCommand::ReturnType Shell::processLine( std::stringstream &args )
{
	std::string commandName;
	args >> commandName;
	ShellCommandPtr command = _commands[ commandName ];
	if ( command != nullptr ) {
		return command->execute( this, args );
	}

	if ( commandName.length() > 0 ) {
		std::cerr << "Unknown command '" << commandName << "'" << std::endl;
	}

	return ShellCommand::ReturnType::ERROR;
}

