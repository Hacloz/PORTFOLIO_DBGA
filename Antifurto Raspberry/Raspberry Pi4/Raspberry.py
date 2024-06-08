import paho.mqtt.client as paho
import sys

# Funzione di callback per la gestione dei messaggi MQTT in arrivo
def onMessage(cliente, userdata, msg):
    print(msg.topic + ":" + msg.payload.decode())
    messaggio = str(msg.payload.decode())
    if messaggio == "rilevato":
        print("rilevato")
    elif messaggio == "apri":
        print("accendo")
    else:
        print("no")

# ID del client MQTT
id = "Ciao"
client = paho.Client(paho.CallbackAPIVersion.VERSION1, id)
client.on_message = onMessage

# Connessione al broker MQTT
if client.connect("localhost", 1883, 60) != 0:
    print("no")
    sys.exit(-1)

# Sottoscrizione al topic "allarme/movimento"
client.subscribe("allarme/movimento")

# Loop principale per mantenere il client MQTT in esecuzione
try:
    print("ctrl c")
    client.loop_forever()
except:
    print("#")

# Disconnessione dal broker MQTT
client.disconnect()
