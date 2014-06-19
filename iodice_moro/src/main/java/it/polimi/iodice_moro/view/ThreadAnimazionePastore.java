package it.polimi.iodice_moro.view;

import java.awt.Color;
import java.awt.Point;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;

public class ThreadAnimazionePastore implements Runnable {

	private View view;
	private JLabel pedGiocatore;
	private Point sorg;
	private Point dest;
	private JLayeredPane mappa;
	private Color colore;
	
	public ThreadAnimazionePastore(View view,JLayeredPane mappa, JLabel pedGiocatore, Point sorg, Point dest, Color colore ){
		this.view=view;
		this.pedGiocatore=pedGiocatore;
		this.sorg=sorg;
		this.dest=dest;
		this.mappa=mappa;
		this.colore=colore;
	}

	@Override
	public void run() {
		ImageIcon img = null;
		//carico l'icona della pedina corretta
		if(colore.equals(new Color(255,0,0))){
			img=new ImageIcon(this.getClass().getResource("/immagini/pedinarossa.png"));
		}
		if(colore.equals(new Color(0,255,0))){
			img=new ImageIcon(this.getClass().getResource("/immagini/pedinaverde.png"));
		}
		if(colore.equals(new Color(0,0,255))){
			img=new ImageIcon(this.getClass().getResource("/immagini/pedinaazzurra.png"));
		}
		if(colore.equals(new Color(255,255,0))){
			img=new ImageIcon(this.getClass().getResource("/immagini/pedinagialla.png"));
		}
		//se la pedina del giocatore non c'è, la creo e la aggiungo alla Map delle pedine dei giocatori
		if(pedGiocatore == null){
			pedGiocatore = new JLabel();
			mappa.add(pedGiocatore, View.SHEPPARD_LAYER);
			view.addPedinaGiocatore(colore, pedGiocatore);
		}
		pedGiocatore.setVisible(false);
		pedGiocatore.setIcon(img);
		//setto subito la nuova posizione della pedina
		pedGiocatore.setBounds(dest.x,dest.y, img.getIconWidth(), img.getIconHeight());
		
		//è usato anche nel caso in cui il pastore non sia ancora stato posizionato,
		//in questo caso viene chiamato con sorg = null
		if(sorg!=null){
			//se abbiamo il primo parametro, significa che dobbiamo far vedere l'animazione del pastore
			view.spostaImmagine(sorg, dest, img);
		}
		
		
		pedGiocatore.setVisible(true);
		mappa.repaint();
	}
	
}
