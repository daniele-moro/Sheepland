package it.polimi.iodice_moro.network;

import it.polimi.iodice_moro.controller.Controller;
import it.polimi.iodice_moro.model.Giocatore;
import it.polimi.iodice_moro.model.TipoTerreno;
import it.polimi.iodice_moro.view.IFView;

import java.awt.Color;
import java.awt.Point;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;


//Utilizzata dal controller, quindi dal SERVER
public class ViewSocket implements IFView {
	
	private Controller controller;
	ServerSocket serverSocket;
	Map<Color, Socket> socketGiocatori = new HashMap<Color, Socket>();
	Map<Color, PrintWriter> writerGiocatori = new HashMap<Color,PrintWriter>();
	private long inizio;
	ServerConnessione attesaConnessioni;
	
	private static final Logger logger =  Logger.getLogger("it.polimi.iodice_moro.network");
	
	/**
	 * Metodo per ricevere tutte le mosse dei giocatori, cioè dei client
	 */
	/*public void riceviMossa(){
		int cont=0;
		while(!serverSocket.isClosed()){
			try {
				Thread.sleep(10);
			} catch (InterruptedException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			for(Entry<Color, BufferedReader> entry : scannerGiocatori.entrySet()){
				try {
					if(entry.getValue().ready())
					{
						System.out.println("Attesa input da stream ");
						String mossa = entry.getValue().readLine();
						String [] parametri=mossa.split("#");
						PrintWriter giocaRisposta= writerGiocatori.get(entry.getKey());
						switch(parametri[0]){

						case "COMPRA_TESSERA":
							try {
								System.out.println("Acquista Tessera");
								controller.acquistaTessera(parametri[1]);
							} catch (Exception e) {
								giocaRisposta.println("EXCEPTION#"+e.getMessage()+"\n");
							}
							break;

						case "SELEZ_POSIZ":
							System.out.println("SELEZ_POSIZ SERVER");
							try {
								controller.setStradaGiocatore(new Color(Integer.parseInt(parametri[1])), parametri[2]);
								//giocaRisposta.println("OK#"+"Inserimento avvenuto con successo");
							} catch (Exception e1) {
								System.out.println("eccezione");
								giocaRisposta.println("EXCEPTION#"+e1.getMessage());
							}
							break;

						case "SPOSTA_PASTORE":
							try {
								System.out.println("spostaPastore");
								controller.spostaPedina(parametri[1]);
							} catch (Exception e) {
								giocaRisposta.println("EXCEPTION#"+e.getMessage()+"\n");
							}
							break;

						case "SPOSTA_PECORA":
							try {
								System.out.println("sposta Pecora");
								controller.spostaPecora(parametri[1]);
							} catch (Exception e) {
								giocaRisposta.println("EXCEPTION#"+e.getMessage()+"\n");
							}
							break;

						case "SPOSTA_PECORA_NERA":
							try {
								System.out.println("sposta pecora nera");
								controller.spostaPecoraNera(parametri[1]);
							} catch (Exception e) {
								giocaRisposta.println("EXCEPTION#"+e.getMessage()+"\n");
							}
							break;

						case "MOSSA_POSSIBILE":
							try {

								Boolean risp = controller.mossaPossibile(TipoMossa.parseInput(parametri[1]));
								System.out.println("mossapossibile   "+ risp.toString());
								giocaRisposta.println("OK#"+risp.toString());
							} catch (Exception e) {
								System.out.println("ERRORE nella mossa possibile!!");
								giocaRisposta.println("ERROR#"+e.getMessage()+"\n");
							}
							break;

						case "INIZIA_PARTITA":
							//devo attendere che tutti i giocatori siano pronti a giocare
							cont++;
							if(cont==socketGiocatori.size()){
								cont=0;
								controller.iniziaPartita();
							}
							break;

						case "POS_STRADE":
							System.out.println("POS_STRADE");
							for(Entry<String,Point> str : controller.getPosStrade().entrySet()){
								giocaRisposta.println(str.getKey()+"#"+str.getValue().x+"#"+str.getValue().y);
								giocaRisposta.flush();
							}
							giocaRisposta.println("END");
							break;

						case "POS_REGIONI":
							System.out.println("POS_REGIONI");
							for(Entry<String,Point> reg : controller.getPosRegioni().entrySet()){
								giocaRisposta.println(reg.getKey()+"#"+reg.getValue().x+"#"+reg.getValue().y);
								giocaRisposta.flush();
							}
							giocaRisposta.println("END");
							break;

						case "GET_GIOCATORI":
							System.out.println("GET GIOCATORI");
							for(Entry<Color,String> reg : controller.getGiocatori().entrySet()){
								giocaRisposta.println(reg.getKey().getRGB()+"#"+reg.getValue());
								giocaRisposta.flush();
							}
							giocaRisposta.println("END");
							break;	
						case "GET_ID_REG_AD":
							List<String> reg= controller.getIDRegioniAd();
							for(String r : reg){
								giocaRisposta.println(r);
								giocaRisposta.flush();
							}
							giocaRisposta.println("END");
							break;
							
						case "END":
							cont++;
							socketGiocatori.get(entry.getKey()).close();
							if(cont==socketGiocatori.size()){
								//FINITA LA PARTITA
								serverSocket.close();
							}
							break;
						default:
							break;
						}
						giocaRisposta.flush();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}*/
	
