#include <ESP8266WiFi.h>
#include <PubSubClient.h>

// Definizione delle credenziali WiFi
const char *ssid = "TIM-27628435";   // SSID della rete WiFi
const char *password = "Samuelduro"; // Password della rete WiFi

// Definizione dei pin del buzzer e dei LED
#define buzzer D3          // Pin del buzzer
#define GREEN D6           // Pin del LED verde
#define RED D7             // Pin del LED rosso 

// Definizione del nome del dispositivo e del topic MQTT
const char *ID = "Led";  // Nome del dispositivo
const char *TOPIC = "allarme/output";  // Topic a cui sottoscriversi

// Definizione dell'indirizzo IP del broker MQTT
IPAddress broker(192,168,1,219); // IP del broker
WiFiClient wclient;

// Configurazione del client MQTT
PubSubClient client(wclient); // Imposta il client MQTT

// Funzione di callback per gestire i messaggi in arrivo dal broker
void callback(char* topic, byte* payload, unsigned int length) {
  String response;
  for (int i = 0; i < length; i++) {
    response += (char)payload[i];
  }
  Serial.println(response);
  if(response == "on"){
    digitalWrite(RED, LOW);// Led rosso spento
    digitalWrite(GREEN, HIGH);// Led verde acceso
  } else {
    digitalWrite(GREEN, LOW);// Led verde spento
    digitalWrite(RED, HIGH); // Led rosso accesso
    tone(buzzer, 1000, 500); // Emette un suono dal buzzer
  }
}

// Funzione per connettersi alla rete WiFi
void setup_wifi() {
  Serial.print("\nConnecting to ");
  Serial.println(ssid);
  WiFi.persistent(false);
  WiFi.begin(ssid, password); // Connessione alla rete WiFi

  while (WiFi.status() != WL_CONNECTED) { // Attesa della connessione
    delay(500);
    Serial.print(".");
  }
  digitalWrite(RED, LOW);// Led rosso accesso

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
      client.subscribe(TOPIC); // Sottoscrizione al topic
      Serial.println("connected");
      Serial.print("Subscribed to: ");
      Serial.println(TOPIC);
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
  pinMode(GREEN, OUTPUT); // Configurazione del pin del LED verde come output
  pinMode(RED, OUTPUT); // Configurazione del pin del LED rosso come output
  delay(100);
  setup_wifi(); // Connessione alla rete WiFi
  client.setServer(broker, 1883); // Imposta il server MQTT
  client.setCallback(callback); // Inizializza la routine di callback
  pinMode(buzzer, OUTPUT); // Configurazione del pin del buzzer come output
}

// Funzione di loop, viene eseguita ripetutamente
void loop() {
  if (!client.connected()) { // Riconnessione se la connessione è persa
    reconnect();
  }
  client.loop(); // Mantiene la connessione al client MQTT
}
