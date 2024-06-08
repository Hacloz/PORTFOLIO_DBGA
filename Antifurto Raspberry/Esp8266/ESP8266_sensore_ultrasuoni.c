#include <ESP8266WiFi.h>
#include <PubSubClient.h>

// Definizione delle credenziali WiFi
const char *ssid = "CasaDeiDuri";   // SSID della rete WiFi
const char *password = "Samuelduro"; // Password della rete WiFi

// Definizione dei pin del sensore ad ultrasuoni
#define TRIG D2  // Pin di trigger del sensore
#define ECHO D1  // Pin di echo del sensore

const char *ID = "Sensore";  // Nome del dispositivo
const char *TOPIC = "allarme/output";  // Topic a cui sottoscriversi
const char *TOPIC_PUB = "allarme/sensore";  // Topic per pubblicare i dati del sensore

IPAddress broker(192,168,1,219); // Indirizzo IP del broker MQTT (Raspberry Pi)
WiFiClient wclient;

PubSubClient client(wclient); // Configurazione del client MQTT

// Funzione di callback per gestire i messaggi in arrivo dal broker
void callback(char* topic, byte* payload, unsigned int length) {
  // Funzione vuota, da implementare se necessario
}

// Funzione per connettersi alla rete WiFi
void setup_wifi() {
  Serial.print("\nConnecting to ");
  Serial.println(ssid);
  WiFi.begin(ssid, password); // Connessione alla rete WiFi

  while (WiFi.status() != WL_CONNECTED) { // Loop fino a che non si connette
    delay(1000);
    Serial.print(".");
  }

  WiFi.setAutoReconnect(true); // Auto-riconnessione
  WiFi.persistent(true); // Rende la connessione persistente

  Serial.println();
  Serial.println("WiFi connected");
  Serial.print("IP address: ");
  Serial.println(WiFi.localIP());
}

// Funzione per riconnettersi al client MQTT se la connessione è persa
void reconnect() {
  // Loop finché non ci si riconnette
  while (!client.connected()) {
    Serial.print("Attempting MQTT connection...");
    // Tentativo di connessione
    if(client.connect(ID)) {
      Serial.println("connected");
      Serial.print("Publishing into: ");
      Serial.println(TOPIC_PUB);
      Serial.println('\n');
    } else {
      Serial.println(" try again in 5 seconds");
      // Attesa di 5 secondi prima di riprovare
      delay(5000);
    }
  }
}

// Funzione di setup, viene eseguita una volta all'avvio
void setup() {
  Serial.begin(115200); // Avvio della comunicazione seriale a 115200 baud
  pinMode(TRIG, OUTPUT); // Configurazione del pin TRIG come output
  pinMode(ECHO, INPUT); // Configurazione del pin ECHO come input
  delay(100);
  setup_wifi(); // Connessione alla rete WiFi
  client.setServer(broker, 1883); // Imposta il server MQTT
  client.setCallback(callback); // Inizializza la routine di callback
}

// Funzione di loop, viene eseguita ripetutamente
void loop() {
  if (!client.connected()) { // Riconnessione se la connessione è persa
    reconnect();
  }
  client.loop(); // Mantiene la connessione al client MQTT

  // Lettura della distanza dal sensore ad ultrasuoni
  String response;
  digitalWrite(TRIG, LOW);
  digitalWrite(TRIG, HIGH);
  delayMicroseconds(10);
  digitalWrite(TRIG, LOW);
  unsigned long time = pulseIn(ECHO, HIGH);
  float cm = 0.03438 * time / 2; // Calcola la distanza in centimetri in base alla durata del segnale
  Serial.print("Cm = " + String(cm) + "cm\n");
  if (cm < 50) {
    client.publish(TOPIC_PUB, "rilevato"); // Pubblica "rilevato" se la distanza è inferiore a 50 cm
    Serial.print("rilevato\n");
  } else {
    client.publish(TOPIC_PUB, "non_rilevato"); // Pubblica "non_rilevato" se la distanza è superiore a 50 cm
    Serial.print("NO\n");
  }
  delay(500); // Attende 500 ms prima di ripetere il loop
}
