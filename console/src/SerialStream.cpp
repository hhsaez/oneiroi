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

#include "SerialStream.hpp"

#include <iostream>
#include <cstdio>    /* Standard input/output definitions */
#include <stdlib.h> 
#include <stdint.h>   /* Standard types */
#include <string.h>   /* String function definitions */
#include <unistd.h>   /* UNIX standard function definitions */
#include <fcntl.h>    /* File control definitions */
#include <errno.h>    /* Error number definitions */
#include <termios.h>  /* POSIX terminal control definitions */
#include <sys/ioctl.h>
#include <getopt.h>

using namespace oneiroi;

SerialStream::EndLine SerialStream::End;

SerialStream::SerialStream( void )
    : _device( -1 )
{
}

SerialStream::~SerialStream( void )
{
    close();
}

bool SerialStream::open( std::string deviceName, unsigned int baudRate )
{
    struct termios toptions;
    _device = -1;

    std::cout << "Opening device " << deviceName << "@" << baudRate << "bps... ";
    _device = ::open( deviceName.c_str(), O_RDWR | O_NOCTTY | O_NDELAY );
    if ( _device < 0 )  {
        std::cout << "ERROR: Unable to open port " << deviceName << std::endl;
        return false;
    }
    else {
        std::cout << "SUCCESS." << std::endl;
    }

    std::cout << "Fetching term attributes... ";
    if ( tcgetattr( _device, &toptions ) < 0 ) {
        std::cout << "ERROR: Couldn't get term attributes" << std::endl;
        return false;
    }
    else {
        std::cout << "SUCCESS." << std::endl;
    }

    speed_t brate = baudRate; // let you override switch below if needed
    switch( baudRate ) {
	    case 4800: 
            brate = B4800; 
            break;

	   	case 14400: 
            brate = B14400;
            break;

		case 19200: 
            brate = B19200; 
            break;

	   	case 28800: 
            brate = B28800; 
            break;

	    case 38400: 
            brate = B38400; 
            break;

	    case 57600: 
            brate = B57600; 
            break;

	    case 115200: 
            brate = B115200; 
            break;

        case 9600: 
        default:
            brate = B9600; 
            break;
    }
    std::cout << "Setting baud rate to " << brate << "bps... ";

    cfsetispeed( &toptions, brate );
    cfsetospeed( &toptions, brate );
    std::cout << "SUCCESS." << std::endl;

    std::cout << "Configuring term options... ";
    // 8N1
    toptions.c_cflag &= ~PARENB;
    toptions.c_cflag &= ~CSTOPB;
    toptions.c_cflag &= ~CSIZE;
    toptions.c_cflag |= CS8;

    // no flow control
    toptions.c_cflag &= ~CRTSCTS;
    toptions.c_cflag |= CREAD | CLOCAL;  // turn on READ & ignore ctrl lines
    toptions.c_iflag &= ~(IXON | IXOFF | IXANY); // turn off s/w flow ctrl
    toptions.c_lflag &= ~(ICANON | ECHO | ECHOE | ISIG); // make raw
    toptions.c_oflag &= ~OPOST; // make raw

    // see: http://unixwiz.net/techtips/termios-vmin-vtime.html
    toptions.c_cc[ VMIN ] = 0;
    toptions.c_cc[ VTIME ] = 0;

    if( tcsetattr( _device, TCSANOW, &toptions ) < 0) {
        std::cout << "ERROR: Couldn't set term attributes." << std::endl;
        return false;
    }
    else {
        std::cout << "SUCCESS." << std::endl;
    }

    flush();

    return true;
}

void SerialStream::flush( void )
{
    sleep( 2 );
    tcflush( _device, TCIOFLUSH );
}

void SerialStream::close( void )
{
    if ( isOpen() ) {
        ::close( _device );
        _device = -1;
    }
}

void SerialStream::write( std::string s )
{
    int n = ::write( _device, s.c_str(), s.length() );
    if ( n < s.length() ) {
        std::cerr << "Warning: not all bytes could be sent (" << n << " of " << s.length() << " bytes sent)" << std::endl;
    }
}

std::string SerialStream::readLine( void )
{
    flush();

    char b[ 1 ];
    char buffer[ 1024 ];
    int i = 0;
    do {
        int n = ::read( _device, b, 1 );
        if ( n == -1 ) {
            return "";
        }

        if ( n == 0 ) {
            // wait 10 msec and try again
            usleep( 10 * 1000 );
            continue;
        }

        buffer[ i++ ] = b[ 0 ] != '\n' ? b[ 0 ] : '\0';
    } while( b[ 0 ] != '\n' );

    return std::string( buffer );
}

