#include <RPC.h>

int rightLED[9] = {41,43,45,47,49,51,53,9,11};
int rightBTN[9] = {40,42,44,46,48,50,52,8,10};

int avg2;
int min2;
int max2;

bool gameRunning = false;

bool finished = false;

bool IsFinished(){
  return finished;
}

void Start() {

  gameRunning = true;
  finished = false;
}

int GetAvg(){ return avg2; }
int GetMin(){ return min2; }
int GetMax(){ return max2; }


void setup() {

  
  Serial.begin(9600);

  RPC.println("M4 start");

  for(int i=0;i<9;i++){
    pinMode(rightLED[i], OUTPUT);
    pinMode(rightBTN[i], INPUT_PULLUP);
  }

  RPC.begin();

  RPC.bind("Start", Start);
  RPC.bind("GetAvg", GetAvg);
  RPC.bind("GetMin", GetMin);
  RPC.bind("GetMax", GetMax); 
  RPC.bind("IsFinished", IsFinished);
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

void loop() {

  if(gameRunning){
    
    long sum = 0;
    min2 = 2000;
    max2 = 0;

    for(int i=0;i<10;i++){

      int right = random(0,10);

      long r = reactionTest(rightLED[right], rightBTN[right]);

      sum += r;

      if(r > 0 && r < min2) min2 = r;
      if(r > 0 && r > max2) max2 = r;

    }

    avg2 = sum / 10;

    gameRunning = false;
    finished = true;

  }

}