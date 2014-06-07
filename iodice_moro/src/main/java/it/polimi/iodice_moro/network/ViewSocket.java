package it.polimi.iodice_moro.network;

import it.polimi.iodice_moro.controller.IFController;
import it.polimi.iodice_moro.model.Giocatore;
import it.polimi.iodice_moro.model.TipoMossa;
import it.polimi.iodice_moro.model.TipoTerreno;
import it.polimi.iodice_moro.view.IFView;

import java.awt.Color;
import java.awt.Point;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;


//Utilizzata dal controller, quindi dal SERVER
public class ViewSocket implements IFView {
	
	private IFController controller;
	ServerSocket serverSocket;
	Map<Color, Socket> socketGiocatori = new HashMap<Color, Socket>();
	Map<Color, Scanner> scannerGiocatori = new HashMap<Color,Scanner>();
	Map<Color, PrintWriter> writerGiocatori = new HashMap<Color,PrintWriter>();
	Map<Color, InputStream> inputStreamGiocatori = new HashMap<Color, InputStream>();

	/**
	 * Metodo per ricevere tutte le mosse dei giocatori, cioè dei client
	 */
	public void riceviMossa(){
		int cont=0;
		while(true){
			for(Entry<Color, Scanner> entry : scannerGiocatori.entrySet()){
					if(entry.getValue().hasNext())
					{
						System.out.println("Attesa input da stream  "+ inputStreamGiocatori.get(entry.getKey()));
						String mossa = entry.getValue().next();
						String [] parametri=mossa.split("#");
						PrintWriter giocaRisposta= writerGiocatori.get(entry.getKey());
						switch(parametri[0]){
						
						case "COMPRA_TESSERA":
							try {
								controller.acquistaTessera(parametri[1]);
							} catch (Exception e) {
								giocaRisposta.write("EXCEPTION#"+e.getMessage()+"\n");
							}
							break;
							
						case "SELEZ_POSIZ":
							System.out.println("SELEZ_POSIZ SERVER");
							try {
								controller.setStradaGiocatore(new Color(Integer.parseInt(parametri[1])), parametri[2]);
							} catch (Exception e1) {
								giocaRisposta.println("EXCEPTION#"+e1.getMessage());
							}
							break;
							
						case "SPOSTA_PASTORE":
							try {
								controller.spostaPedina(parametri[1]);
							} catch (Exception e) {
								giocaRisposta.write("EXCEPTION#"+e.getMessage()+"\n");
							}
							break;
							
						case "SPOSTA_PECORA":
							try {
								controller.spostaPecora(parametri[1]);
							} catch (Exception e) {
								giocaRisposta.write("EXCEPTION#"+e.getMessage()+"\n");
							}
							break;
							
						case "SPOSTA_PECORA_NERA":
							try {
								controller.spostaPecoraNera(parametri[1]);
							} catch (Exception e) {
								giocaRisposta.write("EXCEPTION#"+e.getMessage()+"\n");
							}
							break;
							
						case "MOSSA_POSSIBILE":
							try {
								Boolean risp = controller.mossaPossibile(TipoMossa.parseInput(parametri[1]));
								giocaRisposta.write("OK#"+risp.toString());
							} catch (Exception e) {
								giocaRisposta.write("ERROR#"+e.getMessage()+"\n");
							}
							break;
							
						case "INIZIA_PARTITA":
							//devo attendere che tutti i giocatori siano pronti a giocare
							cont++;
							if(cont==socketGiocatori.size()){
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

						default:
							break;
						}
						giocaRisposta.flush();
					}
			}
		}
	}
	
	public void attendiGiocatori() throws IOException{
		//Memorizzo l'"orario" in cui inizio ad aspettare i giocatori
		long inizioAttesa = System.currentTimeMillis();
		while(!(socketGiocatori.size()>=4 
				|| (socketGiocatori.size()>=2 && System.currentTimeMillis()-inizioAttesa > 20)
				)){
			Socket nuovoGiocatore =serverSocket.accept();
			System.out.println("Accettata connessione da IP: "+nuovoGiocatore.getInetAddress());
			Scanner in = new Scanner(nuovoGiocatore.getInputStream());
			PrintWriter out = new PrintWriter(nuovoGiocatore.getOutputStream());
			in.useDelimiter("\n");
			String nome = in.next();
			Color colore=controller.creaGiocatore(nome);
			System.out.println("Colore: "+colore + "NOME: "+nome);
			socketGiocatori.put(colore, nuovoGiocatore);
			scannerGiocatori.put(colore,in);
			inputStreamGiocatori.put(colore, nuovoGiocatore.getInputStream());
			writerGiocatori.put(colore, out);
		}
		//accetta la richiesta dal client, il quale gli passa il nome del nuovo giocatore
		//chiama il metodo creaGiocatore del controller, il quale gli torna il colore del giocatore appena creato
		//e con il colore inserisco il socket nella hashmap
	}

	
	public ViewSocket(IFController controller, int porta) throws IOException {
		serverSocket = new ServerSocket(porta);
		this.controller=controller;
		
	}
	

	public ViewSocket(IFController controller) throws IOException {
		this(controller, 12345);
		
	}

	@Override
	public void initMappa() {
		for(Entry<Color, PrintWriter> g : writerGiocatori.entrySet()){
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
					if(giocatore.getKey().equals(color)){
						giocatore.getValue().println("ATTIVA#"+color.getRGB());
					}else{
						giocatore.getValue().println("DISATTIVA#"+color.getRGB());
					}
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
		// TODO Auto-generated method stub

	}

	@Override
	public void addCancelloFinale(String stradaID) {
		// TODO Auto-generated method stub

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
			giocatore.getValue().println("SPOSTA_PASTORE#"+s+"#"+d+"#"+giocatore.getKey().getRGB());
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

	@Override
	public void modificaQtaPecora(String idReg, int num) {
		// TODO Auto-generated method stub

	}

	@Override
	public void modQtaTessera(TipoTerreno tess, int num, Color colore) {
		//invio l'informazione solo al client corretto
		PrintWriter g =writerGiocatori.get(colore);
		g.println("MOD_QTA_TESS#"+tess.toString()+"#"+num+"#"+colore.getRGB());
		g.flush();
		/*
		for(Entry<Color, PrintWriter> g : writerGiocatori.entrySet()){
			if(g.getValue().equals(colore)){
				//se l'if è verificato significa che l'informazione devo inviarla a questo client
				g.getValue().println("MOD_QTA_TESS#"+tess.toString()+"#"+num+"#"+colore.getRGB());
				g.getValue().flush();
			}
		}*/
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
		//Comunico al client corretto che lui è il giocatore corrente
		System.out.println("comunicazione giocatore corrente COLORE" + colore);
		PrintWriter g =writerGiocatori.get(colore);
		System.out.println(g);
		g.println("GIOC_CORR#"+colore.getRGB());
		g.flush();
	}

}
