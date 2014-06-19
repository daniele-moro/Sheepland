package it.polimi.iodice_moro.view;

import java.awt.Point;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;


public class ThreadAnimazionePecoraNera implements Runnable {

	
	private JLayeredPane mappa;
	private Point sorg;
	private Point dest;
	private JLabel pecoraNera;
	private View view;
	
	public ThreadAnimazionePecoraNera(View view, JLayeredPane mappa,JLabel pecoraNera, Point sorg, Point dest){
		this.mappa=mappa;
		this.sorg=sorg;
		this.dest=dest;
		this.pecoraNera=pecoraNera;
		this.view=view;
	}
	
	@Override
	public void run() {
		mappa.remove(pecoraNera);
		Point sorg2=(Point) sorg.clone();
		Point dest2=(Point) dest.clone();
		sorg2.y-=20;
		sorg2.x+=10;
		dest2.y-=20;
		dest2.x+=10;
		view.spostaImmagine(sorg2,dest2, new ImageIcon(this.getClass().getResource("/immagini/pecora_nera.png")));
		mappa.add(pecoraNera,View.SHEEP_LAYER);
		pecoraNera.setBounds(dest2.x, dest2.y, pecoraNera.getWidth(), pecoraNera.getHeight());
	}

}
