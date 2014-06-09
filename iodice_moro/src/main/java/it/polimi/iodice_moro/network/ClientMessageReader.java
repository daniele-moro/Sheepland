package it.polimi.iodice_moro.network;

import it.polimi.iodice_moro.model.TipoTerreno;
import it.polimi.iodice_moro.model.Giocatore;
import it.polimi.iodice_moro.view.IFView;
import it.polimi.iodice_moro.view.ThreadAnimazionePastore;
import it.polimi.iodice_moro.view.ThreadAnimazionePecoraBianca;
import it.polimi.iodice_moro.view.ThreadAnimazionePecoraNera;
import it.polimi.iodice_moro.view.View;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ClientMessageReader implements Runnable{

	private View view;
	private BufferedReader input;
	private Socket socket;
	
	public ClientMessageReader(IFView view2, Socket socket,BufferedReader scInput) {
		this.view=(View)view2;
		this.input=scInput;
		this.socket=socket;
	}

	/**
	 * Ricevo tutte le richeiste che riguardano azioni che il server vuole eseguire sulla view in locale
	 * comprese le eccezioni generate dalle chiamate ai metodi
	 * 
	 */
	@Override
	public void run() {
		//RICEVE TUTTE LE RISPOSTE DAL SERVER
		while(!socket.isClosed()){
			try {
				//Metto in sleep il thread per dare non occupare tutte le risorse del PC
				Thread.sleep(10);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			try {
				//Eseguo il lock sullo stream di input,
				//che potrebbe esssere usato anche in altri metodi dal client
				synchronized(input){
					//Controllo se c'è qualcosa in input
					if(socket.isClosed()){
					}
					if(input.ready()){
						//Leggo cosa c'è in input, leggo fino al carattere di "andata a capo"
						String risposta = input.readLine();
						System.out.println("RICEZIONE: "+risposta);
						//Splitto la stringa in tante sottostringhe
						//che mi rappresentano i parametri del messaggio mandato dal serve
						//(i parametri sono divisi dal carattere #)
						String[] parametri= risposta.split("#");
						//In base al contenuto del primo parametro so cosa riguarda la richiesta del serve
						switch(parametri[0]){

						case "MOD_QTA_TESS":{
							//Modifica della quantità di una determinata tessera
							TipoTerreno tess= TipoTerreno.parseInput(parametri[1]);
							int num = Integer.parseInt(parametri[2]);
							Color colore = new Color(Integer.parseInt(parametri[3]));
							
							view.modQtaTessera(tess, num,colore);
						}break;

						case "GIOC_CORR":{
							//Modifica del giocatore corrente (identificato dal proprio colore)
							Color colore = new Color(Integer.parseInt(parametri[1]));
							
							view.setGiocatoreCorrente(colore);
						}break;

						case "SPOSTA_PECORA_BIANCA":{
							//Animazione di spostamento della pecora bianca
							ThreadAnimazionePecoraBianca r = new ThreadAnimazionePecoraBianca(view, parametri[1], parametri[2]);
							Thread t = new Thread(r);
							t.start();
						}break;

						case "SPOSTA_PECORA_NERA":{
							//Animazione di spostamento della pecora nera
							System.out.println("spostamento pecora nera!!");
							/*ThreadAnimazionePecoraNera r2 = new ThreadAnimazionePecoraNera(view, parametri[1], parametri[2]);
							Thread t2 = new Thread(r2);
							t2.start();*/
							view.spostaPecoraNera(parametri[1], parametri[2]);
						}break;

						case "SPOSTA_PASTORE":{
							//Animazione di spostamento del pastore
							ThreadAnimazionePastore r3 = new ThreadAnimazionePastore(view, parametri[1], parametri[2], new Color(Integer.parseInt(parametri[3])));
							Thread t3 = new Thread(r3);
							t3.start();
						}break;

						/*case "ATTIVA":{
							//Attivazione della view che è collegata al clienti a cui fa riferimento questo oggetto
							Color colore = new Color(Integer.parseInt(parametri[1]));
							view.attivaGiocatore();
							view.cambiaGiocatore(colore);
						}break;

						case "DISATTIVA":{
							//Disattivazione della view che è collegata al client a cui fa riferimento questo oggetto
							Color colore = new Color(Integer.parseInt(parametri[1]));
							view.disattivaGiocatore();
							view.cambiaGiocatore(colore);
						}break;*/
						case "CAMBIA_GIOCATORE":{
							Color colore = new Color(Integer.parseInt(parametri[1]));
							view.cambiaGiocatore(colore);
						}break;

						case "INIT_MAPPA":
							//Comando di inizializzare la mappa
							System.out.println("INIT MAPPA CLIENT");
							view.initMappa();
							break;

						case "MOD_SOLDI_GIOC":{
							//Modifica dei soldi posseduti da un determinato giocatore (identificato dal proprio colore)
							Color colore = new Color(Integer.parseInt(parametri[1]));
							view.modSoldiGiocatore(colore, Integer.parseInt(parametri[2]));
						}break;

						case "MOD_PREZZO_TESS":
							//Modifica del prezzo della tessera di determinato un terreno
							view.incPrezzoTessera(TipoTerreno.parseInput(parametri[1]));
							break;

						case "EXCEPTION":
							//ECCEZIONE, viene visualizzata nella label dedicata alle comunicazioni con l'utente
							view.getLBLOutput().setText(parametri[1]);
							break;

						case"ADD_CANC_NORM":
							//Agginta di un cancello normale ad una strada identificata dal proprio id (stringa)
							view.addCancelloNormale(parametri[1]);
							break;

						case "ADD_CANC_FIN":
							//aggiunta di un cancello finale ad una strada identificata dal proprio id (stringa)
							view.addCancelloFinale(parametri[1]);
							break;

						case "MOD_QTA_PEC":
							//modifica della quantita di pecore contenute in un determinato terreno
							view.modificaQtaPecora(parametri[1], Integer.parseInt(parametri[2]));
							break;
						
						case "RIS_DADO":{
							System.out.println("Visualizzazione del risultato del dado");
							view.visRisDado(Integer.parseInt(parametri[1]));
						}break;

						case "FINE_PARTITA":{
							//FINE DELLA PARTITA
							//prelevo le informazioni dei punteggi dei giocatori
							Map<Giocatore, Integer> punteggiOrdinati = new LinkedHashMap<Giocatore, Integer>();  
							//LETTURA DELLA RISPOSTA
							risposta="";
							try {
								risposta = input.readLine();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							System.out.println(risposta);
							parametri = risposta.split("#");

							while(!parametri[0].equals("END")){
								Giocatore gioc = new Giocatore(parametri[0],
										new Color(Integer.parseInt(parametri[1])));
								Integer punteggio= Integer.parseInt(parametri[2]);
								punteggiOrdinati.put(gioc, punteggio);

								//leggo il prossimo punteggio
								try {
									risposta = input.readLine();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								parametri = risposta.split("#");
							}
							//chiamo ora il metodo della view che deve visualizzare i punteggi dei giocatori
							view.visualizzaPunteggi(punteggiOrdinati);
							//ORA DEVO CHIUDERE LA CONNESSIONE

						}break;
						default:
							break;

						}
					}
				}
			} catch (NumberFormatException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("CHIUSURA THREAD CONTROLLER CLIENT");
	}
}
