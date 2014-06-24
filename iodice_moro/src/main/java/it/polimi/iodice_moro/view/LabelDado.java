package it.polimi.iodice_moro.view;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 * Label con visualizzazione del risultato del lancio di un dado
 * @author Antonio Iodice, Daniele Moro
 */
public class LabelDado extends JLabel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final Logger LOGGER =  Logger.getLogger("it.polimi.iodice_moro.view");

	/**
	 * Serve a visualizzare il risultato del dado sulla schermata.
	 * @param numero Numero del dado da visualizzare.
	 * @param testoDaVisualizzare Testo da visualizzare.
	 */
	public void visualizzaDado(int numero, String testoDaVisualizzare){
		final int n=numero;
		final String testo=testoDaVisualizzare;
		Thread t = new Thread(new Runnable(){

			@Override
			public void run() {
				setIcon(new ImageIcon(this.getClass().getClassLoader().getResource("immagini/dado.gif")));
				try {
					//metto in pausa il thread per dare la sensazione che si stia lanciando il dado
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					LOGGER.log(Level.SEVERE, "Errore nell'esecuzione della thread sleep", e);
				}
				setIcon(null);
				setText("Risultato: "+n);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					LOGGER.log(Level.SEVERE, "Errore nell'esecuzione della thread sleep", e);
				}
				setText(testo);
			}
			
		});
		t.start();
	}
}
