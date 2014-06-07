package it.polimi.iodice_moro.network;

import it.polimi.iodice_moro.controller.IFController;
import it.polimi.iodice_moro.model.Giocatore;
import it.polimi.iodice_moro.model.TipoMossa;
import it.polimi.iodice_moro.model.TipoTerreno;
import it.polimi.iodice_moro.view.IFView;
import it.polimi.iodice_moro.view.ThreadAnimazionePastore;
import it.polimi.iodice_moro.view.ThreadAnimazionePecoraBianca;
import it.polimi.iodice_moro.view.ThreadAnimazionePecoraNera;
import it.polimi.iodice_moro.view.View;

import java.awt.Color;
import java.awt.Point;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


//Utilizzato dalla view "remota", cioè dal client
public class ControllerSocket implements IFController{

	private Socket socket;
	private static final int DEFAULT_PORT = 12345;
	private IFView view;
	
	PrintWriter output;
	Scanner input;
	
	public ControllerSocket(String host, int port) throws UnknownHostException, IOException{
		//Apriamo la connessione
		socket = new Socket(host, port);
		System.out.println("Apertura connessione");
		output = new PrintWriter(socket.getOutputStream());
		input = new Scanner(socket.getInputStream());
		input.useDelimiter("\n");
	}
	
	/**
	 * Metodo invocato quando non è il turno di questa istanza, usato per visualizzare le mosse sulla view
	 */
	public void attesaTurno(){
		while(true){
			System.out.println("attesa input");
			if(input.hasNext()){
				System.out.println("ricevuto qualcosa");
				String risposta = input.next();
				String[] parametri= risposta.split("#");
				switch(parametri[0]){
				case "MOD_QTA_TESS":

					TipoTerreno tess= TipoTerreno.parseInput(parametri[1]);
					int num = Integer.parseInt(parametri[2]);
					Color colore = new Color(Integer.parseInt(parametri[3]));

					view.modQtaTessera(tess, num,colore);
					break;

				case "GIOC_CORR":
					colore = new Color(Integer.parseInt(parametri[1]));
					view.setGiocatoreCorrente(colore);
					System.out.println("INIZIATA LA PARTITA!!");
					return;

				case "SPOSTA_PECORA_BIANCA":
					break;

				case "SPOSTA_PECORA_NERA":
					break;

				case "SPOSTA_PASTORE":
					break;

				case "ATTIVA":
					view.setGiocatoreCorrente(new Color(Integer.parseInt(parametri[1])));
					view.attivaGiocatore();
					return;
				case "DISATTIVA":
					view.setGiocatoreCorrente(new Color(Integer.parseInt(parametri[1])));
					view.disattivaGiocatore();
				}

			}
		}
	}
	
	public ControllerSocket(String host) throws UnknownHostException, IOException{
		this(host,DEFAULT_PORT);
	}
	
	@Override
	public void spostaPecora(String idRegione) throws Exception {
		output.println("SPOSTA_PECORA#"+idRegione);
		output.flush();
		//Attendo la risposta, che sara la mossa da effettuare nella mappa!
		String risposta=input.next();
		String[] parametri = risposta.split("#");
		switch(parametri[0]){
		case "SPOSTA_PECORA_BIANCA":
			ThreadAnimazionePecoraBianca r = new ThreadAnimazionePecoraBianca(view, parametri[1], parametri[2]);
			Thread t = new Thread(r);
			t.start();
			break;
		case "EXCEPTION":
			throw new Exception(parametri[1]);
		}
		
	}

	@Override
	public void spostaPecoraNera(String idRegPecoraNera) throws Exception {
		output.println("SPOSTA_PECORA_NERA#"+idRegPecoraNera);
		output.flush();
		String risposta=input.next();
		String[] parametri = risposta.split("#");
		switch(parametri[0]){
		case "SPOSTA_PECORA_NERA":
			ThreadAnimazionePecoraNera r = new ThreadAnimazionePecoraNera(view, parametri[1], parametri[2]);
			Thread t = new Thread(r);
			t.start();
			break;
		case "EXCEPTION":
			throw new Exception(parametri[1]);
		}
		
	}


	@Override
	public void acquistaTessera(String idRegione) throws Exception {
		output.println("COMPRA_TESSERA#"+idRegione);
		output.flush();
		String risposta=input.next();
		String[] parametri = risposta.split("#");
		switch(parametri[0]){
		//DOVREBBE MODIFICARE LA QTA TESSERA
		case "OK":
			System.out.println("TUTTO OK");
			break;
		case "EXCEPTION":
			throw new Exception(parametri[1]);
		}
		
	}

	@Override
	public void spostaPedina(String idStrada) throws Exception {
		output.println("SPOSTA_PASTORE#"+idStrada);
		output.flush();
		String risposta=input.next();
		String[] parametri = risposta.split("#");
		switch(parametri[0]){
		case "SPOSTA_PASTORE":
			//Ora attivo il movimento del pastore nella view!
			Color colore = new Color(Integer.parseInt(parametri[3]));
			ThreadAnimazionePastore r = new ThreadAnimazionePastore(view, parametri[1], parametri[2], colore);
			Thread t = new Thread(r);
			t.start();
			break;
		case "EXCEPTION":
			throw new Exception(parametri[1]);
		}
	}


