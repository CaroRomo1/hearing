#!/usr/bin/python

# Requirements:
# sudo aptitude install python-bluetooth

# Information Sources: 
# http://code.google.com/p/pybluez/source/browse/trunk/examples/simple/rfc...
# http://people.csail.mit.edu/albert/bluez-intro/x290.html#py-rfcomm-serve...

from __future__ import print_function
import time, sys, signal, atexit
from upm import pyupm_mpu9150 as sensorObj
import mraa
import bluetooth
import threading

name = "BluetoothChat"
uuid = "fa87c0d0-afac-11de-8a39-0800200c9a66"
#uuid = "00001101-0000-1000-8000-00805F9B34FB"

m = mraa.Gpio(5)
m.dir(mraa.DIR_IN)

server_sock = bluetooth.BluetoothSocket( bluetooth.RFCOMM )
#server_sock.bind(("", bluetooth.PORT_ANY))
server_sock.bind(("", 2))
server_sock.listen(1)
port = server_sock.getsockname()[1]

bluetooth.advertise_service( server_sock, name, uuid )

print ("Waiting for connection on RFCOMM channel ", port)

class echoThread(threading.Thread):
    def __init__ (self,sock,client_info):
        threading.Thread.__init__(self)
        self.sock = sock
        self.client_info = client_info
    def run(self):
        try:
            prev = 0

            # Instantiate an MPU60X0 on I2C bus 0
            sensor = sensorObj.MPU60X0()

            ## Exit handlers ##
            # This function stops python from printing a stacktrace when you hit control-C
            def SIGINTHandler(signum, frame):
                raise SystemExit

            # This function lets you run code on exit
            def exitHandler():
                print("Exiting")
                sys.exit(0)

            # # Register exit handlers
            # atexit.register(exitHandler)
            # signal.signal(signal.SIGINT, SIGINTHandler)

            sensor.init()

            x = sensorObj.new_floatp()
            y = sensorObj.new_floatp()
            z = sensorObj.new_floatp()

            while True:
                val = m.read()
                if(val == 1 and prev == 0) :
                    sensor.update()
                    sensor.getGyroscope(x, y, z)
                    print(" GX: ", sensorObj.floatp_value(x), end=' ')
                    print(" GY: ", sensorObj.floatp_value(y), end=' ')
                    print(" GZ: ", sensorObj.floatp_value(z))

                    coord = str(sensorObj.floatp_value(x)) + " " + str(sensorObj.floatp_value(y)) + " " + str(sensorObj.floatp_value(z))

                    self.sock.send(str(coord))
                    print (self.client_info, ": sent [", coord, "]")
                    prev = 1
                elif(val == 0 and prev == 1):
                    prev = 0
        except IOError:
            pass
        self.sock.close()
        print (self.client_info, ": disconnected")

while True:
    client_sock, client_info = server_sock.accept()
    print (client_info, ": connection accepted")
    echo = echoThread(client_sock, client_info)
    echo.setDaemon(True)
    echo.start()

server_sock.close()
print ("all done")
