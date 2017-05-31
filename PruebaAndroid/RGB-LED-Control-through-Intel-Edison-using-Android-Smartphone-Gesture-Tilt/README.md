# RGB LED Control through Intel Edison using Android Smartphone Gesture (Tilt)
Controlling an RGB LED through Intel Edison using Android smartphone gesture (Tilt) and collects the _app usage statistics_ (Ex: how many times the user checks the list of Bluetooth devices, number of successful connections, etc..) and posts it to [Google Analytics](https://www.google.com/analytics/)

The idea is to introduce smartphone with Intel Edison to trigger/control an event using sensors on smartphone. Most Android-powered devices have built-in sensors that measure motion, orientation, and various environmental conditions. These sensors are capable of providing raw data with high precision and accuracy.

By tilting the phone around Y-axis (Roll), control the color variations of an RGB LED which is connected to Edison.

## Setup
<p align="left">
<img src="https://github.com/sarweshkumar47/RGB-LED-Control-through-Intel-Edison-using-Android-Smartphone-Gesture-Tilt/blob/master/Images/led.jpg" alt="rgbled" width="400" />
<img src="https://github.com/sarweshkumar47/RGB-LED-Control-through-Intel-Edison-using-Android-Smartphone-Gesture-Tilt/blob/master/Images/setup.jpg" alt="setup" width="450" align="right"/>
</p>

## Demo
<p align="left">
<img src="https://github.com/sarweshkumar47/RGB-LED-Control-through-Intel-Edison-using-Android-Smartphone-Gesture-Tilt/blob/master/Images/rgb1.gif" alt="rgbled" width="420" />
<img src="https://github.com/sarweshkumar47/RGB-LED-Control-through-Intel-Edison-using-Android-Smartphone-Gesture-Tilt/blob/master/Images/rgb2.gif" alt="rgbled" width="420"  align="right" />
</p>

## Sofware Tools
* Android Studio

## Hardware
* [Intel Edison](http://www.intel.com/content/www/us/en/do-it-yourself/edison.html)
* [RGB LED](http://www.ebay.in/itm/8mm-Diffused-Round-RGB-LED-Diode-Common-Anode-Super-Bright-4-Legs-10-Pcs-Per-Lot-/171983997852?_trksid=p2054897.l4275)
* Android Smartphone (Should contain Accelerometer and Magnetometer)
* [220 Ohm Resistor - 3](http://www.amazon.com/E-Projects-Resistors-Watt-220R-Pieces/dp/B00B5LNEF6)

## Software
* __On Android:__

  Build the above project (BTRGBControl) using Android Studio and upload the generated apk to your smartphone
  
* __On Edison:__  

1. Bluetooth-SPP Python wrapper
2. PyBluez

The above two application packages are Edison specific. To install and run the program, please follow [this link](https://github.com/kiranmadansar/RGB-LED-control-using-Android-Smartphone).

## Credits
On Edison, the program was written by [Kiran Hegde](http://github.com/kiranmadansar) (Email: kiranmadansar@gmail.com)

