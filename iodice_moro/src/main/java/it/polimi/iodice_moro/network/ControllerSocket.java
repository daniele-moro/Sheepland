package it.polimi.iodice_moro.network;

import it.polimi.iodice_moro.controller.IFController;
import it.polimi.iodice_moro.model.Giocatore;
import it.polimi.iodice_moro.model.TipoMossa;
import it.polimi.iodice_moro.view.View;

import java.awt.Color;
import java.awt.Point;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


//Utilizzato dalla view "remota", cioè dal client
public class ControllerSocket implements IFController{

	private Socket socket;
	private static final int DEFAULT_PORT = 12345;
	private View view;
	
	PrintWriter output;
	Scanner input;
	
	public ControllerSocket(String host, int port) throws UnknownHostException, IOException{
		//Apriamo la connessione
		socket = new Socket(host, port);
		output = new PrintWriter(socket.getOutputStream());
		input = new Scanner(socket.getInputStream());
		input.useDelimiter("\n");
	}
	
	public ControllerSocket(String host) throws UnknownHostException, IOException{
		this(host,DEFAULT_PORT);
	}
	
	@Override
	public void spostaPecora(String idRegione) throws Exception {
		output.println("SPOSTA_PECORA#"+idRegione);
		String risposta=input.next();
		String[] parametri = risposta.split("#");
		switch(parametri[0]){
		case "OK":
			System.out.println("TUTTO OK");
			break;
		case "EXCEPTION":
			throw new Exception(parametri[1]);
		}
		
	}

	@Override
	public void spostaPecoraNera(String idRegPecoraNera) throws Exception {
		output.println("SPOSTA_PECORA_NERA#"+idRegPecoraNera);
		String risposta=input.next();
		String[] parametri = risposta.split("#");
		switch(parametri[0]){
		case "OK":
			System.out.println("TUTTO OK");
			break;
		case "EXCEPTION":
			throw new Exception(parametri[1]);
		}
		
	}


	@Override
	public void acquistaTessera(String idRegione) throws Exception {
		output.println("COMPRA_TESSERA#"+idRegione);
		String risposta=input.next();
		String[] parametri = risposta.split("#");
		switch(parametri[0]){
		case "OK":
			System.out.println("TUTTO OK");
			break;
		case "EXCEPTION":
			throw new Exception(parametri[1]);
		}
		
	}

	@Override
	public void spostaPedina(String idStrada) throws Exception {
		output.println("SPOSTA_PEDINA#"+idStrada);
		String risposta=input.next();
		String[] parametri = risposta.split("#");
		switch(parametri[0]){
		case "OK":
			System.out.println("TUTTO OK");
			break;
		case "EXCEPTION":
			throw new Exception(parametri[1]);
		}
	}


	@Override
	public Color creaGiocatore(String nome) {
		output.print(nome+"\n");
		return null;
	}

	@Override
	public void setStradaGiocatore(Color colore, String idStrada,
			String idStrada2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean mossaPossibile(TipoMossa mossaDaEffettuare) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Giocatore checkTurnoGiocatore(TipoMossa mossaFatta) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void iniziaPartita() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<String, Point> getPosRegioni() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Point> getPosStrade() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getIDRegioniAd() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Map<Color, String> getGiocatori() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setStradaGiocatore(Color colore, String idStrada)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

}
