package it.polimi.iodice_moro.network;

import it.polimi.iodice_moro.controller.Controller;
import it.polimi.iodice_moro.exceptions.PartitaIniziataException;
import it.polimi.iodice_moro.model.StatoPartita;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerConnessione implements Runnable {

	private Controller controller;
	private ServerSocket serverSocket;
	private Map<Color, Socket> socketGiocatori = new HashMap<Color, Socket>();
	boolean partitaIniziata;
	private ViewSocket view;
	
	private static final Logger logger =  Logger.getLogger("it.polimi.iodice_moro.network");
	
	public ServerConnessione(Controller controller, ViewSocket view, ServerSocket server) {
		this.controller=controller;
		partitaIniziata=false;
		this.view=view;
		this.serverSocket=server;
	}

	@Override
	public void run() {
		//accetta la richiesta dal client, il quale gli passa il nome del nuovo giocatore
		//chiama il metodo creaGiocatore del controller, il quale gli torna il colore del giocatore appena creato
		//e con il colore inserisco il socket nella hashmap
		while(!serverSocket.isClosed()){
			try{
				Thread.sleep(10);
				//serverSocket.accept() è bloccante
				Socket nuovoGiocatore = serverSocket.accept();

				BufferedReader in;
				in = new BufferedReader(
						new InputStreamReader(nuovoGiocatore.getInputStream()));
				PrintWriter out = new PrintWriter(nuovoGiocatore.getOutputStream());
				synchronized(this){
					//Controllo se tutti i socket sono ancora connessi
					boolean tuttiConnessi=true;
					for(Entry<Color, Socket> s:socketGiocatori.entrySet()){
						if(s.getValue().isClosed()){
							tuttiConnessi=false;
						}
					}
					String nome = in.readLine();
					if(tuttiConnessi){
						if(partitaIniziata){
						//se sono tutti connessi e la partita è gia iniziata, devo rifiutare la connessione
						out.println("NO");
						out.flush();
						nuovoGiocatore.close();
						} else{
							//se tutti sono connessi, ma la partita deve ancora iniziare, aggiungo il client agli altri giocatori
							System.out.println("Accettata connessione da IP: "+nuovoGiocatore.getInetAddress());
							//creo il nuovo giocatore
							Color colore;
							try {
								colore = controller.creaGiocatore(nome);
								System.out.println("Colore: "+colore + "NOME: "+nome);
								socketGiocatori.put(colore, nuovoGiocatore);
								out.println(colore.getRGB());
								out.flush();
								view.addClient(nuovoGiocatore, out, colore);
								//devo creare il nuovo threda per ascoltare e spedire i messaggi!
								//se ci sono almeno 2 giocatori ed è passato più di mezzo minuto da quando si è connesso il primo utente
								//allora faccio iniziare la partita
								ServerMessageReader messageReader = new ServerMessageReader(controller, nuovoGiocatore, out, in);
								Thread t = new Thread(messageReader);
								t.start();
							} catch (PartitaIniziataException e) {
								//Comunico l'errore di connessione
								logger.log(Level.SEVERE, "Partita iniziata", e);
								out.println("NO");
								out.flush();
								nuovoGiocatore.close();
							}
						}
					}else{
						//Se qualcuno si è disconnesso, mi occuperò di chiudere la vecchia partita, comprese le connessioni, che sia stata avviata o meno,
						//e di aprirne una nuova a cui aggiungerò l'utente che si è connesso
						
						//Chiusura delle connessioni ancora aperte con i vecchi client
						view.close();
						//chiudo anche il serveSocket di questo thread, 
						//perchè verrà avviato un altro thread di ascolto delle connessioni
						serverSocket.close();
						//istanzio il nuovo controller con il nuovo statoPartita
						controller=new Controller(new StatoPartita());
						//istanzio la nuova View
						view=new ViewSocket(controller);
						controller.setView(view);
						partitaIniziata=false;
						//azzero la mappa dei socket dei giocatori
						socketGiocatori=new HashMap<Color, Socket>();
						//creo il nuovo thread su cui girerà il controller
						Thread t = new Thread(new Runnable(){
							@Override
							public void run() {
								try {
									view.attendiGiocatori();
								} catch (IOException e) {
									logger.log(Level.SEVERE, "Errore di IO", e);
								}
							}
						});
						t.start();
						
						//Creo il nuovo giocatore
						Color colore;
						try {
							colore = controller.creaGiocatore(nome);
							System.out.println("Colore: "+colore + "NOME: "+nome);
							socketGiocatori.put(colore, nuovoGiocatore);
							out.println(colore.getRGB());
							out.flush();
							view.addClient(nuovoGiocatore, out, colore);
							//devo creare il nuovo threda per ascoltare e spedire i messaggi!
							ServerMessageReader messageReader = new ServerMessageReader(controller, nuovoGiocatore, out, in);
							Thread t2 = new Thread(messageReader);
							t2.start();
						} catch (PartitaIniziataException e) {
							logger.log(Level.SEVERE, "Partita iniziata", e);
							//Comunico l'errore di connessione
							out.println("NO");
							out.flush();
							
							nuovoGiocatore.close();
						}
					}
				}
			}catch(InterruptedException | IOException e){
				logger.log(Level.SEVERE, "Errore di IO", e);
			}
		}
	}
}