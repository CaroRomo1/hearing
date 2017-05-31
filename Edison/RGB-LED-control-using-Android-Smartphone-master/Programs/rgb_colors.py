#! /usr/bin python

"""
Here we take the accelerometer value from 180 degree to 360 degree. First 180 to 240 degree change is mapped to change in the intensity of Red LED, keeping other LEDs off. 240 to 300 degree change is mapped to intensity of blue LED, keeping Red LED on and green LED off. 300 to 360 degree change is mapped to intensity of green LED and other LEDs are kept on.
"""

from bluetooth import *
import time
import sys
import mraa

PORT = 2
server_sock=BluetoothSocket( RFCOMM )
server_sock.bind(("",PORT))
server_sock.listen(1)

port = server_sock.getsockname()[1]
uuid = "fa87c0d0-afac-11de-8a39-0800200c9a66"

advertise_service( server_sock, "SampleServer",
    service_id = uuid,
    service_classes = [ uuid, SERIAL_PORT_CLASS ],
    profiles = [ SERIAL_PORT_PROFILE ]
)

redled = mraa.Pwm(3)
redled.period_us(700)
redled.enable(True)

blueled = mraa.Pwm(5)                                       
blueled.period_us(700)                                      
blueled.enable(True)

greenled = mraa.Pwm(6)                                       
greenled.period_us(700)                                      
greenled.enable(True)  


def translate(value, leftMin, leftMax, rightMin, rightMax):
    # Figure out how 'wide' each range is
    leftSpan = leftMax - leftMin
    rightSpan = rightMax - rightMin

    # Convert the left range into a 0-1 range (float)
    valueScaled = float(value - leftMin) / float(leftSpan)

    # Convert the 0-1 range into a value in the right range.
    return rightMin + (valueScaled * rightSpan)

try:
  while True:
	redled.write(1)
	blueled.write(1)
	greenled.write(1)
        print "Waiting for connection on RFCOMM channel %d" % port
        client_sock, client_info = server_sock.accept()
        print "Accepted connection from ", client_info
        try:
                while True:
                        data = client_sock.recv(3)
                        if len(data) == 0: break
                        print str(data)
                        d = int(data)
			if (d < 180):
				d = 180
			elif (d > 360):
				d = 360
			else:
				pass
			if (d <= 240 and d >= 180):
				redval = translate(d, 180, 240, 1, 0)	
				redled.write(redval)
				blueled.write(1)
				greenled.write(1)
				del redval
			elif (d <= 300 and d > 240):
				blueval = translate(d, 241, 300, 1, 0)             
				redled.write(0) 
				blueled.write(blueval)                
				greenled.write(1)
				del blueval
			elif (d <= 360 and d > 300):
				greenval = translate(d, 301, 360, 1, 0)                   
				redled.write(0)            
				blueled.write(0)                
				greenled.write(greenval)
				del greenval
			else:
				redled.write(1)
				blueled.write(1)
				greenled.write(1)
                        del data
                        del d
        except IOError:
                pass
        print "Client disconnected "
        client_sock.close()
except KeyboardInterrupt:
        server_sock.close()
	redled.write(1)
	blueled.write(1)
	greenled.write(1)
        print "Interruption from keyboard"
except:
	redled.write(1)
	blueled.write(1)
	greenled.write(1)
        server_sock.close()
        print "General Exception"

