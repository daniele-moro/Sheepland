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
import java.util.List;
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
	
	private static final Logger LOGGER =  Logger.getLogger("it.polimi.iodice_moro.network");
	
	
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
	
	/**
	 * Metodo che si mette in attesa che i client si connettano,
	 * quando c'è un determinato numero di utenti connessi parte chiamando iniziaPartita
	 */
	public void attendiGiocatori() throws IOException{
		long ora = System.currentTimeMillis();
		while(inizio==0 ||
				(inizio!=0 &&
					!(socketGiocatori.size()>=4 || (socketGiocatori.size()>=2 && ora-inizio>30)))){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				LOGGER.log(Level.SEVERE, "Errore con la thread sleep");
			}
			ora=System.currentTimeMillis();
		}
		System.out.println("partita iniziata!!!");
		attesaConnessioni.partitaIniziata=true;
		controller.iniziaPartita();
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
			giocatore.getValue().println("CAMBIA_GIOCATORE#"+color.getRGB());
			giocatore.getValue().flush();
		}
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
	public void spostaPastore(List<String> listaMov, Color colore) {
		//Deve effettuare il movimento del pastore su tutti i client collegati alla partita!
		for(Entry<Color, PrintWriter> giocatore :writerGiocatori.entrySet()){
			giocatore.getValue().println("SPOSTA_PASTORE#"+colore.getRGB());
			for(String pos : listaMov){
				giocatore.getValue().println(pos);
				giocatore.getValue().flush();
			}
			giocatore.getValue().println("END");
			giocatore.getValue().flush();
		}

	}
	
	@Override
	public void posiziona2Pastore(String idStrada, Color colore) {
		//Deve poisizionare il secondo pastore su tutti i client collegati alla partita
		for(Entry<Color, PrintWriter> giocatore :writerGiocatori.entrySet()){
			giocatore.getValue().println("POS_2_PAST#"+idStrada+"#"+colore.getRGB());
			giocatore.getValue().flush();
		}
	}
	

	@Override
	public void selezPast(Color colore) throws RemoteException {
		//mando solo al client che deve giocare la comunicazione che deve selezionare il pastore
		PrintWriter g =writerGiocatori.get(colore);
		g.println("G2_SELEZ_PAST#"+colore.getRGB());
		g.flush();
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


	@Override
	public void usaPast2(Color colore) throws RemoteException {
		//Notifico a tutti i client che deve essere usato il secondo pastore del giocatore che sta giocando
		for(Entry<Color, PrintWriter> g : writerGiocatori.entrySet()){
			g.getValue().println("USA_PAST_2#"+colore.getRGB());
			g.getValue().flush();
		}
		
	}



}
