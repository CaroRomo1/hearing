#include <MPU9150Lib.h>
#include <MPUQuaternion.h>
#include <MPUVector3.h>

// Arduino Wire library is required if I2Cdev I2CDEV_ARDUINO_WIRE implementation
// is used in I2Cdev.h
#include "Wire.h"

// I2Cdev and MPU9150 must be installed as libraries, or else the .cpp/.h files
// for both classes must be in the include path of your project
#include "I2Cdev.h"
#include <MPU9150.h>
#include "helper_3dmath.h"

const int analogPin0 = A0;
const int analogPin1 = A1;
const int analogPin2 = A2;
const int analogPin3 = A3;
//const int analogPin4 = A4;

int sensorValue0 = 0;
int sensorValue1 = 0;
int sensorValue2 = 0;
int sensorValue3 = 0;
int sensorValue4 = 0;

// class default I2C address is 0x68
// specific I2C addresses may be passed as a parameter here
// AD0 low = 0x68 (default for InvenSense evaluation board)
// AD0 high = 0x69
MPU9150 accelGyroMag;

int16_t ax, ay, az;
int16_t gx, gy, gz;
int16_t mx, my, mz;

//#define LED_PIN 13
//bool blinkState = false;

void setup() {
  // join I2C bus (I2Cdev library doesn't do this automatically)
  Wire.begin();
    
  // put your setup code here, to run once:
  Serial.begin(9600);
  
  // initialize device
  Serial.println("Initializing I2C devices...");
  accelGyroMag.initialize();

  // verify connection
  Serial.println("Testing device connections...");
  Serial.println(accelGyroMag.testConnection() ? "MPU9150 connection successful" : "MPU9150 connection failed");

  // configure Arduino LED for
  //pinMode(LED_PIN, OUTPUT);
}

void loop() {
    // put your main code here, to run repeatedly:
    sensorValue0 = analogRead(analogPin0);
    sensorValue1 = analogRead(analogPin1);
    sensorValue2 = analogRead(analogPin2);
    sensorValue3 = analogRead(analogPin3);
    //sensorValue4 = analogRead(analogPin4);

    // read raw accel/gyro/mag measurements from device
    accelGyroMag.getMotion9(&ax, &ay, &az, &gx, &gy, &gz, &mx, &my, &mz);
    // display tab-separated accel/gyro/mag x/y/z values
    Serial.print("accelerometer(x,y,z)\t");
    Serial.print(ax); Serial.print("\t");
    Serial.print(",");
    Serial.print(ay); Serial.print("\t");
    Serial.print(",");
    Serial.print(az); Serial.print("\t");
    Serial.print(",");
    Serial.print("gyroscope(x,y,z)\t");
    Serial.print(gx); Serial.print("\t");
    Serial.print(",");
    Serial.print(gy); Serial.print("\t");
    Serial.print(",");
    Serial.print(gz); Serial.print("\t");
    Serial.print(",");
    Serial.println(); 

    const float N = 256;

    Serial.print("Pulgar:");
    Serial.print(sensorValue0);
    Serial.print("\t Indice:");
    Serial.print(sensorValue1);
    Serial.print("\t Medio:");
    Serial.print(sensorValue2);
    Serial.print("\t Anular:");
    Serial.print(sensorValue3);
    //Serial.print("\t Meñique:");
    //Serial.print(sensorValue4);
    Serial.println();
    fingerDetect();
  
    delay (500);
}

void fingerDetect(){
  if ((sensorValue0 < 96 && sensorValue0 > 70) && (sensorValue1 < 186 && sensorValue1 > 160) && (sensorValue3 < 198 && sensorValue3 > 172)){
    Serial.println("a");
  }
  else if ((sensorValue0 < 130 && sensorValue0 > 105) && (sensorValue1 < 105 && sensorValue1 > 80) && (sensorValue3 < 110 && sensorValue3 > 85)){
    Serial.println("b");
  }
  else if ((sensorValue0 < 125 && sensorValue0 > 100) && (sensorValue1 < 130 && sensorValue1 > 105) && (sensorValue3 < 150 && sensorValue3 > 125)){
    Serial.println("c");
  }
  else if ((sensorValue0 < 125 && sensorValue0 > 100) && (sensorValue1 < 105 && sensorValue1 > 80) && (sensorValue3 < 198 && sensorValue3 > 172)){
    Serial.println("d");
  }
  else if ((sensorValue0 < 135 && sensorValue0 > 110) && (sensorValue1 < 165 && sensorValue1 > 140) && (sensorValue3 < 165 && sensorValue3 > 140)){
    Serial.println("e");
  }
  else {
    Serial.println("No letter");
  }
}
