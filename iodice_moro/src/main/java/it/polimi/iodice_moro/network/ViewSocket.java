package it.polimi.iodice_moro.network;

import it.polimi.iodice_moro.controller.IFController;
import it.polimi.iodice_moro.model.Giocatore;
import it.polimi.iodice_moro.model.TipoMossa;
import it.polimi.iodice_moro.model.TipoTerreno;
import it.polimi.iodice_moro.view.IFView;

import java.awt.Color;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;


//Utilizzata dal controller, quindi dal SERVER
public class ViewSocket implements IFView {
	
	private IFController controller;
	ServerSocket serverSocket;
	Map<Color, Socket> socketGiocatori = new HashMap<Color, Socket>();
	Map<Color, BufferedReader> scannerGiocatori = new HashMap<Color,BufferedReader>();
	Map<Color, PrintWriter> writerGiocatori = new HashMap<Color,PrintWriter>();
	//Map<Color, InputStream> inputStreamGiocatori = new HashMap<Color, InputStream>();
	
	/**
	 * Metodo per ricevere tutte le mosse dei giocatori, cioè dei client
	 */
	public void riceviMossa(){
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
	}
	
	public void attendiGiocatori() throws IOException{
		//Memorizzo l'"orario" in cui inizio ad aspettare i giocatori
		long inizioAttesa = System.currentTimeMillis();
		while(!(socketGiocatori.size()>=4 
				|| (socketGiocatori.size()>=2 && System.currentTimeMillis()-inizioAttesa > 20)
				)){
			Socket nuovoGiocatore =serverSocket.accept();
			System.out.println("Accettata connessione da IP: "+nuovoGiocatore.getInetAddress());
			BufferedReader in =  new BufferedReader(
		            new InputStreamReader(nuovoGiocatore.getInputStream()));
			PrintWriter out = new PrintWriter(nuovoGiocatore.getOutputStream());
			String nome = in.readLine();
			
			Color colore=controller.creaGiocatore(nome);
			
			System.out.println("Colore: "+colore + "NOME: "+nome);
			socketGiocatori.put(colore, nuovoGiocatore);
			scannerGiocatori.put(colore,in);
			writerGiocatori.put(colore, out);
			out.println(colore.getRGB());
			out.flush();
			//Crea thread per ascolto e spedizione messaggi
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


}
