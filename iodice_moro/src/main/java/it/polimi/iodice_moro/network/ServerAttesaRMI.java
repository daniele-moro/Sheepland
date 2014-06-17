package it.polimi.iodice_moro.network;

import it.polimi.iodice_moro.controller.IFController;

import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerAttesaRMI implements Runnable {
	
	IFController controller;
	boolean partitaIniziata;
	long inizio;
	long ora;
	
	private static final Logger logger =  Logger.getLogger("it.polimi.iodice_moro.network");
	

	public ServerAttesaRMI(IFController controller) {
		partitaIniziata=false;
		this.controller=controller;
		
	}

	@Override
	public void run() {
		long ora = System.currentTimeMillis();
		inizio=0;
		try {
			while(inizio==0 ||
					(inizio!=0 &&
						!(controller.getGiocatori().size()>=4 || (controller.getGiocatori().size()>=2 && ora-inizio>30)))){
				//System.out.println("attesaGiocatori");
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					logger.log(Level.SEVERE, "Errore di IO", e);
					//e.printStackTrace();
				}
				ora=System.currentTimeMillis();
			}
		} catch (RemoteException e) {
			logger.log(Level.SEVERE, "Errore di rete", e);
			//e.printStackTrace();
		}
		System.out.println("partita iniziata!!!");
		partitaIniziata=true;
		try {
			controller.iniziaPartita();
		} catch (RemoteException e) {
			logger.log(Level.SEVERE, "Errore di rete", e);
			//e.printStackTrace();
		}
		
	}
	
	public boolean isIniziata() {
		return partitaIniziata;
	}
	
	public void setInizio() {
		inizio=System.currentTimeMillis();
	}
}
