package Project_Tombola;

import java.net.*; 
import java.io.*; 

class ServerThread extends Thread { 
	ServerSocket server      = null;
	Socket client            = null;
	String stringaRicevuta   = null;
	int numeroclient=1;
	String stringaModificata = null;
	BufferedReader   inDalClient; 
	DataOutputStream outVersoClient;
	
	public ServerThread (Socket socket, int nclient){ 
		this.client = socket; 
		numeroclient=nclient;
	}
	
	//tenta di entrare in comunicazione con il client , se non riesce stampa errore
	public void run(){ 
		try{
			comunica();  
		}catch (Exception e){ 
			e.printStackTrace(System.out);
		} 
	} 
  
	public void comunica ()throws Exception{ 
		//crea una schedina e la passa ad un client
		inDalClient      = new BufferedReader(new InputStreamReader (client.getInputStream()));
		outVersoClient   = new DataOutputStream(client.getOutputStream());
		int[] arr=new int[15];
		int temp;
		boolean ripetizioni=false;
		outVersoClient.write(numeroclient);
		do {
	    	ripetizioni=false;
		    for(int i=0; i<15; i++) {//ciclo che da 15 numeri (1 tabella di tombola) ad 1 client
		    	temp=(int)(Math.random()*89)+1;//generazione numero casuale da 1 a 90
		    	arr[i]=temp;//inserisce i numeri nell'array
		    }
		    for(int i=0; i<15; i++) {//ciclo di verifica per le ripetizioni
		    	temp=arr[i];//inserimento valori array in var temporale
		    	for(int j=0;j<15;j++) {
		    		if(i!=j) { //controllo per posizione di ogni numero per eventuali ripetizioni
		    			if(temp==arr[j]) {
			    			ripetizioni=true;
			    		}
			    	}
		    	}
		    }
		}while(ripetizioni);
		String damandare="";
		for(int i=0; i<15; i++) {//aggiunta numeri NON ripetuti in una stringa
			damandare=damandare+arr[i]+" ";
		}
		damandare=damandare+"\n";
		outVersoClient.writeBytes(damandare.toString());
	}
	
	//ciclo che da i numeri
	public void dainumeri(int n) throws IOException{ 
		outVersoClient   = new DataOutputStream(client.getOutputStream());
		try{
			outVersoClient.write(n);
		}catch (Exception e){ 
			e.printStackTrace(System.out);  
	   	}
	}
  
	//il server legge se il client ha fatto punti
	public String ottienirisposta() throws IOException{ 
		inDalClient    = new BufferedReader(new InputStreamReader (client.getInputStream()));
		String risposta="";
		try{
			risposta=inDalClient.readLine();
		}catch (Exception e){ 
			e.printStackTrace(System.out);  
	    } 
		return risposta;
	}
  
	//metodo che manda messaggio di vincita al client
	//comunica ai client se e' stato fatto qualche punto e da chi e' stato fatto
	public void mandavincitore(String risposta, int nclient) throws IOException, InterruptedException{ 
	    outVersoClient   = new DataOutputStream(client.getOutputStream());
	    if(risposta.equalsIgnoreCase("tombola")) {//controllo : solo se e' stata fatta "tombola" comunica che il gioco finisce
	    	System.out.println("il gioco e' finito");
	    }
    	outVersoClient.writeBytes(risposta);//comunica il punto
    	outVersoClient.write(nclient);//comunica il client
	}
  
  
	
}//fine classe : ServerThread

public class tombolaserver{
	BufferedReader tastiera;
	int numero_giocatori;
	int n;//numero estratto
	boolean verifica=false;
	boolean[] numeriUsciti = new boolean[90];
	static int turno = 1;

	public void numero(){//genera un numero casuale
		boolean numeroDoppio=true;	//controllo numeri duplicati
		while(numeroDoppio) {
			n=(int)(Math.random()*89)+1;
			if(!numeriUsciti[n-1]) {//controllo se il numero prima e' diverso al numero generato
				numeriUsciti[n-1]=true;
				numeroDoppio=false;
			}
		}
		System.out.println("\n -- TURNO " + turno + " --\n");
		System.out.println("il numero estratto e': "+n);//stampa il numero estratto
		System.out.println("Premi invio per continuare"); 
		waitForEnter();
		System.out.println("--------------------------------------------------");
	    turno ++;

	}
	private static void waitForEnter() {
		 BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		 try { 
			 reader.readLine(); 
			 }
		 catch (IOException e) { 
			 e.printStackTrace(); 
			 }
	}
	public void start(){
		int nclient=1;
		try{
			tastiera = new BufferedReader(new InputStreamReader(System.in));
			//crea un thread per generare un altro socket per connetterlo al client
			System.out.println("Numero giocatori:");
			numero_giocatori = Integer.parseInt(tastiera.readLine());
			ServerSocket serverSocket = new ServerSocket(6789);
			ServerThread[] serverThread = new ServerThread[numero_giocatori];
			for (int i=0; i<numero_giocatori;i++) 
			{//creazione di thread per ogni client
				System.out.println("Server in attesa del client n."+(i+1)); 
				Socket socket = serverSocket.accept();
				System.out.println("Server socket  " + socket); 
				serverThread[i] = new ServerThread(socket, nclient); 
				serverThread[i].start(); 
                nclient++;

			}      
			String rispostadamandare="0\n";
			while(!rispostadamandare.equalsIgnoreCase("tombola\n")) {//ciclo che va all'infinito finche un client non fa tombola e finisce la partita, se ciÃ² non avviene vengono continuamente dati dei numeri.
				numero();
				for(int i=0; i<numero_giocatori; i++) {
					//waitForEnter();
					serverThread[i].dainumeri(n);
					Thread.sleep(10);
				}
				rispostadamandare="0\n";
				for(int i=0; i<numero_giocatori; i++) {	//scorre tutti i client per controllare i punteggi
					String risp=serverThread[i].ottienirisposta();
					if(risp.equalsIgnoreCase("ambo")||risp.equalsIgnoreCase("terna")||risp.equalsIgnoreCase("quaterna")||risp.equalsIgnoreCase("cinquina")||risp.equalsIgnoreCase("tombola")) {
						//se hanno fatto dei punti scrive il punteggio ed il client che lo ha fatto 
						rispostadamandare=risp+"\n";
						nclient=i+1;
					}
			    	Thread.sleep(10); //il thread va a mimir
				}
				for(int i=0; i<numero_giocatori; i++) {
					serverThread[i].mandavincitore(rispostadamandare, nclient);
				}

		    	Thread.sleep(10);
				for(int i=0; i<numero_giocatori; i++) {
					serverThread[i].ottienirisposta();
					Thread.sleep(10);
				}
				Thread.sleep(10);
				for(int i=0; i<numero_giocatori; i++) {
					if(rispostadamandare.equalsIgnoreCase("tombola\n")) {
						//se un client ha fatto tombola si chiude la comunicazione con i client 
						serverThread[i].stop();
					}
					if(rispostadamandare.equalsIgnoreCase("tombola\n")&&i==4) {
						//viene chiuso il server se un client ha fatto tombola 
						serverSocket.close();
					}
				}
			}
		}
		catch (Exception e){
			System.out.println(e.getMessage());
			System.out.println("Errore durante l'istanza del server !");
			System.exit(1);
		}
	} 

	public static void main (String[] args){ 
		tombolaserver tcpServer = new tombolaserver(); 
		//inizia il giocoo!!! 
		tcpServer.start();
		System.out.println("GIOCO FINITO!!!");
	} 
}//fine classe : tombolaserver