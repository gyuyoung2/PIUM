#include "DHT.h"
#define DHTTYPE DHT11
#define DHTPIN 18

DHT dht(DHTPIN, DHTTYPE);

#if defined(ESP32)
#include <WiFi.h>
#include <FirebaseESP32.h>
#elif defined(ESP8266)
#include <ESP8266WiFi.h>
#include <FirebaseESP8266.h>
#endif

// Provide the token generation process info.
#include <addons/TokenHelper.h>

// Provide the RTDB payload printing info and other helper functions.
#include <addons/RTDBHelper.h>

/* 1. Define the WiFi credentials */
#define WIFI_SSID "1234"
#define WIFI_PASSWORD "12345678"

// For the following credentials, see examples/Authentications/SignInAsUser/EmailPassword/EmailPassword.ino

/* 2. Define the API Key */
#define API_KEY "AIzaSyB5xIocN1ClflxLDiNr81kBeW9WPG8HnhI"

/* 3. Define the RTDB URL */
#define DATABASE_URL "fordelete-5db5a-default-rtdb.firebaseio.com" //<databaseName>.firebaseio.com or \
                           //<databaseName>.<region>.firebasedatabase.app

/* 4. Define the user Email and password that alreadey registerd or added in
 * your project */
#define USER_EMAIL "sktinovation@gmail.com"
#define USER_PASSWORD "123456"

// Define Firebase Data object
FirebaseData fbdo;

FirebaseAuth auth;
FirebaseConfig config;

void setup()
{

  Serial.begin(115200);
  Serial.println();
  Serial.println();

  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Connecting to Wi-Fi");
  while (WiFi.status() != WL_CONNECTED)
  {
    Serial.print(".");
    delay(300);
  }
  Serial.println();
  Serial.print("Connected with IP: ");
  Serial.println(WiFi.localIP());
  Serial.println();

  Serial.printf("Firebase Client v%s\n\n", FIREBASE_CLIENT_VERSION);

  /* Assign the api key (required) */
  config.api_key = API_KEY;

  /* Assign the user sign in credentials */
  auth.user.email = USER_EMAIL;
  auth.user.password = USER_PASSWORD;

  /* Assign the RTDB URL (required) */
  config.database_url = DATABASE_URL;

  /* Assign the callback function for the long running token generation task */
  config.token_status_callback = tokenStatusCallback; // see addons/TokenHelper.h

  // Or use legacy authenticate method
  // config.database_url = DATABASE_URL;
  // config.signer.tokens.legacy_token = "<database secret>";

  // To connect without auth in Test Mode, see Authentications/TestMode/TestMode.ino

  Firebase.begin(&config, &auth);

  dht.begin();

  Firebase.reconnectWiFi(true);
#if defined(ESP8266)
  fbdo.setBSSLBufferSize(512, 2048);
#endif
}

void loop()
{
  int m=analogRead(35);

  int moisture=map(m,4095,2500,0,100);  

  if (moisture>=100){
      moisture=100;
  }
   
  float humidity = dht.readHumidity();       
    
  Firebase.setIntAsync(fbdo, "102634219531785912704/1/Plant1/moisture", moisture);      

  Firebase.setIntAsync(fbdo, "102634219531785912704/1/Humidity/humidity", humidity);  
          
  delay(500);
     
}
