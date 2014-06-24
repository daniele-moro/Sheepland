package it.polimi.iodice_moro.network;

import it.polimi.iodice_moro.controller.IFController;
import it.polimi.iodice_moro.exceptions.IllegalClickException;
import it.polimi.iodice_moro.exceptions.NotAllowedMoveException;
import it.polimi.iodice_moro.model.TipoMossa;
import it.polimi.iodice_moro.view.IFView;

import java.awt.Color;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe utilizzata dalla view "remota", cioè dal client. Si occupa da agire 
 * da intermediario per il client.
 * @author Antonio Iodice, Daniele Moro
 *
 */
public class ControllerSocket implements IFController{

	private String host;
	private int port;
	private Socket socket;
	private static final int DEFAULT_PORT = 12345;
	private IFView view;
	Thread attesaRisp;
	ClientMessageReader r;
	
	PrintWriter output;
	BufferedReader input;
	static boolean threadSuspended;
	
	private static final Logger LOGGER=  Logger.getLogger("it.polimi.iodice_moro.network");
	
	/**
	 * Costruttore del ControllerSocket.
	 * @param host Host a cui connettersi.
	 * @param port Porta a cui connettersi.
	 * @throws IOException
	 */
	public ControllerSocket(String host, int port) throws IOException{
		//inizializza i parametri della connessione
		this.host=host;
		this.port=port;
		
	}
	
	/**
	 * Costruttore di default di ControllerSocket.
	 * @param host
	 * @throws IOException
	 */
	public ControllerSocket(String host) throws IOException{
		this(host,DEFAULT_PORT);
	}
	
	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.controller.IFController#spostaPecora(java.lang.String)
	 */
	@Override
	public void spostaPecora(String idRegione) throws NotAllowedMoveException , IllegalClickException{
		output.println("SPOSTA_PECORA#"+idRegione);
		output.flush();
	}

	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.controller.IFController#spostaPecoraNera(java.lang.String)
	 */
	@Override
	public void spostaPecoraNera(String idRegPecoraNera) throws NotAllowedMoveException, RemoteException {
		output.println("SPOSTA_PECORA_NERA#"+idRegPecoraNera);
		output.flush();
	}


	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.controller.IFController#acquistaTessera(java.lang.String)
	 */
	@Override
	public void acquistaTessera(String idRegione) throws IllegalClickException, NotAllowedMoveException, RemoteException {
		output.println("COMPRA_TESSERA#"+idRegione);
		output.flush();
	}

	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.controller.IFController#spostaPedina(java.lang.String)
	 */
	@Override
	public void spostaPedina(String idStrada) throws IllegalClickException, NotAllowedMoveException, RemoteException {
		output.println("SPOSTA_PASTORE#"+idStrada);
		output.flush();
	}


	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.controller.IFController#creaGiocatore(java.lang.String)
	 */
	@Override
	public Color creaGiocatore(String nome) {
		//Prova a connettersi e se non riesce torna null, 
		//se la connessione avviene, allora comunica il nome del giocatore 
		//e attende la rispsta del server con il colore assegnatogli
		
		Color colore = null;
		//tenta la connessione al server
		try{
			socket = new Socket(host, port);
			System.out.println("Apertura connessione");
			output = new PrintWriter(socket.getOutputStream());
			input = new BufferedReader(
					new InputStreamReader(socket.getInputStream()));
			
			System.out.println("NOME:" + nome);
			output.print(nome+"\n");
			output.flush();
			
			long inizio = System.currentTimeMillis();
			//aspetto che il server mi risponda alla connessione
			while((!input.ready()) && System.currentTimeMillis()-inizio<30000){
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					LOGGER.log(Level.SEVERE, "Errore nella thread.sleep", e);
				}
			}
			if(!input.ready()){
				System.out.println("ERRORE DI CONNESSIONE");
			}
			
			System.out.println("Connessione avenuta");
			
			String risp="";
			risp = input.readLine();
				
			if("NO".equals(risp)){
				//La connessione non è avvenuta
				System.out.println("CONNESSIONE NON AVVENUTA");
			} else {
				colore = new Color(Integer.parseInt(risp));
				System.out.println("colore assegnato: " + colore);
			}			
		}catch(IOException e ){
			LOGGER.log(Level.SEVERE, "Errore di IO", e);
		}
		
