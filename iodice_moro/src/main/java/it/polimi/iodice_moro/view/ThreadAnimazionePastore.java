package it.polimi.iodice_moro.view;

import java.awt.Color;
import java.awt.Point;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class ThreadAnimazionePastore implements Runnable {

	private View view;
	private JLabel pedGiocatore;
	private Point sorg;
	private Point dest;
	private JLabel mappa;
	private Color colore;
	
	public ThreadAnimazionePastore(View view,JLabel mappa, JLabel pedGiocatore, Point sorg, Point dest, Color colore ){
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
			img=new ImageIcon("immagini/pedinarossa.png");
		}
		if(colore.equals(new Color(0,255,0))){
			img=new ImageIcon("immagini/pedinaverde.png");
		}
		if(colore.equals(new Color(0,0,255))){
			img=new ImageIcon("immagini/pedinaazzurra.png");
		}
		if(colore.equals(new Color(255,255,0))){
			img=new ImageIcon("immagini/pedinagialla.png");
		}

		if(pedGiocatore!=null){
			pedGiocatore.setVisible(false);
		}
		//è usato anche nel caso in cui il pastore non sia ancora stato posizionato,
		//in questo caso viene chiamato con sorg = null
		if(sorg!=null){
			//se abbiamo il primo parametro, significa che dobbiamo far vedere l'animazione del pastore
			view.spostaImmagine(sorg, dest, img);
		}
		
		//se la pedina del giocatore non c'è, la creo e la aggiungo alla Map delle pedine dei giocatori
		if(pedGiocatore == null){
			pedGiocatore = new JLabel();
			pedGiocatore.setIcon(img);
			mappa.add(pedGiocatore);
			view.addPedinaGiocatore(colore, pedGiocatore);
		} else{
			pedGiocatore.setVisible(true);
		}
		pedGiocatore.setBounds(dest.x,dest.y, img.getIconWidth(), img.getIconHeight());
		mappa.repaint();

	}
	
}
