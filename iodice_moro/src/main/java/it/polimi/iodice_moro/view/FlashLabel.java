package it.polimi.iodice_moro.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.Timer;


/**
 * Questa classe è un estensione di JLabel, aggiunge la possibilità alla label di flashare 
 * rendendo visibile e invisibile la label ad intervalli regolari chiamando il metodo flash()
 */
public class FlashLabel extends JLabel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final int TIMER_DELAY =150;
	private boolean flashing;
	private int count;
	private boolean visible;
	private boolean startVisibility;
	
	/**
	 * Metodo per comandare il falshing della label
	 * @param count Numero di flash che deve fare la label
	 */
	public void flash(int count){
		flashing = true;
		this.count=count;
		visible=this.isVisible();
		startVisibility=this.isVisible();
		performFlash();
		
	}
	
	/**
	 * @return Ritorna true solo se la label sta "flashando"
	 */
	public boolean isFlashing(){
		return flashing;
	}
	
	private void performFlash(){
		//Creiamo un blocco di codice che viene chiamato ripetutamente ogni 100ms,
		//per un numero di volte pari all'attributo count
		ActionListener flashingTask = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent evt) {
				//Cambio lo stato di visibilità della label
				visible=!visible;
				FlashLabel.this.setVisible(visible);
				//Controllo se la visibilità è la stessa iniziale
				if(visible==startVisibility){
					//se è quella iniziale, decremento il contatore per il numero di flash
					count--;
				}
				//quanto il contatore è a zero, allora sono finiti i flash da fare
				if(count==0){
					((Timer)evt.getSource()).stop();
					flashing = false;
				}
			}
		};

		//settiamo il timer in modo che attivi flashingTask ogni 100ms
		Timer timer = new Timer(TIMER_DELAY,flashingTask);
		timer.start();
	}

}
