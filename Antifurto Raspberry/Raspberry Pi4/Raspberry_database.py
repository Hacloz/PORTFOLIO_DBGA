import mysql.connector
import time
import paho.mqtt.client as paho
from mysql.connector import Error

# Funzione per ottenere l'ultimo stato dell'allarme dal database
def ultimoAccessostatoAllarme():
    value = False
    try:
        # Connessione al database MySQL
        connection = mysql.connector.connect(
            host='localhost',
            database='sicurezza',
            user='sicurezza',
            password='hamzamihali123'
        )

        if connection.is_connected():
            cursor = connection.cursor()
            # Query per ottenere l'ultimo stato dell'allarme
            query = """
                SELECT stato_allarme
                FROM iot
                WHERE ID_ultimostato = (SELECT MAX(ID_ultimostato) FROM iot);
            """
            cursor.execute(query)
            results = cursor.fetchall()

            print("Ultima data e stato di allarme:")
            for result in results:
                value = result[0]

    except Error as e:
        print(f"Errore durante la connessione al database: {e}")
    finally:
        if connection.is_connected():
            cursor.close()
            connection.close()
            print("Connessione al database chiusa.")
    return value

# Funzione di callback per la gestione dei messaggi MQTT in arrivo
def onMessage(cliente, userdata, msg):
    print(msg.topic + ":" + msg.payload.decode())
    messaggio = str(msg.payload.decode())
    if ultimoAccessostatoAllarme():
        if messaggio == "rilevato":
            client.publish("allarme/output", "on")
        else:
            client.publish("allarme/output", "off")

# ID del client MQTT
id = "Ciao"
client = paho.Client(paho.CallbackAPIVersion.VERSION1, id)
client.on_message = onMessage

# Connessione al broker MQTT
if client.connect("localhost", 1883, 60) != 0:
    print("no")
    sys.exit(-1)

# Sottoscrizione al topic "allarme/sensore"
client.subscribe("allarme/sensore")

# Attesa di mezzo secondo
time.sleep(0.5)

# Pubblicazione dello stato iniziale del LED in base all'ultimo stato dell'allarme
if ultimoAccessostatoAllarme():
    client.publish("led/stato", "on")
    print("ON")
else:
    client.publish("led/stato", "off")
    print("OFF")

# Loop principale per mantenere il client MQTT in esecuzione
try:
    print("ctrl c")
    client.loop_forever()
except:
    print("preso")

client.disconnect()
