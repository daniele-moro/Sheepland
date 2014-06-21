package it.polimi.iodice_moro.model;

import java.awt.Point;

/**
 * La classe strada estende la classe astratta {@link VerticeGrafo},
 * contiene tutti i dati che riguardano la strada
 * @author Antonio Iodice, Daniele Moro
 *
 */
public class Strada extends VerticeGrafo {
	
	private final int nCasella;
	/**
	 * Indica la presenza o meno di un recinto
	 */
	private boolean recinto;
	
	private String colore;
	
	private Point posizione;
	
	/**
	 * Costruttore che inizializza la classe, verrà chiamato all'inizializzazione della partita
	 * @param nCasella Numero della casella presente sulla strada
	 */
	public Strada(int nCasella){
		this.nCasella=nCasella;
		this.recinto=false;
	}
	/**
	 * Costruttore con parametri, che inizializza la classe
	 * @param nCasella Numero della casella associata
	 * @param colore Colore (ID) associato a questa strada
	 * @param posizione Posizione assegnata a questa strada, rappresenta la posizione della casella assegnata a questa strada
	 */
	public Strada(int nCasella, String colore, Point posizione){
		this.nCasella=nCasella;
		this.recinto=false;
		this.colore=colore;
		this.posizione=posizione;
	}

	/**
	 * @return Colore del giocatore.
	 */
	public String getColore(){
		return colore;
	}
	
	/**
	 * @return Posizione attuale del giocatore.
	 */
	public Point getPosizione(){
		return posizione;
	}
	
	/**
	 * @return Ritorna la presenza o meno del recinto
	 */
	public boolean isRecinto() {
		return recinto;
	}
	
	/**
	 * @param recinto Setta la variabile recinto
	 */
	public void setRecinto(boolean recinto) {
		this.recinto = recinto;
	}
	
	/**
	 * @return Ritorna il numero della casella della classe
	 */
	public int getnCasella() {
		return nCasella;
	}

	
	/**
	 * Override del metodo toString ereditato dalla classe Object, 
	 * usato per avere una stampa leggibile delle informazioni contenute nella classe
	 */
	@Override
	public String toString() {
		return "Strada [nCasella=" + nCasella + ", recinto=" + recinto + "colore=" + colore + "]" + "\n";
	}

	/**
	 * Override del metodo {@link VerticeGrafo#isRegione()} ereditato da VerticeGrafo, 
	 * usato per confermare che l'istanza corrente della classe non è una regione
	 */
	@Override
	public boolean isRegione() {
		return false;
	}

}
