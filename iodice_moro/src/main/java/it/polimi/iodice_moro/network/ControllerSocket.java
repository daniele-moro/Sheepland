package it.polimi.iodice_moro.network;

import it.polimi.iodice_moro.controller.IFController;
import it.polimi.iodice_moro.model.TipoMossa;
import it.polimi.iodice_moro.model.TipoTerreno;
import it.polimi.iodice_moro.view.IFView;
import it.polimi.iodice_moro.view.ThreadAnimazionePastore;
import it.polimi.iodice_moro.view.ThreadAnimazionePecoraBianca;
import it.polimi.iodice_moro.view.ThreadAnimazionePecoraNera;

import java.awt.Color;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


//Utilizzato dalla view "remota", cioè dal client
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
	
	public ControllerSocket(String host, int port) throws UnknownHostException, IOException{
		//inizializza i parametri della connessione
		this.host=host;
		this.port=port;
		
	}
	
	public ControllerSocket(String host) throws UnknownHostException, IOException{
		this(host,DEFAULT_PORT);
	}
	
	@Override
	public void spostaPecora(String idRegione) throws Exception {
		output.println("SPOSTA_PECORA#"+idRegione);
		output.flush();
	}

	@Override
	public void spostaPecoraNera(String idRegPecoraNera) throws Exception {
		output.println("SPOSTA_PECORA_NERA#"+idRegPecoraNera);
		output.flush();
	}


	@Override
	public void acquistaTessera(String idRegione) throws Exception {
		output.println("COMPRA_TESSERA#"+idRegione);
		output.flush();
	}

	@Override
	public void spostaPedina(String idStrada) throws Exception {
		output.println("SPOSTA_PASTORE#"+idStrada);
		output.flush();
	}


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
					e.printStackTrace();
				}
			}
			if(!input.ready()){
				System.out.println("ERRORE DI CONNESSIONE");
			}
			
			System.out.println("Connessione avenuta");
			
			String risp="";
			try {
				risp = input.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			colore = new Color(Integer.parseInt(risp));
			System.out.println("colore assegnato: " + colore);
			
		}catch(IOException e ){
			System.out.println("ECCEZIONE");
			e.printStackTrace();
		}
		return colore;
	}
	
	
	//METODO INUTILE!!
	@Override
	public void setStradaGiocatore(Color colore, String idStrada, String idStrada2) {
	}

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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("MOSSA POSSIBILE? "+ risposta);
			String[] parametri = risposta.split("#");
			switch(parametri[0]){
			case "OK":
				retValue= Boolean.parseBoolean(parametri[1]);
			break;
			case "ERROR":
				System.out.println("ERRORE"+parametri[1]);
			}
		}
		return retValue;
	}


	@Override
	public void iniziaPartita() {
		output.println("INIZIA_PARTITA");
		output.flush();
		//Faccio partire il thread che gestisce i messaggi ricevuti dal server
		r = new ClientMessageReader(view, socket, input);
		attesaRisp = new Thread(r);
		attesaRisp.start();
	}

	@Override
	public Map<String, Point> getPosRegioni() {
		Map<String,Point> posRegioni = new HashMap<String,Point>();
		synchronized(input){
			System.out.println("RICHEISTA POS REGIONI");
			output.println("POS_REGIONI");
			System.out.println("Inviata richiesta");
			output.flush();
			System.out.println("attesa risposta");
			//LETTURA DELLA RISPOSTA
			String risposta="";
			try {
				risposta = input.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("rispostar icveuto ");
			String[] parametri = risposta.split("#");
			System.out.println(risposta);
			while(!parametri[0].equals("END")){
				posRegioni.put(parametri[0], new Point(Integer.parseInt(parametri[1]),Integer.parseInt(parametri[2])));
				try {
					risposta = input.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				parametri = risposta.split("#");
			}
		}
		return posRegioni;

	}

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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String[] parametri = risposta.split("#");
			System.out.println(risposta);
			while(!parametri[0].equals("END")){
				posStrade.put(parametri[0], new Point(Integer.parseInt(parametri[1]),Integer.parseInt(parametri[2])));
				try {
					risposta = input.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				parametri = risposta.split("#");
				System.out.println(risposta);
			}
		}
		return posStrade;
	}

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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			while(!risposta.equals("END")){
				idReg.add(risposta);
				try {
					risposta=input.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return idReg;
	}


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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(risposta);
			String[] parametri = risposta.split("#");

			while(!parametri[0].equals("END")){
				Color colore = new Color(Integer.parseInt(parametri[0]));
				String pos=parametri[1];
				giocatori.put(colore,pos);
				try {
					risposta = input.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				parametri = risposta.split("#");
			}
		}
		return giocatori;
	}

	@Override
	public void setStradaGiocatore(Color colore, String idStrada) throws Exception {
		output.println("SELEZ_POSIZ#"+colore.getRGB()+"#"+idStrada);
		output.flush();
	}

	@Override
	public void setView(IFView view) {
		this.view=view;
		
	}

	public void end() {
		System.out.println("chiusura della connessione!!");
		output.println("END");
		output.flush();
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void addView(IFView view, Color coloreGiocatore)
			throws RemoteException {
		// TODO Auto-generated method stub
		
	}
	

}
