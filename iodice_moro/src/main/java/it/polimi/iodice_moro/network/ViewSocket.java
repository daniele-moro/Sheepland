package it.polimi.iodice_moro.network;

import it.polimi.iodice_moro.controller.Controller;
import it.polimi.iodice_moro.model.Giocatore;
import it.polimi.iodice_moro.model.TipoTerreno;
import it.polimi.iodice_moro.view.IFView;

import java.awt.Color;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;


//Utilizzata dal controller, quindi dal SERVER
public class ViewSocket implements IFView {
	
	private Controller controller;
	ServerSocket serverSocket;
	Map<Color, Socket> socketGiocatori = new HashMap<Color, Socket>();
	Map<Color, Scanner> scannerGiocatori = new HashMap<Color,Scanner>();
	Map<Color, PrintWriter> writerGiocatori = new HashMap<Color,PrintWriter>();

	public void riceviMossa(){
		while(true){
			for(Entry<Color, Scanner> entry : scannerGiocatori.entrySet()){
				if(entry.getValue().hasNext()){
					String mossa = entry.getValue().next();
					String [] parametri=mossa.split("#");
					switch(parametri[0]){
					case "COMPRA_TESSERA":
						try {
							controller.acquistaTessera(parametri[1]);
							writerGiocatori.get(entry.getKey()).write("OK\n");
						} catch (Exception e) {
							writerGiocatori.get(entry.getKey()).write("EXCEPTION#"+e.getMessage()+"\n");
						}
						break;
					case "SELEZ_POSIZ":

						break;
					case "SPOSTA_PASTORE":
						try {
							controller.spostaPedina(parametri[1]);
							writerGiocatori.get(entry.getKey()).write("OK\n");
						} catch (Exception e) {
							writerGiocatori.get(entry.getKey()).write("EXCEPTION#"+e.getMessage()+"\n");
						}
						break;
					case "SPOSTA_PECORA":
						try {
							controller.spostaPecora(parametri[1]);
							writerGiocatori.get(entry.getKey()).write("OK\n");
						} catch (Exception e) {
							writerGiocatori.get(entry.getKey()).write("EXCEPTION#"+e.getMessage()+"\n");
						}
						break;
					case "SPOSTA_PECORA_NERA":
						try {
							controller.spostaPecoraNera(parametri[1]);
							writerGiocatori.get(entry.getKey()).write("OK\n");
						} catch (Exception e) {
							writerGiocatori.get(entry.getKey()).write("EXCEPTION#"+e.getMessage()+"\n");
						}
						break;
					default:
						break;
					}
				}
			}
		}

	}
	
	public void riceviComando(){
		
	}
	
	public void attendiGiocatori() throws IOException{
		//Memorizzo l'"orario" in cui inizio ad aspettare i giocatori
		long inizioAttesa = System.currentTimeMillis();
		while(!(socketGiocatori.size()>=4 
				|| (socketGiocatori.size()>=2 && System.currentTimeMillis()-inizioAttesa > 120000)
				)){
			Socket nuovoGiocatore =serverSocket.accept();
			Scanner in = new Scanner(nuovoGiocatore.getInputStream());
			PrintWriter out = new PrintWriter(nuovoGiocatore.getOutputStream());
			in.useDelimiter("\n");
			Color colore=controller.creaGiocatore(in.next());
			socketGiocatori.put(colore, nuovoGiocatore);
			scannerGiocatori.put(colore,in);
			writerGiocatori.put(colore, out);
		}
		//accetta la richiesta dal client, il quale gli passa il nome del nuovo giocatore
		//chiama il metodo creaGiocatore del controller, il quale gli torna il colore del giocatore appena creato
		//e con il colore inserisco il socket nella hashmap
	}

	public ViewSocket(Controller controller) throws IOException {
		serverSocket = new ServerSocket(12345);
		this.controller=controller;
		
	}

	@Override
	public void initMappa() {
		// TODO Auto-generated method stub

	}

	@Override
	public void cambiaGiocatore(Color color) {
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub

	}

	@Override
	public void addCancelloFinale(String stradaID) {
		// TODO Auto-generated method stub

	}

	@Override
	public void spostaPecoraBianca(String s, String d) {
		// TODO Auto-generated method stub

	}

	@Override
	public void spostaPastore(String s, String d, Color colore) {
		// TODO Auto-generated method stub

	}

	@Override
	public void spostaPecoraNera(String s, String d) {
		// TODO Auto-generated method stub

	}

	@Override
	public void modificaQtaPecora(String idReg, int num) {
		// TODO Auto-generated method stub

	}

	@Override
	public void modQtaTessera(TipoTerreno tess, int num) {
		// TODO Auto-generated method stub

	}

	@Override
	public void modSoldiGiocatore(Color coloreGiocatoreDaModificare, int soldi) {
		// TODO Auto-generated method stub

	}

	@Override
	public void incPrezzoTessera(TipoTerreno tess) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visualizzaPunteggi(Map<Giocatore, Integer> punteggiOrdinati) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setGiocatoreCorrente(Color colore) {
		// TODO Auto-generated method stub

	}

}
