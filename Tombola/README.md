# TOMBOLA
Il progetto "Tombola Java" è un'implementazione di un classico gioco della tombola, sviluppato in linguaggio Java, che consente a più giocatori di partecipare simultaneamente. Questo progetto include sia il lato client che il lato server. La tombola è un gioco di fortuna in cui i giocatori cercano di coprire i numeri sulle loro cartelle in base a quelli estratti casualmente.

## STRUTTURA DEL PROGETTO
1. ***TombolaClient***: Questa classe rappresenta il client nel gioco della tombola. Si occupa della connessione al server, riceve la cartella numerata, ascolta i numeri estratti e verifica se ha ottenuto una combinazione vincente. Utilizza socket per la connessione e comunica con il server tramite BufferedReader e DataOutputStream. La cartella del giocatore è una matrice 3x5, e una matrice di booleani traccia i numeri estratti. I metodi principali gestiscono la comunicazione, l'ottenimento dei numeri, la verifica delle vincite e le risposte al server.

2. ***TombolaServer***: Questa classe rappresenta il server del gioco. Gestisce le connessioni dei client, distribuisce le cartelle numerate, estrae numeri casuali e determina le vincite. Utilizza ServerSocket per ascoltare le connessioni in ingresso e una classe interna ServerThread per gestire la comunicazione con ciascun client in un thread separato. Un array di booleani traccia i numeri già estratti. I metodi principali sono responsabili della generazione dei numeri casuali, della comunicazione con i client e della gestione delle vincite.

## FUNZIONAMENTO DEL GIOCO
Il gioco inizia con l'avvio del server, che specifica il numero di giocatori e attende le connessioni. I client si connettono al server e ricevono le loro cartelle numerate. Durante il gioco, il server estrae numeri casuali e li invia ai client. I client verificano se i numeri estratti sono presenti nelle loro cartelle e li segnano come usciti. Dopo ogni estrazione, i client controllano le combinazioni vincenti e, se trovano una combinazione, la comunicano al server. Il server annuncia le vincite a tutti i giocatori. Quando un client ottiene la "tombola", il gioco termina, il server chiude tutte le connessioni e l'esecuzione si conclude.