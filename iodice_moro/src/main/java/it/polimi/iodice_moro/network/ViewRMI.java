package it.polimi.iodice_moro.network;

import it.polimi.iodice_moro.controller.Controller;
import it.polimi.iodice_moro.controller.IFController;
import it.polimi.iodice_moro.exceptions.PartitaIniziataException;
import it.polimi.iodice_moro.model.Giocatore;
import it.polimi.iodice_moro.model.StatoPartita;
import it.polimi.iodice_moro.model.TipoTerreno;
import it.polimi.iodice_moro.view.IFView;

import java.awt.Color;
import java.awt.Point;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ViewRMI implements IFView {
	
	Map<Color, IFView> listaView;
	IFController controller;
	
	long inizio;
	Boolean partitaIniziata;
	
	private static final Logger LOGGER= Logger.getLogger("it.polimi.iodice_moro.view");

	public ViewRMI() {
		listaView = new HashMap<Color, IFView>();
		
	}
	
	public ViewRMI(IFController controller) {
		this();
		this.controller=controller;
		partitaIniziata=false;
	}

	public void initMappa() {
		for(IFView view : listaView.values()) {
			try {
				view.initMappa();
			} catch (RemoteException e) {
				LOGGER.log(Level.SEVERE, "Errore di rete", e);
			}
		}

	}

	@Override
	public void cambiaGiocatore(Color color) {
		for(IFView view : listaView.values()){
			try {
				view.cambiaGiocatore(color);
			} catch (RemoteException e) {
				LOGGER.log(Level.SEVERE, "Errore di rete", e);
			}
		}


	}

	@Override
	public void addCancelloNormale(String stradaID) {
		for(IFView view : listaView.values()) {
			try {
				view.addCancelloNormale(stradaID);
			} catch (RemoteException e) {
				LOGGER.log(Level.SEVERE, "Errore di rete", e);
			}
		}

	}

	@Override
	public void addCancelloFinale(String stradaID) {
		for(IFView view : listaView.values()) {
			try {
				view.addCancelloFinale(stradaID);
			} catch (RemoteException e) {
				LOGGER.log(Level.SEVERE, "Errore di rete", e);
			}
		}

	}

	@Override
	public void spostaPecoraBianca(String s, String d) {
		for(IFView view : listaView.values()) {
			try {
				view.spostaPecoraBianca(s, d);
			} catch (RemoteException e) {
				LOGGER.log(Level.SEVERE, "Errore di rete", e);
			}
		}

	}

	@Override
	public void spostaPastore(String s, String d, Color colore) {
		for(final IFView view : listaView.values()) {
			try {
				view.spostaPastore(s, d, colore);
			} catch (RemoteException e) {
				LOGGER.log(Level.SEVERE, "Errore di rete", e);
			}
		}
	}
	
	@Override
	public void posiziona2Pastore(String idStrada, Color colore) {
		for(final IFView view : listaView.values()) {
			try {
				view.posiziona2Pastore(idStrada, colore);
			} catch (RemoteException e) {
				LOGGER.log(Level.SEVERE, "Errore di rete", e);
			}
		}
	}
	
	@Override
	public void selezPast(Color colore) throws RemoteException {
		//Mando solo all'interfaccia corretta il comando di selezione del pastore
		listaView.get(colore).selezPast(colore);
		
	}

	@Override
	public void spostaPecoraNera(String s, String d) {
		for(IFView view : listaView.values()) {
			try {
				view.spostaPecoraNera(s, d);
			} catch (RemoteException e) {
				LOGGER.log(Level.SEVERE, "Errore di rete", e);
			}
		}
	}
	
	@Override
	public void spostaLupo(String s, String d) throws RemoteException {
		for(IFView view : listaView.values()) {
			try {
				view.spostaLupo(s, d);
			} catch (RemoteException e) {
				LOGGER.log(Level.SEVERE, "Errore di rete", e);
			}
		}
		
	}

	@Override
	public void modificaQtaPecora(String idReg, int num) {
		for(IFView view : listaView.values()) {
			try {
				view.modificaQtaPecora(idReg, num);
			} catch (RemoteException e) {
				LOGGER.log(Level.SEVERE, "Errore di rete", e);
			}
		}
	}

	@Override
	public void modQtaTessera(TipoTerreno tess, int num, Color colore) {
		try {
			listaView.get(colore).modQtaTessera(tess, num, colore);
		} catch (RemoteException e) {
			LOGGER.log(Level.SEVERE, "Errore di rete", e);
		}
		

	}

	@Override
	public void modSoldiGiocatore(Color coloreGiocatoreDaModificare, int soldi) {
		for(IFView view : listaView.values()) {
			try {
				view.modSoldiGiocatore(coloreGiocatoreDaModificare, soldi);
			} catch (RemoteException e) {
				LOGGER.log(Level.SEVERE, "Errore di rete", e);
			}
		}

	}

	@Override
	public void incPrezzoTessera(TipoTerreno tess) {
		for(IFView view : listaView.values()) {
			try {
				view.incPrezzoTessera(tess);
			} catch (RemoteException e) {
				LOGGER.log(Level.SEVERE, "Errore di rete", e);
			}
		}
	}

	@Override
	public void visualizzaPunteggi(Map<Giocatore, Integer> punteggiOrdinati) {
		final Map<Giocatore,Integer> punteggi=punteggiOrdinati;
		for(IFView view : listaView.values()) {
			final IFView v=view;
			//Creo un thread per riuscire a visualizzare tutti i punteggi in tutti i client
			Thread t = new Thread(new Runnable(){
				@Override
				public void run() {
					try {
						v.visualizzaPunteggi(punteggi);
					} catch (RemoteException e) {
						LOGGER.log(Level.SEVERE, "Errore di rete", e);
					}
				}

			});
			t.start();
		}

	}

	@Override
	public void setGiocatoreCorrente(Color colore) {
		for(IFView view : listaView.values()) {
			try {
				view.setGiocatoreCorrente(colore);
			} catch (RemoteException e) {
				LOGGER.log(Level.SEVERE, "Errore di rete", e);
			}
		}
	}
	
	//Aggiunge istanza View alla lista della View. Utilizzato in implementazione View RMI.
	public void addView(IFView view, Color coloreGiocatore) throws RemoteException, PartitaIniziataException {
		if(listaView.isEmpty()) {
			setInizio();
		} 
		if(!isIniziata()){
			listaView.put(coloreGiocatore, view);
		}
		else {
			throw new PartitaIniziataException("Partita già iniziata 1");
		}
	}
	
	public void removeView(IFView view, Color coloreGiocatore){
		listaView.remove(coloreGiocatore);
	}

	private void setInizio() {
		inizio=System.currentTimeMillis();	
	}


	@Override
	public void attendiGiocatori() throws IOException {
		long ora = System.currentTimeMillis();
		inizio=0;
		while(inizio==0 ||
				(inizio!=0 &&
				!(listaView.size()>=4 || (listaView.size()>=2 && ora-inizio>30)))){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				LOGGER.log(Level.SEVERE, "Errore di IO", e);
			}
			ora=System.currentTimeMillis();
		}
		System.out.println("partita iniziata!!!");
		partitaIniziata=true;
		try {
			controller.iniziaPartita();
		} catch (RemoteException e) {
			LOGGER.log(Level.SEVERE, "Errore di rete", e);
		}
	}

	@Override
	public void visRisDado(int numero) throws RemoteException {
		for(IFView view : listaView.values()) {
			try {
				view.visRisDado(numero);
			} catch (RemoteException e) {
				LOGGER.log(Level.SEVERE, "Errore di rete", e);
			}
		}
	}

	@Override
	public void setPosizioniRegioni(Map<String, Point> posizioniRegioni) {
		for(IFView view : listaView.values()) {
			try {
				view.setPosizioniRegioni(posizioniRegioni);
			} catch (RemoteException e) {
				LOGGER.log(Level.SEVERE, "Errore di rete", e);
			}
		}
	}

	@Override
	public void setPosizioniStrade(Map<String, Point> posizioniCancelli) {
		for(IFView view : listaView.values()) {
			try {
				view.setPosizioniStrade(posizioniCancelli);
			} catch (RemoteException e) {
				LOGGER.log(Level.SEVERE, "Errore di rete", e);
			}
		}
		
	}

	@Override
	public void setGiocatori(Map<Color, String> giocatori) {
		for(IFView view : listaView.values()) {
			try {
				view.setGiocatori(giocatori);
			} catch (RemoteException e) {
				LOGGER.log(Level.SEVERE, "Errore di rete", e);
			}
		}
		
	}

	@Override
	public void close() throws RemoteException {
		for(Entry<Color,IFView> view : listaView.entrySet()) {
			final IFView v = view.getValue();
			Thread t = new Thread(new Runnable(){

				@Override
				public void run() {
					try {
						v.close();
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				
			});
			t.start();
		}
		Thread t2 = new Thread( new Runnable(){

			@Override
			public void run() {
				//Riavvio la connessione
				System.out.println("RIATTIVO IL SERVER");
				listaView=new HashMap<Color,IFView>();
				try {
					controller=new Controller(new StatoPartita());
					//listaView=new HashMap<Color,IFView>();
					controller.setView(ViewRMI.this);
				} catch (RemoteException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				partitaIniziata=false;
				try {
					Naming.rebind("///Server", controller);
				} catch (MalformedURLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					ViewRMI.this.inizio=0;
					ViewRMI.this.attendiGiocatori();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		t2.start();
		
		
	}
	
	public Boolean isIniziata() {
		return partitaIniziata;
	}

	public Map<Color, IFView> getViews() {
		return listaView;
	}

	@Override
	public void usaPast2(Color colore) throws RemoteException {
		//Comunico a tutti che va usato il secondo pastore del giocatore che deve giocare
		for(IFView view : listaView.values()) {
			try {
				view.usaPast2(colore);
			} catch (RemoteException e) {
				LOGGER.log(Level.SEVERE, "Errore di rete", e);
			}
		}
		
	}
}