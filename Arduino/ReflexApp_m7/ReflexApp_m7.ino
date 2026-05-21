
#include <RPC.h>
#include <ArduinoBLE.h>

bool timerRunning = false;
int leftside = 0;
int rightside = 0;
unsigned long startTime = 0;
unsigned long endTime = 0;
unsigned long reactionTime = 0;
int repeats = 0;
unsigned long avgreactionTime = 0;
unsigned long avgtime = 0 ;
int buttons = 0;
unsigned long MINreaction;
unsigned long MAXreaction;

int leftLED[9]  = {23,25,27,29,31,33,35,37,39};
int leftBTN[9]  = {22,24,26,28,30,32,34,36,38};

int rightLED[9] = {41,43,45,47,49,51,53,9,11};
int rightBTN[9] = {40,42,44,46,48,50,52,8,10};

byte gameMode = 0;


BLEService ReflexAPP("12345678-1234-1234-1234-1234567890ab");  
BLECharacteristic  modeChar("12345678-1234-1234-1234-1234567890ac", BLERead | BLEWrite, 50);
BLEStringCharacteristic resultChar("12345678-1234-1234-1234-1234567890ad", BLENotify, 50);

void setup() {
  
  Serial.begin(9600);
  randomSeed(analogRead(A0));
  delay(2000); 

  if (!BLE.begin()) {
    Serial.println("BLE nie wystartowal!");
    while (1);
  } 

  BLE.setLocalName("ReflexDevice");
  BLE.setDeviceName("ReflexDevice"); 

  ReflexAPP.addCharacteristic(modeChar);
  ReflexAPP.addCharacteristic(resultChar);
  BLE.addService(ReflexAPP);
  BLE.setAdvertisedService(ReflexAPP);

  BLE.advertise();

  Serial.println("BLE ready");

  for(int i=0;i<9;i++){

    pinMode(leftLED[i],OUTPUT);
    pinMode(leftBTN[i],INPUT_PULLUP);

    pinMode(rightLED[i],OUTPUT);
    pinMode(rightBTN[i],INPUT_PULLUP);

  }
  
  RPC.begin();
  
}

void loop() {
  BLE.poll();

 if (modeChar.written()) {

    int length = modeChar.valueLength();
    String value = "";

    for (int i = 0; i < length; i++) {
        value += (char)modeChar.value()[i];
    }

    value.trim();

    Serial.print("ODEBRANO: ");
    Serial.println(value);

    if (value == "devicetest") {
        DeviceTest();
    }

    if (value == "single") {
        SinglePlayerGame();
    }

    if (value == "multi") {
        MultiPlayerGame();
    }
}
}

void DeviceTest() {

  // TEST LEWEJ STRONY
  for(int i = 0; i < 9; i++) {

    digitalWrite(leftLED[i], HIGH);

    while(digitalRead(leftBTN[i]) == HIGH) {}

    digitalWrite(leftLED[i], LOW);

    Serial.print("Lewo Guzik ");
    Serial.print(i + 1);
    Serial.println(" działa.");

  }

  // TEST PRAWEJ STRONY
  for(int i = 0; i < 9; i++) {

    digitalWrite(rightLED[i], HIGH);

    while(digitalRead(rightBTN[i]) == HIGH) {}

    digitalWrite(rightLED[i], LOW);

    Serial.print("Prawo Guzik ");
    Serial.print(i + 1);
    Serial.println(" działa.");

  }

}

void SinglePlayerGame() {

  int repeats = 0;
  int buttons = 0;

  long sumTime = 0;
  int MINreaction = 2000;
  int MAXreaction = 0;

  while (repeats < 5) {

    int left = random(0, 9);

    Serial.print("LEWO --- ");
    Serial.println(left + 1);

    long reactionTime = reactionTest(leftLED[left], leftBTN[left]);

    Serial.print("Czas: ");
    Serial.print(reactionTime);
    Serial.println(" ms");

    sumTime += reactionTime;
    buttons++;

    if (reactionTime < MINreaction) MINreaction = reactionTime;
    if (reactionTime > MAXreaction) MAXreaction = reactionTime;


    int right = random(0, 9);

    Serial.print("Prawo --- ");
    Serial.println(right + 1);

    reactionTime = reactionTest(rightLED[right], rightBTN[right]);

    Serial.print("Czas: ");
    Serial.print(reactionTime);
    Serial.println(" ms");

    sumTime += reactionTime;
    buttons++;

    if (reactionTime < MINreaction) MINreaction = reactionTime;
    if (reactionTime > MAXreaction) MAXreaction = reactionTime;

    repeats++;
  }

  int avgtime = sumTime / buttons;

  Serial.println("Sredni czas:");
  Serial.println(avgtime);

  Serial.println("MIN:");
  Serial.println(MINreaction);

  Serial.println("MAX:");
  Serial.println(MAXreaction);

  String result = String(avgtime) + "," + String(MINreaction) + "," + String(MAXreaction);

  resultChar.writeValue(result);

  Serial.println("Wyslano wynik:");
  Serial.println(result);
}

long reactionTest(int ledPin, int buttonPin) {

  digitalWrite(ledPin, HIGH);

  unsigned long startTime = millis();

  while (digitalRead(buttonPin) == HIGH) {

    if (millis() - startTime > 2000) {
      break;
    }

  }

  digitalWrite(ledPin, LOW);

  return millis() - startTime;
}

void MultiPlayerGame() {

  Serial.println("START MULTI");

  
  RPC.call("Start");

  long sum1 = 0;
  int min1 = 2000;
  int max1 = 0;

  for(int i=0;i<10;i++){

    int left = random(0,10);

    long r = reactionTest(leftLED[left], leftBTN[left]);

    sum1 += r;

    if(r > 0 && r < min1) min1 = r;
    if(r > 0 && r > max1) max1 = r;

  }

  long avg1 = sum1 / 10;

  
  while(RPC.call("IsFinished").as<bool>() == false){
  Serial.println("Oczekiwanie na M4");
  delay(100);
  }
  

  long avg2 = RPC.call("GetAvg").as<long>();
  long min2 = RPC.call("GetMin").as<long>();
  long max2 = RPC.call("GetMax").as<long>();

  Serial.println("wyniki:");

  Serial.print("Player1 AVG: ");
  Serial.println(avg1);

  Serial.print("Player2 AVG: ");
  Serial.println(avg2);

  
  String part1 = String(avg1) + "," + min1 + "," + max1 + ",";

  String part2 = String(avg2) + "," + min2 + "," + max2;

  resultChar.writeValue(part1);
  Serial.println("Wyslano wynik:");
  Serial.println(part1);
  delay(50); 
  resultChar.writeValue(part2);
  Serial.println("Wyslano wynik:");
  Serial.println(part2);  

}





