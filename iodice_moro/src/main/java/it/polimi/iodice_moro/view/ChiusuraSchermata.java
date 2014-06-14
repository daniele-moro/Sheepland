package it.polimi.iodice_moro.view;

import it.polimi.iodice_moro.controller.IFController;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;

import javax.swing.JOptionPane;

public class ChiusuraSchermata extends WindowAdapter {

	private IFController controller;

	public ChiusuraSchermata(IFController controller) {
		this.controller=controller;
	}
	
	public void windowClosing(WindowEvent e){
		int ret = JOptionPane.showConfirmDialog (e.getWindow(), "Sicuro di voler uscire da SHEEPLAND?\nLa chiusura del gioco comporta la terminazione della partita",
				"Uscita Applicazione", JOptionPane.YES_NO_OPTION);
		switch(ret){
		case JOptionPane.YES_OPTION:
			try {
				controller.end();
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.getWindow().dispose();
			System.exit(0);
			break;
		case JOptionPane.NO_OPTION:
			break;

		}
		
	}

}
