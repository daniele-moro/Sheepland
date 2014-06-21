package it.polimi.iodice_moro.view;

import it.polimi.iodice_moro.controller.IFController;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

/**
 * Classe che viene utilizzata per la chiusura della schermata principale
 *
 */
public class ChiusuraSchermata extends WindowAdapter {

	private IFController controller;

	private static final Logger LOGGER =  Logger.getLogger("it.polimi.iodice_moro.view");

	/**
	 * @param controller Riferimento al controller, in cui invocare i metodi di chiusura della schermata
	 */
	public ChiusuraSchermata(IFController controller) {
		this.controller=controller;
	}

	/**
	 * Evento di chiusura della finestra: questo metodo viene invocato quanto la schermata si sta chiudendo
	 */
	public void windowClosing(WindowEvent e){
		int ret = JOptionPane.showConfirmDialog (e.getWindow(), "Sicuro di voler uscire da SHEEPLAND?\nLa chiusura del gioco comporta la terminazione della partita",
				"Uscita Applicazione", JOptionPane.YES_NO_OPTION);
		if(ret == JOptionPane.YES_OPTION){
			try {
				controller.end();
			} catch (RemoteException e1) {
				LOGGER.log(Level.SEVERE, "Parameter is null", e1);
			}
			e.getWindow().dispose();
			System.exit(0);
		}
	}
}
