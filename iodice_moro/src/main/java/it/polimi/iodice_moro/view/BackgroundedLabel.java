package it.polimi.iodice_moro.view;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JLabel;

public class BackgroundedLabel extends JLabel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	BufferedImage img;
	
	public BackgroundedLabel(File f){
		super(); //crea un JPanel con doubleBuffered true
		try{
			setImage(ImageIO.read(f));
		} catch(Exception e) {
		}
	}
	
	public void setImage(BufferedImage img){
		this.img = img;
	}

	// sovrascrivi il metodo paintComponent passandogli l'immagine partendo dalle coordinate 0,0 senza usare un ImageObserver (null)
	public void paintComponent(Graphics g){
		g.drawImage(img,0, 0, null);
		super.paintComponent(g);
	}

}
