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




	private static final Logger LOGGER = Logger.getLogger("it.polimi.iodice_moro.view");
	BufferedImage img;
	
	/**
	 * Costruttore per avere un immagine di sfondo nella label
	 * @param f InputStream dell'immagine di sfondo
	 */
	public MovableLabel(InputStream f){
		super();
		try{
			img = ImageIO.read(f);
		} catch(IOException e) {
			LOGGER.log(Level.SEVERE, "Errore di IO", e);
		} catch (IllegalArgumentException e) {
			LOGGER.log(Level.SEVERE, "Parameter is null", e);
		}
		removeAfterAnimation=false;
	}
	/**
	 * Costruttore normal, crea una label Movable senza sfondo
	 */
	public MovableLabel(){
		super();
		removeAfterAnimation=false;
	}

	/**
	 * Metodo per settare l'immagine di sfondo
	 * @param img BufferedImage di sfondo
	 */
	public void setImage(BufferedImage img){
		this.img = img;
	}

	/**
	 * Sovrascrivo il metod.o paintComponent, disegnando l'immagine che è stata passato nel costruttore.
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
	 * Animazione del movimento in una serie di punti da attraversare
	 * @param destination Lista di punti da attraversare
	 * @param timeMillisec Durata dell'animazione tra due punti della lista
	 */
	public void moveTo(List<Point> destination, int timeMillisec) {
		final int durationAnimation=timeMillisec;
		//Iteratore che mi serve per iterare su tutti i punti in cui si deve muovere
		final Iterator<Point> it = destination.iterator();
		//Eseguo il primo movimento subito
		if(it.hasNext()){
			moveTo(it.next(),timeMillisec);
		}
		//creo un listener che venga attivato dal timer
		ActionListener animationTask = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				//ogni volta che viene attivato il timer, controllo se ci sono ancora movimenti da fare
				if(it.hasNext()){
					//nel caso positivo avvio il movimento
					moveTo(it.next(),durationAnimation);
				} else{
					//in caso negativo fermo il timer
					((Timer)evt.getSource()).stop();
				}
			}
		};
		/*
		 * Inizializzo un timer che ogni durataAnimation millisecondi, 
		 * in base a quanto deve durare una animazione tra due strade
		 * chiama l'azione collegata ad animationTast
		 */
		Timer timer = new Timer(durationAnimation,animationTask);
		timer.start();
	}
	
	/**
	 * Fa partire l'animazione dalla posizione attuale del componente fino alla posizione passata come parametro
	 * Se l'oggetto è già animato, l'animazione precedente viene annullata
	 * @param destination Point che corrisponde alla destinazione
	 * @param timeMillisec Durata dell'animazione
	 */
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
	
	/**
	 * Setta il parametro che indica al metodo {@link MovableLabel#move(int, int)} se l'immagine
	 * è da rimuovere dopo l'animazione.
	 */
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