		return colore;
	}
	

	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.controller.IFController#mossaPossibile(Model.TipoMossa)
	 */
	@Override
	public boolean mossaPossibile(TipoMossa mossaDaEffettuare) {
		boolean retValue=false;
		synchronized(input){
			System.out.println("FERMATO THREAD NEL TH PRINC\nchiamata remota al metodo Mossa Possibile");
			output.println("MOSSA_POSSIBILE#"+mossaDaEffettuare.toString());
			output.flush();
			String risposta="";
			try {
				risposta = input.readLine();
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, "Errore di IO", e);
			}
			
			System.out.println("MOSSA POSSIBILE? "+ risposta);
			
			String[] parametri = risposta.split("#");
			if("OK".equals(parametri[0])){
				retValue= Boolean.parseBoolean(parametri[1]);
			}
			if("ERROR".equals(parametri[0])){
				System.out.println("ERRORE"+parametri[1]);
			}
		}
		return retValue;
	}

	// TODO CONTROLLARE SE VIENE USATO; NON DOVREBBE VENIRE USAtO, iniziaPartita chiamato dal server
	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.controller.IFController#iniziaPartita
	 */
	@Override
	public void iniziaPartita() {
		output.println("INIZIA_PARTITA");
		output.flush();
		//Faccio partire il thread che gestisce i messaggi ricevuti dal server
		r = new ClientMessageReader(view, socket, input);
		attesaRisp = new Thread(r);
		attesaRisp.start();
	}

	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.controller.IFController#getPosRegioni
	 */
	@Override
	public Map<String, Point> getPosRegioni() {
		Map<String,Point> posRegioni = new HashMap<String,Point>();
		synchronized(input){
			System.out.println("RICHIESTA POS REGIONI");
			output.println("POS_REGIONI");
			System.out.println("Inviata richiesta");
			output.flush();
			System.out.println("attesa risposta");
			//LETTURA DELLA RISPOSTA
			String risposta="";
			try {
				risposta = input.readLine();
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, "Errore di IO", e);
			}
			System.out.println("rispostar icveuto ");
			String[] parametri = risposta.split("#");
			System.out.println(risposta);
			while(!"END".equals(parametri[0])){
				posRegioni.put(parametri[0], new Point(Integer.parseInt(parametri[1]),Integer.parseInt(parametri[2])));
				try {
					risposta = input.readLine();
				} catch (IOException e) {
					LOGGER.log(Level.SEVERE, "Errore di IO", e);
				}
				parametri = risposta.split("#");
			}
		}
		return posRegioni;

	}

	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.controller.IFController#getPosStrade
	 */
	@Override
	public Map<String, Point> getPosStrade() {
		Map<String,Point> posStrade = new HashMap<String,Point>();
		synchronized(input){
			System.out.println("RICHIESTA POS STRADE");
			output.print("POS_STRADE\n");
			output.flush();

			//LETTURA DELLA RISPOSTA
			String risposta="";
			try {
				risposta = input.readLine();
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, "Errore di IO", e);
			}
			String[] parametri = risposta.split("#");
			System.out.println(risposta);
			while(!"END".equals(parametri[0])){
				posStrade.put(parametri[0], new Point(Integer.parseInt(parametri[1]),Integer.parseInt(parametri[2])));
				try {
					risposta = input.readLine();
				} catch (IOException e) {
					LOGGER.log(Level.SEVERE, "Errore di IO", e);
				}
				parametri = risposta.split("#");
				System.out.println(risposta);
			}
		}
		return posStrade;
	}

	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.controller.IFController#getIDRegioniAd(java.lang.String)
	 */
	@Override
	public List<String> getIDRegioniAd() {
		List<String> idReg= new ArrayList<String>();
		synchronized(input){
			System.out.println("chiamata remota a GET ID REG AD NEL CLIENT");
			output.println("GET_ID_REG_AD");
			output.flush();

			String risposta ="";
			try {
				risposta=input.readLine();
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, "Errore di IO", e);
			}
			while(!"END".equals(risposta)){
				idReg.add(risposta);
				try {
					risposta=input.readLine();
				} catch (IOException e) {
					LOGGER.log(Level.SEVERE, "Errore di IO", e);
				}
			}
		}
		return idReg;
	}


	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.controller.IFController#getGiocatori
	 */
	@Override
	public Map<Color, String> getGiocatori() {
		Map<Color,String> giocatori = new HashMap<Color,String>();
		synchronized(input){
			System.out.println("GET_GIOCATORI CLIENT");
			output.print("GET_GIOCATORI\n");
			output.flush();
			
			//LETTURA DELLA RISPOSTA
			String risposta="";
			try {
				risposta = input.readLine();
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, "Errore di IO", e);
			}
			System.out.println(risposta);
			String[] parametri = risposta.split("#");

			while(!"END".equals(parametri[0])){
				Color colore = new Color(Integer.parseInt(parametri[0]));
				String pos=parametri[1];
				giocatori.put(colore,pos);
				try {
					risposta = input.readLine();
				} catch (IOException e) {
					LOGGER.log(Level.SEVERE, "Errore di IO", e);
				}
				parametri = risposta.split("#");
			}
		}
		return giocatori;
	}

	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.controller.IFController#setStradaGiocatore(Color, java.lang.String)
	 */
	@Override
	public void setStradaGiocatore(Color colore, String idStrada) throws IllegalClickException {
		output.println("SELEZ_POSIZ#"+colore.getRGB()+"#"+idStrada);
		output.flush();
	}

	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.controller.IFController#ssetView(View.IFView)
	 */
	@Override
	public void setView(IFView view) {
		this.view=view;
		//creo il thread che stara in ascolto delle risposte del server
		r = new ClientMessageReader(view, socket, input);
		attesaRisp = new Thread(r);
		attesaRisp.start();
		
	}

	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.controller.IFController#end
	 */
	@Override
	public void end() {
		System.out.println("chiusura della connessione!!");
		output.println("END");
		output.flush();
		try {
			socket.close();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Errore di IO", e);
		}
		
	}

	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.controller.IFController#addView(View.IFView, Color)
	 */
	@Override
	public void addView(IFView view, Color coloreGiocatore)
			throws RemoteException {
		//Metodo non utilizzato con i socket, ma solo in RMI
	}

	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.controller.IFController#accoppiamento1(java.lang.String)
	 */
	@Override
	public void accoppiamento1(String idRegione) throws NotAllowedMoveException, RemoteException, IllegalClickException {
		output.println("ACCOPPIAMENTO1#"+idRegione);
		output.flush();
		
	}

	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.controller.IFController#sparatoria1(java.lang.String)
	 */
	@Override
	public void sparatoria1(String idRegione) throws NotAllowedMoveException, RemoteException, IllegalClickException {
		output.println("SPARATORIA1#"+idRegione);
		output.flush();
	}

	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.controller.IFController#cambiaPastore(java.lang.String)
	 */
	@Override
	public void cambiaPastore(String idStrada) throws RemoteException,IllegalClickException {
		output.println("CAMBIA_PASTORE#"+idStrada);
		output.flush();	
	}

	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.controller.IFController#sparatoria2(java.lang.String)
	 */
	@Override
	public void sparatoria2(String idRegione) throws RemoteException,
			IllegalClickException, NotAllowedMoveException {
		output.println("SPARATORIA2#"+idRegione);
		output.flush();	
		
	}


}
