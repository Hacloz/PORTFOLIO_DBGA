package Project_Tombola;
import java.io.*;
import java.net.*;
public class tombolaclient {
  String nomeServer ="localhost";                  // indirizzo server locale o anche remoto  
  int portaServer   = 6789;                        // porta x servizio data e ora
  Socket miosocket;   
  String[] finale=new String[5];
  int nfinale=0;
  static int numeroclient;
  BufferedReader tastiera;                         // buffer per l'input da tastiera
  String stringaUtente;                            // stringa inserita da utente
  String stringaRicevutaDalServer;                 // stringa ricevuta dal server
  DataOutputStream outVersoServer;                 // stream di output
  BufferedReader inDalServer;                      // stream di input 
  int[][] cartella = new int[3][5];			//array che contiene i numeri della nostra tabella
  boolean[][] usciti = new boolean[3][5];	//arrey dei numeri della nostra tabella che sono usciti
  int premi=0;	//0 ambo | 1 terna | 2 quaterna | 3 cinquina | 4 tombola (non ancora fatti i punti)
  boolean vittoria=false;
  static int turno = 1;
  public void comunica() {  //dice che numero di client e' e riceve una stringa con 15 numeri randomici non uguali 
    try{
      int conto=0;
      numeroclient=inDalServer.read();
      System.out.println("benvenuto, sei il client n."+numeroclient);
      System.out.println("la tua cartella e': \n");
      String risposta=inDalServer.readLine();
      String[] Array = risposta.split(" ");
      for(int j=0; j<3; j++) {
    	  for(int i=0; i<5; i++) {
    		  cartella[j][i]=Integer.parseInt(Array[conto]);
    		  if(Array[conto].length()==1) {//se il numero e' ad una cifra aggiunge uno spazio
    			  System.out.print(" ");
    		  }
    		  System.out.print(cartella[j][i]+" | ");
    		  conto++;
    	  }
    	  System.out.println();
       }
    }
    catch (Exception e) 
    {
      System.out.println(e.getMessage());
      System.out.println("Errore durante la comunicazione col server!");
      System.exit(1);
    }
  }
  
  public Socket connetti(){			//instaura la connessione con il server
    System.out.println("CLIENT partito in esecuzione ...");
    try{
      // input da tastiera
      tastiera = new BufferedReader(new InputStreamReader(System.in));
      //  miosocket = new Socket(InetAddress.getLocalHost(), 6789);
      miosocket = new Socket(nomeServer,portaServer);
      // associo due oggetti al socket per effettuare la scrittura e la lettura 
      outVersoServer = new DataOutputStream(miosocket.getOutputStream());
      inDalServer    = new BufferedReader(new InputStreamReader (miosocket.getInputStream()));
      
    } 
    catch (UnknownHostException e){
      System.err.println("Host sconosciuto"); } 
    catch (Exception e){
      System.out.println(e.getMessage());
      System.out.println("Errore durante la connessione!");
      System.exit(1);
    }
    return miosocket;
  }

  public void ottieninumero() throws IOException {//prende in input il numero dal server della tombala
	  int temp=inDalServer.read();
	  System.out.println();
	  System.out.println("\n -- TURNO " + turno + " --\n");
	  System.out.println("il numero che e' uscito e' il: "+temp);
	  turno ++;
	  for(int i=0; i<5; i++) {
    	  for(int j=0; j<3; j++) {
    		  if(cartella[j][i]==temp) {//verifica se il numero uscito e' presente nella cartella del client
    			  usciti[j][i]=true;
    			  System.out.println("il numero estratto e' presente nella tua cartella");
    		  }
    	  }
       }
  }
  
