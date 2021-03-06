#!/usr/bin/python

# Requirements:
# sudo aptitude install python-bluetooth

# Information Sources: 
# http://code.google.com/p/pybluez/source/browse/trunk/examples/simple/rfc...
# http://people.csail.mit.edu/albert/bluez-intro/x290.html#py-rfcomm-serve...

import mraa
import bluetooth
import threading
import time

name = "BluetoothChat"
uuid = "fa87c0d0-afac-11de-8a39-0800200c9a66"
#uuid = "00001101-0000-1000-8000-00805F9B34FB"

x = mraa.Gpio(5)
x.dir(mraa.DIR_IN)

server_sock = bluetooth.BluetoothSocket( bluetooth.RFCOMM )
#server_sock.bind(("", bluetooth.PORT_ANY))
server_sock.bind(("", 2))
server_sock.listen(1)
port = server_sock.getsockname()[1]

bluetooth.advertise_service( server_sock, name, uuid )

print "Waiting for connection on RFCOMM channel %d" % port

class echoThread(threading.Thread):
    def __init__ (self,sock,client_info):
        threading.Thread.__init__(self)
        self.sock = sock
        self.client_info = client_info
    def run(self):
        try:
            yprev = 0
            while True:
                y = x.read()
                if(y == 1 and yprev == 0) :
                    self.sock.send(str(y))
                    print self.client_info, ": sent [%d]" % y
                    yprev = 1
                elif(y == 0 and yprev == 1):
                    yprev = 0
        except IOError:
            pass
        self.sock.close()
        print self.client_info, ": disconnected"

while True:
    client_sock, client_info = server_sock.accept()
    print client_info, ": connection accepted"
    echo = echoThread(client_sock, client_info)
    echo.setDaemon(True)
    echo.start()

server_sock.close()
print "all done"
