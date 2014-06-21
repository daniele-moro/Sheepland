package it.polimi.iodice_moro.view;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.Timer;


/**
 * Questa classe è un estensione di una JLabel, aggiunge la possibilità di muovere
 *  la label dalla posizione in cui si trova verso un'altra posizione chiamando il metodo moveTo() 
 *  specificando la destinazione e la durata dell'animazione.
 *  Inoltre la classe include la possibilità di avere una immagine di sfondo attraverso
 *  l'invocazione del costruttore con passaggio dell'inpustream del file dell'immagine.
 *  La classe permette anche che alla fine dell'animazione la label venga rimossa dall'oggetto padre
 *  (utilizzando il metodo setRemoveAfterAnimation()),
 *   utile nel caso si debbano usare label utili per la sola animazione
 *   @author Daniele Moro
 *
 */
public class MovableLabel extends JLabel {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final long DURATION_MUL = (long) 1E6;
	private static final int TIMER_DELAY = 10;


	private Point endPosition;
	private Point startPosition;
	private long startingTime;
	private long animationDuration;
	private boolean animating;
	private boolean removeAfterAnimation;




	private static final Logger logger =  Logger.getLogger("it.polimi.iodice_moro.view");
	BufferedImage img;
	
	/**
	 * Costruttore per avere un immagine di sfondo nella label
	 * @param f InputStream dell'immagine di sfondo
	 */
	public MovableLabel(InputStream f){
		super();
		try{
			setImage(ImageIO.read(f));
		} catch(IOException e) {
			logger.log(Level.SEVERE, "Errore di IO", e);
		} catch (IllegalArgumentException e) {
			logger.log(Level.SEVERE, "Parameter is null", e);
		}
		removeAfterAnimation=false;
	}
	public MovableLabel(){
		super();
		removeAfterAnimation=false;
	}
	
	public void setImage(BufferedImage img){
		this.img = img;
	}

	/**
	 * Sovrascrivo il metodo paintComponent, disegnando l'immagine che è stata passato nel costruttore.
	 * Disegna l'immagine solo se questa è stata aggiunta nel costruttore
	 */
	public void paintComponent(Graphics g){
		if(img!=null){
			g.drawImage(img,0, 0, null);
		}
		super.paintComponent(g);
	}

//ANIMAZIONE
	/**
	 * Fa partire l'animazione dalla posizione attuale del componente fino alla posizione passata come parametro
	 * Se l'oggetto è già animato, l'animazione precedente viene annullata
	 * @param destination Point che corrisponde alla destinazione
	 * @param timeMillisec Durata dell'animazione
	 */
	public void moveTo(List<Point> destination, int timeMillisec) {
		//calcolo la durata dell'animazione del movimento tra due strade
		final int durataAnimation=timeMillisec/destination.size();
		final Iterator<Point> it = destination.iterator();
		if(it.hasNext()){
			moveTo(it.next(),durataAnimation);
		}
		ActionListener animationTask = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				if(it.hasNext()){
					moveTo(it.next(),durataAnimation);
				} else{
					((Timer)evt.getSource()).stop();
				}
			}
		};
		//Inizializzo un timer che ogni x secondi, in base a quanto deve durare una animazione tra due strade
		//chiama l'azione collegata ad animationTast
		Timer timer = new Timer(durataAnimation,animationTask);
		timer.start();
	}
	
	public void moveTo(Point destination, int timeMillisec){
		startingTime = System.nanoTime();
		animationDuration = (long) (timeMillisec*DURATION_MUL);
		startPosition = getBounds().getLocation();
		this.endPosition = destination;
		animating = true;
		performAnimation();
	}
	
	/**
	 * @return True se l'animazione è in corso
	 */
	public boolean isAnimating() {
		return animating;
	}
	
	public void setRemoveAfterAnimation(){
		removeAfterAnimation=true;
	}



	private void performAnimation() {
		
		//Questo blocco di codice verrà ripetuto ogni 10ms
		ActionListener animationTask = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent evt) {
				//Prelevo il currentTime in nano secondi
				long now = System.nanoTime();
				//progress è un numero tra 0 e 1 che rappresenta il progresso dell'animazione 
				//in relazione al tempo passato dall'inizio dell'animazione
				double progress =  now > (startingTime + animationDuration) ?
						1 : (double)(now - startingTime) / (double)animationDuration ;

				if(now < startingTime) {
					progress = 0;
				}

				//Calcola la nuova posizione in cui posizionare la label
				double newX = startPosition.x + (endPosition.x - startPosition.x)*progress;
				double newY = startPosition.y + (endPosition.y - startPosition.y)*progress;


				Point newPosition = new Point((int)newX,(int)newY);

				//Posiziona la label nella nuova posizione
				MovableLabel.this.setLocation(newPosition);
				
				//Controlla quando l'animazione deve terminare
				if(progress == 1) {
					if(removeAfterAnimation){
						//AGGIUNTA:
						//rimuovo la label dopo il movimento se ho settato la removeAfterAnimation
						MovableLabel.this.getParent().remove(MovableLabel.this);
					}
					((Timer)evt.getSource()).stop();
					animating = false;
				}
			}
		};

		//Inizializzo un timer che ogni 10ms chiama l'azione collegata ad animationTast
		Timer timer = new Timer(TIMER_DELAY,animationTask);
		timer.start();
	}
}
