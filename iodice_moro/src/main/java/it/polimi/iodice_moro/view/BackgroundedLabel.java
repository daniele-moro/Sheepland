package it.polimi.iodice_moro.view;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.JLabel;

public class BackgroundedLabel extends JLabel {
	
	private static final Logger logger =  Logger.getLogger("it.polimi.iodice_moro.view");
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	BufferedImage img;
	
	public BackgroundedLabel(InputStream f){
		super(); //crea un JPanel con doubleBuffered true
		try{
			setImage(ImageIO.read(f));
		} catch(IOException e) {
			logger.log(Level.SEVERE, "Errore di IO", e);
		} catch (IllegalArgumentException e) {
			logger.log(Level.SEVERE, "Parameter is null", e);
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
