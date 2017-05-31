const int analogPin0 = A0;
const int analogPin1 = A1;
const int analogPin2 = A2;
const int analogPin3 = A3;
const int analogPin4 = A4;

int sensorValue0 = 0;
int sensorValue1 = 0;
int sensorValue2 = 0;
int sensorValue3 = 0;
int sensorValue4 = 0;

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
}

void loop() {
  // put your main code here, to run repeatedly:
  sensorValue0 = analogRead(analogPin0);
  sensorValue1 = analogRead(analogPin1);
  sensorValue2 = analogRead(analogPin2);
  sensorValue3 = analogRead(analogPin3);
  sensorValue4 = analogRead(analogPin4);

  Serial.print("Pulgar:");
  Serial.print(sensorValue0);
  Serial.print("\t Indice:");
  Serial.print(sensorValue1);
  Serial.print("\t Medio:");
  Serial.print(sensorValue2);
  Serial.print("\t Anular:");
  Serial.print(sensorValue3);
  Serial.print("\t Meñique:");
  Serial.print(sensorValue4);
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
