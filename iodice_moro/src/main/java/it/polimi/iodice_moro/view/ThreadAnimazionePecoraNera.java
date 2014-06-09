package it.polimi.iodice_moro.view;

import java.awt.Point;

import javax.swing.ImageIcon;
import javax.swing.JLabel;


public class ThreadAnimazionePecoraNera implements Runnable {

	
	private JLabel mappa;
	private Point sorg;
	private Point dest;
	private JLabel pecoraNera;
	
	public ThreadAnimazionePecoraNera(JLabel mappa,JLabel pecoraNera, Point sorg, Point dest){
		this.mappa=mappa;
		this.sorg=sorg;
		this.dest=dest;
		this.pecoraNera=pecoraNera;
	}
	
	@Override
	public void run() {
		mappa.remove(pecoraNera);
		Point sorg2=(Point) sorg.clone();
		Point dest2=(Point) dest.clone();
		sorg2.y-=21;
		dest2.y-=21;
		spostaImmagine(sorg2,dest2, new ImageIcon("immagini/pecora_nera.png"));
		mappa.add(pecoraNera);
		pecoraNera.setBounds(dest2.x, dest2.y, pecoraNera.getWidth(), pecoraNera.getHeight());
	}
	
	
	/**
	 * Animazione di spostamento di un immagine, nel nostro caso sarà una pecora
	 * @param sorg Point sorgente
	 * @param dest Point Destinazione
	 * @param image Sfondo della label da spostare
	 */
	void spostaImmagine(Point sorg, Point dest, ImageIcon image){
		JLabel lblMove = new JLabel();
		lblMove.setIcon(image);
		
		//Posizioni x e y
		double posx=sorg.x;
		double posy=sorg.y;
		lblMove.setLocation((int)posx,(int)posy);
		
		//incrementi di ogni passo nei due assi
		double incx=(dest.x-sorg.x)/100.0;
		double incy=(dest.y-sorg.y)/100.0;
		try{
			mappa.add(lblMove);
			for(int i=0; i<100;i++){
				lblMove.setBounds((int)posx, (int)posy,image.getIconWidth(),image.getIconHeight());
				Thread.sleep(15);
				posx+=incx;
				posy+=incy;
			}
			mappa.remove(lblMove);
			mappa.repaint();
		}catch(InterruptedException e){
			e.printStackTrace();
		}
	}
	

}