  public void premi() throws IOException, InterruptedException {//il server scrive al client se qualcuno ha vinto
	  String temp=inDalServer.readLine();//legge se qualcuno ha fatto un punto 
	  int nclient=inDalServer.read();//legge il numero del client che ha fatto un punto
	  switch (temp) {
		case "ambo": {
				premi=1;
				System.out.println("il client n."+nclient+" ha fatto ambo");
				System.out.println("--------------------------------------------------");
				finale[nfinale]="il client n."+nclient+" ha fatto ambo";
				nfinale++;
				outVersoServer.writeBytes("\n");// manda al server l'ack 
				break;
		}
		case "terna":{
				premi=2;
				System.out.println("il client n."+nclient+" ha fatto terna");
				System.out.println("--------------------------------------------------");
				finale[nfinale]="il client n."+nclient+" ha fatto terna";
				nfinale++;
				outVersoServer.writeBytes("\n");// manda al server l'ack 
				break;
		}
		case "quaterna":{
				premi=3;
				System.out.println("il client n."+nclient+" ha fatto quaterna");
				System.out.println("--------------------------------------------------");
				finale[nfinale]="il client n."+nclient+" ha fatto quaterna";
				nfinale++;
				outVersoServer.writeBytes("\n");// manda al server l'ack 
				break;
			}
		case "cinquina":{
				premi=4;
				System.out.println("il client n."+nclient+" ha fatto cinquina");
				System.out.println("--------------------------------------------------");
				finale[nfinale]="il client n."+nclient+" ha fatto cinquina";
				nfinale++;
				outVersoServer.writeBytes("\n");// manda al server l'ack 
				break;
		}
		case "tombola":{
				premi=5;
				System.out.println("il client n."+nclient+" ha fatto tombola");
				finale[nfinale]="il client n."+nclient+" ha fatto tombola";
				vittoria=true;
				break;
		}
		default:{
			System.out.println("nessuno ha ottenuto una combinazioni specifica");
			System.out.println("--------------------------------------------------");

			outVersoServer.writeBytes("\n");
			break;
		}
	}
  }
  
  public void rispondi() throws IOException {//il client manda al server se ha fatto punti
	  int contausciti=0, max=0;
	  for(int j=0; j<3; j++) {
		  if(premi<4) {//se manca solo la tombola non si ripristina, negli altri casi conta ogni riga da 0
			  contausciti=0;
		  }
    	  for(int i=0; i<5; i++) {
    		  if(usciti[j][i]==true) {
    			  contausciti++;
    		  }
    	  }
    	  if(contausciti>max) {
    		  max=contausciti; //salva il numero di numeri usciti nella riga con piu' numeri usciti
    	  }
    	  
	  }
	  switch (premi) {//manda i risultati delle verifiche al server
		case 0: {
			if(max==2) {
				premi=1;
				outVersoServer.writeBytes("ambo\n");
			}else {
				outVersoServer.writeBytes("0\n");
			}
			break;
		}
		case 1:{
			if(max==3) {
				premi=2;
				outVersoServer.writeBytes("terna\n");
			}else {
				outVersoServer.writeBytes("0\n");
			}
			break;
		}
		case 2:{
			if(max==4) {
				premi=3;
				outVersoServer.writeBytes("quaterna\n");
			}else {
				outVersoServer.writeBytes("0\n");
			}
			break;
			}
		case 3:{
			if(max==5) {
				premi=4;
				outVersoServer.writeBytes("cinquina\n");
			}else {
				outVersoServer.writeBytes("0\n");
			}
			break;
		}
		case 4:{
			if(max==15) {
				premi=5;
				outVersoServer.writeBytes("tombola\n");
			}else {
				outVersoServer.writeBytes("0\n");
			}
			break;
		}
	}
  }
  
  public static void main(String args[]) throws IOException, InterruptedException {
	  tombolaclient cliente = new tombolaclient();
    cliente.connetti();
    cliente.comunica();
    while(cliente.vittoria==false) {//il ciclo si ripete finche' qualcuno non vince
    	cliente.ottieninumero();
    	cliente.rispondi();
    	cliente.premi();
    }
    Thread.sleep(5000);
    System.out.println("\nIL GIOCO È FINITO");
	System.out.println("--------------------------------------------------");
    System.out.println("RIEPILOGO PARTITA:\n");
    for(int i=0;i<5;i++) {
    	System.out.println(cliente.finale[i]);
    }
    System.out.println("\nil tuo punto piu' alto e' stato:");
    int n=-1;
    int cli;
    for(int i=0; i<5;i++){
    	cli=Integer.parseInt(cliente.finale[i].substring(12,13));
    	if(cli==cliente.numeroclient) {
    		n=i;
    	}
    }
    if(n==-1) {
    	System.out.println("NESSUNA VINCITA :(");
    }else {
    	String risp=cliente.finale[n].substring(23);
    	System.out.println(risp);
    }
	cliente.miosocket.close(); 
  }   
}