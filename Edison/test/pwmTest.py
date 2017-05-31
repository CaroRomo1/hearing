#! /usr/bin python

import time
import sys
import mraa

x = mraa.Pwm(20)
x.period_us(700)
x.enable(True)
value = 0.0

try:
	while True:
		x.write(value)
		time.sleep(0.05)
		value = value + 0.01
		if value >= 1:
			value = 0.0