	/**
	 * Metodo che si mette in attesa che i client si connettano,
	 * quando c'è un determinato numero di utenti connessi parte chiamando iniziaPartita
	 */
	public void attendiGiocatori() throws IOException{
		long ora = System.currentTimeMillis();
		while(inizio==0 ||
				(inizio!=0 &&
					!(socketGiocatori.size()>=4 || (socketGiocatori.size()>=2 && ora-inizio>30000)))){
			//System.out.println("attesaGiocatori");
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				logger.log(Level.SEVERE, "Errore con la thread sleep");
			}
			ora=System.currentTimeMillis();
		}
		System.out.println("partita iniziata!!!");
		attesaConnessioni.partitaIniziata=true;
		controller.iniziaPartita();
	}

	
	public ViewSocket(Controller controller, int porta) throws IOException {
		serverSocket = new ServerSocket(porta);
		this.controller=controller;
		inizio=0;
		//Avvio il thread per la ricezione delle connessioni
		//il quale riceve le connessioni e aggiugne i giocatori alla partita
		attesaConnessioni = new ServerConnessione(controller, this,serverSocket);
		Thread t = new Thread(attesaConnessioni);
		t.start();
		
	}
	

	public ViewSocket(Controller controller) throws IOException {
		this(controller, 12345);
		
	}

	@Override
	public void initMappa() {
		for(Entry<Color, PrintWriter> g : writerGiocatori.entrySet()){
			System.out.println("INIT_MAPPA socket");
			g.getValue().println("INIT_MAPPA");
			g.getValue().flush();
		}

	}

	@Override
	public void cambiaGiocatore(Color color) {
		//COMUNICO qual'è il giocatore che deve giocare in questo turno
		//ATTIVO solo il giocatore che deve giocare in questo turno
		//DISATTIVO comunico di disattivarsi a tutti gli altri giocatori
		for(Entry<Color, PrintWriter> giocatore :writerGiocatori.entrySet()){
			/*if(giocatore.getKey().equals(color)){
				giocatore.getValue().println("ATTIVA#"+color.getRGB());
			}else{
				giocatore.getValue().println("DISATTIVA#"+color.getRGB());
			}*/
			giocatore.getValue().println("CAMBIA_GIOCATORE#"+color.getRGB());
			giocatore.getValue().flush();
		}
	}

	@Override
	public void attivaGiocatore() {
		// TODO Auto-generated method stub

	}

	@Override
	public void disattivaGiocatore() {
		// TODO Auto-generated method stub

	}

	@Override
	public void addCancelloNormale(String stradaID) {
		//Comunico a tutti i client che bisogna posizionare un cancello in una determinata posizione indicata da stradaID
		for(Entry<Color, PrintWriter> g : writerGiocatori.entrySet()){
			g.getValue().println("ADD_CANC_NORM#"+stradaID);
			g.getValue().flush();
		}

	}

	@Override
	public void addCancelloFinale(String stradaID) {
		//Comunico a tutti i client che bisogna posizionare un cancello FINALE in una determinata posizione indicata da stradaID
		for(Entry<Color, PrintWriter> g : writerGiocatori.entrySet()){
			g.getValue().println("ADD_CANC_FIN#"+stradaID);
			g.getValue().flush();
		}
	}

	@Override
	public void spostaPecoraBianca(String s, String d) {
		//DEVE effettuare il movimento della pecora normale in tutti i client collegati alla parita
		for(Entry<Color, PrintWriter> giocatore :writerGiocatori.entrySet()){
			giocatore.getValue().println("SPOSTA_PECORA_BIANCA#"+s+"#"+d);
			giocatore.getValue().flush();
		}
	}

	@Override
	public void spostaPastore(String s, String d, Color colore) {
		//Deve effettuare il movimento del pastore su tutti i client collegati alla partita!
		for(Entry<Color, PrintWriter> giocatore :writerGiocatori.entrySet()){
			giocatore.getValue().println("SPOSTA_PASTORE#"+s+"#"+d+"#"+colore.getRGB());
			giocatore.getValue().flush();
		}

	}

	@Override
	public void spostaPecoraNera(String s, String d) {
		//Deve effettuare il movimento della pecora nera su tutti i client collegati alla partita
		for(Entry<Color, PrintWriter> giocatore :writerGiocatori.entrySet()){
			giocatore.getValue().println("SPOSTA_PECORA_NERA#"+s+"#"+d);
			giocatore.getValue().flush();
		}
	}
	
	public void spostaLupo(String s, String d) {
		//Deve effettuare il movimento della pecora nera su tutti i client collegati alla partita
		for(Entry<Color, PrintWriter> giocatore :writerGiocatori.entrySet()){
			giocatore.getValue().println("SPOSTA_LUPO#"+s+"#"+d);
			giocatore.getValue().flush();
		}
	}

	@Override
	public void modificaQtaPecora(String idReg, int num) {
		//Comunico a tutti i client che bisogna modificare la quantita di pecora in una determinata regione
		for(Entry<Color, PrintWriter> g : writerGiocatori.entrySet()){
			g.getValue().println("MOD_QTA_PEC#"+idReg+"#"+num);
			g.getValue().flush();
		}

	}

	@Override
	public void modQtaTessera(TipoTerreno tess, int num, Color colore) {
		//invio l'informazione solo al client corretto
		PrintWriter g =writerGiocatori.get(colore);
		g.println("MOD_QTA_TESS#"+tess.toString()+"#"+num+"#"+colore.getRGB());
		g.flush();
	}

	@Override
	public void modSoldiGiocatore(Color coloreGiocatoreDaModificare, int soldi) {
		//Invio in flooding la modifica dei soldi a tutti i giocatori
		for(Entry<Color, PrintWriter> g :writerGiocatori.entrySet()){
			g.getValue().println("MOD_SOLDI_GIOC#"+coloreGiocatoreDaModificare.getRGB()+"#"+soldi);
			g.getValue().flush();
		}
	}

	@Override
	public void incPrezzoTessera(TipoTerreno tess) {
		//invio in flooding a tutti i client la modifica del prezzo della tessera
		for(Entry<Color, PrintWriter> g :writerGiocatori.entrySet()){
			g.getValue().println("MOD_PREZZO_TESS#"+tess.toString());
			g.getValue().flush();
		}

	}

	@Override
	public void visualizzaPunteggi(Map<Giocatore, Integer> punteggiOrdinati) {
		//Mando in flooding a tutti i client i dati dei punteggi di tutti i giocatori
		for(Entry<Color, PrintWriter> i : writerGiocatori.entrySet()){
			PrintWriter out = i.getValue();
			out.println("FINE_PARTITA");
			out.flush();
			for(Entry<Giocatore, Integer> g : punteggiOrdinati.entrySet()){
				out.println(g.getKey().getNome()+"#"+g.getKey().getColore().getRGB()+"#"+g.getValue());
				out.flush();
			}
			out.println("END");
			out.flush();
		}

	}

	@Override
	public void setGiocatoreCorrente(Color colore) {
		for(Entry<Color, PrintWriter> g : writerGiocatori.entrySet()){
			System.out.println("comunicazione giocatore corrente COLORE" + colore);
			//PrintWriter g =writerGiocatori.get(colore);
			System.out.println(g);
			g.getValue().println("GIOC_CORR#"+colore.getRGB());
			g.getValue().flush();
		}
	}

	@Override
	public void visRisDado(int numero) {
		for(Entry<Color, PrintWriter> g : writerGiocatori.entrySet()){
			g.getValue().println("RIS_DADO#"+numero);
			g.getValue().flush();
		}
		
	}

	@Override
	public void setColore(Color coloreGiocatore) throws RemoteException {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Usato per aggiungere i client connessi
	 * @param socket
	 * @param output
	 * @param colore
	 */
	public void addClient(Socket socket, PrintWriter output, Color colore) throws RemoteException{
		if(socketGiocatori.size()==0){
			System.out.println("INIZIO ARRIVO CLIENT");
			inizio=System.currentTimeMillis();
		}
		socketGiocatori.put(colore, socket);
		writerGiocatori.put(colore, output);
	}


	@Override
	public void setPosizioniRegioni(Map<String, Point> posizioniRegioni)  throws RemoteException{
		System.out.println("POS_REGIONI");
		for(Entry<Color, PrintWriter> g : writerGiocatori.entrySet()){
			g.getValue().println("SET_POS_REG");
			for(Entry<String,Point> reg : controller.getPosRegioni().entrySet()){
				g.getValue().println(reg.getKey()+"#"+reg.getValue().x+"#"+reg.getValue().y);
				g.getValue().flush();
			}
			g.getValue().println("END");
			g.getValue().flush();
		}
	}


	@Override
	public void setPosizioniStrade(Map<String, Point> posizioniCancelli)  throws RemoteException{
		
		System.out.println("POS_STRADE");
		for(Entry<Color, PrintWriter> g : writerGiocatori.entrySet()){
			g.getValue().println("SET_POS_STR");
			for(Entry<String,Point> str : controller.getPosStrade().entrySet()){
				g.getValue().println(str.getKey()+"#"+str.getValue().x+"#"+str.getValue().y);
				g.getValue().flush();
			}
			g.getValue().println("END");
			g.getValue().flush();
		}
		
	}


	@Override
	public void setGiocatori(Map<Color, String> giocatori)  throws RemoteException{
		System.out.println("GET GIOCATORI");
		for(Entry<Color, PrintWriter> g : writerGiocatori.entrySet()){
			g.getValue().println("SET_GIOC");
			for(Entry<Color,String> reg : controller.getGiocatori().entrySet()){
				g.getValue().println(reg.getKey().getRGB()+"#"+reg.getValue());
				g.getValue().flush();
			}
			g.getValue().println("END");
			g.getValue().flush();
		}
	}
	
	@Override
	public void close(){
		//notifico a tutti che l'applicazione va chiusa
		for(Entry<Color, PrintWriter> g : writerGiocatori.entrySet()){
			if(!socketGiocatori.get(g.getKey()).isClosed()){
				g.getValue().println("CLOSE");
				g.getValue().flush();
			}
		}
	}


}
