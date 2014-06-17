package it.polimi.iodice_moro.view;

import java.awt.Point;

import javax.swing.ImageIcon;

public class ThreadAnimazionePecoraBianca implements Runnable {
	
	private View view;
	private Point sorg;
	private Point dest;
	
	public ThreadAnimazionePecoraBianca(View view, Point sorg, Point dest){
		this.view=view;
		this.sorg=sorg;
		this.dest=dest;
	}

	@Override
	public void run() {
		view.spostaImmagine(sorg, dest, new ImageIcon(this.getClass().getResource("/immagini/pecora_bianca.png")));
	}

}
