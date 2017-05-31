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
  sensorValue1= analogRead(analogPin1);
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
  Serial.print("\t Menique:");
  Serial.print(sensorValue4);
  Serial.println();

  delay (500);
}