	@Override
	public Color creaGiocatore(String nome) {
		System.out.println("NOME:" + nome);
		output.print(nome+"\n");
		output.flush();
		System.out.println("QUI");
		return null;
	}
	
	
	//METODO INUTILE!!
	@Override
	public void setStradaGiocatore(Color colore, String idStrada,
			String idStrada2) {
		output.println("SET_STRADE_GIOCATORE#"+colore.toString()+idStrada+idStrada2);
		output.flush();
		String risposta=input.next();
		String[] parametri = risposta.split("#");
		switch(parametri[0]) {
		case "OK":
			System.out.println("OK"+parametri[1]);
		case "ERROR":
			System.out.println("ERRORE"+parametri[1]);
		}
		
	}

	@Override
	public boolean mossaPossibile(TipoMossa mossaDaEffettuare) {
		output.println("MOSSA_POSSIBILE#"+mossaDaEffettuare.toString());
		output.flush();
		String risposta=input.next();
		String[] parametri = risposta.split("#");
		switch(parametri[0]){
		case "OK":
			return Boolean.parseBoolean(parametri[1]);
		case "ERROR":
			System.out.println("ERRORE"+parametri[1]);
		}
		return false;
	}


	@Override
	public void iniziaPartita() {
		output.println("INIZIA_PARTITA");
		output.flush();
		System.out.println("ATTESA CHE SIA IL MIO TURNO PER SELEZIONARE LA POSIZIONE");
		attesaTurno();
		/*String risposta=input.next();
		System.out.println("è il mio turno!!" + risposta);
		String[] parametri = risposta.split("#");
		switch(parametri[0]){
		case "GIOC_CORR":
			Color colore = new Color(Integer.parseInt(parametri[1]));
			view.setGiocatoreCorrente(colore);
			System.out.println("INIZIATA LA PARTITA!!");
			break;
		case "ERROR":
			System.out.println("ERRORE"+parametri[1]);
		}
		*/
	}

	@Override
	public Map<String, Point> getPosRegioni() {
		System.out.println("RICHEISTA POS REGIONI");
		output.print(new String("POS_REGIONI")+"\n");
		System.out.println("Inviata richiesta");
		output.flush();
		Map<String,Point> posRegioni = new HashMap<String,Point>();
		System.out.println("attesa risposta");
		//LETTURA DELLA RISPOSTA
		String risposta = input.next();
		System.out.println("rispostar icveuto ");
		String[] parametri = risposta.split("#");
		System.out.println(risposta);
		while(!parametri[0].equals("END")){
			posRegioni.put(parametri[0], new Point(Integer.parseInt(parametri[1]),Integer.parseInt(parametri[2])));
			risposta = input.next();
			parametri = risposta.split("#");
		}
		
		return posRegioni;

	}

	@Override
	public Map<String, Point> getPosStrade() {
		System.out.println("RICHIESTA POS STRADE");
		output.print("POS_STRADE\n");
		output.flush();
		Map<String,Point> posStrade = new HashMap<String,Point>();
		//LETTURA DELLA RISPOSTA
		String risposta = input.next();
		String[] parametri = risposta.split("#");
		System.out.println(risposta);
		while(!parametri[0].equals("END")){
			posStrade.put(parametri[0], new Point(Integer.parseInt(parametri[1]),Integer.parseInt(parametri[2])));
			risposta = input.next();
			parametri = risposta.split("#");
			System.out.println(risposta);
		}
		
		return posStrade;
	}

	@Override
	public List<String> getIDRegioniAd() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Map<Color, String> getGiocatori() {
		output.print("GET_GIOCATORI\n");
		output.flush();
		Map<Color,String> giocatori = new HashMap<Color,String>();
		//LETTURA DELLA RISPOSTA
		String risposta = input.next();
		String[] parametri = risposta.split("#");
		
		while(!parametri[0].equals("END")){
			giocatori.put(new Color(Integer.parseInt(parametri[0])),parametri[1]);
			risposta = input.next();
			parametri = risposta.split("#");
		}
		
		return giocatori;
	}

	@Override
	public void setStradaGiocatore(Color colore, String idStrada) throws Exception {
		
		output.println("SELEZ_POSIZ#"+colore+"#".toString()+"#"+idStrada);
		output.flush();
		String risposta=input.next();
		String[] parametri = risposta.split("#");
		switch(parametri[0]) {
		case "OK":
			System.out.println("OK"+parametri[1]);
		case "ERROR":
			System.out.println("ERRORE"+parametri[1]);
		}
		
	}

	@Override
	public void setView(IFView view) {
		this.view=view;
		
	}
	

}
