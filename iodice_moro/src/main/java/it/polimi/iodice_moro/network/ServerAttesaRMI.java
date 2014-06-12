package it.polimi.iodice_moro.network;

import java.rmi.RemoteException;

import it.polimi.iodice_moro.controller.IFController;

public class ServerAttesaRMI implements Runnable {
	
	IFController controller;
	boolean partitaIniziata;
	long inizio;
	long ora;
	

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
						!(controller.getGiocatori().size()>=4 || (controller.getGiocatori().size()>=2 && ora-inizio>30000)))){
				//System.out.println("attesaGiocatori");
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ora=System.currentTimeMillis();
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("partita iniziata!!!");
		partitaIniziata=true;
		try {
			controller.iniziaPartita();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public boolean isIniziata() {
		return partitaIniziata;
	}
	
	public void setInizio() {
		inizio=System.currentTimeMillis();
	}
}
