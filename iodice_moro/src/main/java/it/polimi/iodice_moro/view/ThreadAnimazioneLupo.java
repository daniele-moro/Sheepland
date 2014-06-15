package it.polimi.iodice_moro.view;

import java.awt.Point;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class ThreadAnimazioneLupo implements Runnable {

	
	private JLabel mappa;
	private Point sorg;
	private Point dest;
	private JLabel lupo;
	private View view;
	
	public ThreadAnimazioneLupo(View view, JLabel mappa,JLabel lupo, Point sorg, Point dest){
		this.mappa=mappa;
		this.sorg=sorg;
		this.dest=dest;
		this.lupo=lupo;
		this.view=view;
	}
	
	@Override
	public void run() {
		mappa.remove(lupo);
		Point sorg2=(Point) sorg.clone();
		Point dest2=(Point) dest.clone();
		sorg2.y-=20;
		sorg2.x+=10;
		dest2.y-=20;
		dest2.x+=10;
		view.spostaImmagine(sorg2,dest2, new ImageIcon(this.getClass().getClassLoader().getResource("immagini/lupo.png")));
		mappa.add(lupo);
		lupo.setBounds(dest2.x, dest2.y, lupo.getWidth(), lupo.getHeight());
	}

}