package it.polimi.iodice_moro.network;

import it.polimi.iodice_moro.model.Giocatore;
import it.polimi.iodice_moro.model.TipoTerreno;
import it.polimi.iodice_moro.view.IFView;
import it.polimi.iodice_moro.view.View;

import java.awt.Color;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientMessageReader implements Runnable{

	private View view;
	private BufferedReader input;
	private Socket socket;
	
	private static final Logger LOGGER =  Logger.getLogger("it.polimi.iodice_moro.network");
	
	/**
	 * Costruttore del ClientMessageReader.
	 * @param view2 View Associata.
	 * @param socket Socket Associato.
	 * @param scInput Da qui verranno lette le stringhe in ingresso.
	 */
	public ClientMessageReader(IFView view2, Socket socket,BufferedReader scInput) {
		this.view=(View)view2;
		this.input=scInput;
		this.socket=socket;
	}

	/**
	 * Ricevo tutte le richeiste che riguardano azioni che il server vuole eseguire sulla view
	 * comprese le eccezioni generate dalle chiamate ai metodi che verranno comunicate all'utente
	 */
	@Override
	public void run() {
		//RICEVE TUTTE LE RISPOSTE DAL SERVER
		while(!socket.isClosed()){
			try {
				//Metto in sleep il thread per dare non occupare tutte le risorse del PC
				Thread.sleep(10);
			} catch (InterruptedException e1) {
				LOGGER.log(Level.SEVERE, "Errore durante la thrad.sleep", e1);
			}
				//Eseguo il lock sullo stream di input,
				//che potrebbe esssere usato anche in altri metodi dal client
			synchronized(input){

				if(!socket.isClosed()){
					try {
						//Controllo se c'è qualcosa in input
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

							case "SPOSTA_PECORA_BIANCA":
								//Animazione di spostamento della pecora bianca
								view.spostaPecoraBianca( parametri[1], parametri[2]);
								break;

							case "SPOSTA_PECORA_NERA":
								//Animazione di spostamento della pecora nera
								System.out.println("spostamento pecora nera!!");
								view.spostaPecoraNera(parametri[1], parametri[2]);
								break;

							case "SPOSTA_LUPO":
								//Animazione di spostamento del lupo.
								System.out.println("spostamento lupo!!");
								view.spostaLupo(parametri[1], parametri[2]);
								break;

							case "SPOSTA_PASTORE":
								//Animazione di spostamento del pastore
								view.spostaPastore(parametri[1], parametri[2], new Color(Integer.parseInt(parametri[3])));
								break;
							
							case "POS_2_PAST":
								//Comando il posizionamento della seconda pedina sulla view
								view.posiziona2Pastore(parametri[1], new Color(Integer.parseInt(parametri[2])));
								break;
								
							case "G2_SELEZ_PAST":
								view.selezPast(new Color(Integer.parseInt(parametri[1])));
								break;
							
							case "USA_PAST_2":
								view.usaPast2(new Color(Integer.parseInt(parametri[1])));
								break;

							case "SET_POS_REG":{
								Map<String, Point> posRegioni= new HashMap<String,Point>();
								//LETTURA DELLA RISPOSTA
								String messaggio="";
								messaggio = input.readLine();

								System.out.println("rispostar icveuto ");
								String[] valori = messaggio.split("#");
								System.out.println(messaggio);
								while(!valori[0].equals("END")){
									posRegioni.put(valori[0], new Point(Integer.parseInt(valori[1]),Integer.parseInt(valori[2])));

									messaggio = input.readLine();
									valori = messaggio.split("#");
								}
								view.setPosizioniRegioni(posRegioni);

							}break;

							case "SET_POS_STR":{
								Map<String, Point> posStrade= new HashMap<String,Point>();
								//LETTURA DELLA RISPOSTA
								String messaggio="";
								messaggio = input.readLine();

								String[] valori = messaggio.split("#");
								System.out.println(messaggio);
								while(!valori[0].equals("END")){
									posStrade.put(valori[0], new Point(Integer.parseInt(valori[1]),Integer.parseInt(valori[2])));
									try {
										messaggio = input.readLine();
									} catch (IOException e) {
										LOGGER.log(Level.SEVERE, "Errore di IO", e);
									}
									valori = messaggio.split("#");
									System.out.println(messaggio);
								}
								view.setPosizioniStrade(posStrade);

							}break;

							case "SET_GIOC":{
								Map<Color,String> giocatori = new HashMap<Color,String>();
								String messaggio="";
								messaggio = input.readLine();

								System.out.println(messaggio);
								String[] valori = messaggio.split("#");

								while(!valori[0].equals("END")){
									Color colore = new Color(Integer.parseInt(valori[0]));
									String pos=valori[1];
									giocatori.put(colore,pos);
									messaggio = input.readLine();

									valori = messaggio.split("#");
								}
								view.setGiocatori(giocatori);

							}break;

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

							case "CLOSE":{
								//messaggio ricevuto alla chiusura dell'applicazione per vari motivi
								view.close();
							}break;

							case "FINE_PARTITA":{
								//FINE DELLA PARTITA
								//prelevo le informazioni dei punteggi dei giocatori
								Map<Giocatore, Integer> punteggiOrdinati = new LinkedHashMap<Giocatore, Integer>();  
								//LETTURA DELLA RISPOSTA
								risposta="";
								risposta = input.readLine();

								System.out.println(risposta);
								parametri = risposta.split("#");

								while(!parametri[0].equals("END")){
									Giocatore gioc = new Giocatore(parametri[0],
											new Color(Integer.parseInt(parametri[1])));
									Integer punteggio= Integer.parseInt(parametri[2]);
									punteggiOrdinati.put(gioc, punteggio);

									//leggo il prossimo punteggio
									risposta = input.readLine();

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

					} catch (NumberFormatException
							| IOException e) {
						LOGGER.log(Level.SEVERE, "Errore di IO", e);
					}
				}
			}
		}
		System.out.println("CHIUSURA THREAD CONTROLLER CLIENT");
	}
}

