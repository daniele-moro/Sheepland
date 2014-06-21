package it.polimi.iodice_moro.model;

import java.awt.Point;

/**
 * La classe regione estende la classe astratta {@link VerticeGrafo},
 * contiene tutti i dati che riguardano la regione
 * @author Antonio Iodice, Daniele Moro
 */

public class Regione extends VerticeGrafo{
	
	/**
	 * Numero di pecore nella regione.
	 */
	private int numPecore;
	
	/**
	 * Presenta pecora nera nella regione.
	 */
	private boolean pecoraNera;
	
	/**
	 * Presneta lupo nella regione.
	 */
	private boolean lupo;
	
	/**
	 * Tipo del terreno. Attributo immutabile.
	 */
	private final TipoTerreno tipo;
	
	/**
	 * Colore della regione
	 */
	private String colore;
	
	private Point posizione;
	
	//Costruttore per l'inizializzazione della mappa nel caricamento da XML
	/**
	 * Costruttore che inizializza la classe, verrà chiamato all'inizializzazione della partita
	 * @param tipo Tipo del terreno della regione che si sta inizializzando
	 */
	public Regione(String tipo){
		this.tipo=TipoTerreno.parseInput(tipo);
		this.numPecore=0;
		this.pecoraNera=false;
	}
	
	/**
	 * Costruttore con parametri
	 * @param tipo Tipo del terreno della regione che si sta inizializzando
	 * @param colore Colore (ID) associato alla regione che si sta inizializzando
	 * @param posizione Posizione nella mappa assegnata a questa regione(usata per le label delle pecore)
	 */
	public Regione(String tipo, String colore, Point posizione){
		this.tipo=TipoTerreno.parseInput(tipo);
		this.colore=colore;
		this.posizione=posizione;
		this.numPecore=0;
		this.pecoraNera=false;
	}

	
	
	//GETTER & SETTER degli attributi
	/**
	 * @return Colore (ID) associato a questa regione
	 */
	public String getColore(){
		return colore;
	}
	/**
	 * @return Posizione associata a questa regione
	 */
	public Point getPosizione(){
		return posizione;
	}
	
	
	/**
	 * @return Presenza o meno della PecoraNera
	 */
	public boolean isPecoraNera() {
		return pecoraNera;
	}
	
	/**
	 * @return Presenza o meno del lupo nella regione.
	 */
	public boolean isLupo(){
		return lupo;
	}
	/**
	 * @param pecoraNera Valore che indica la presenza o meno della pecora nera
	 */
	public void setPecoraNera(boolean pecoraNera) {
		this.pecoraNera = pecoraNera;
	}
	
	/**
	 * @param lupo Valore che indica la presenza o meno del lupo.
	 */
	public void setLupo(boolean lupo) {
		this.lupo = lupo;
	}
	
	/**
	 * @return Ritorna il numero di pecore presente nella regione
	 */
	public int getNumPecore() {
		return numPecore;
	}
	/**
	 * @param numPecore Numero di pecore presenti nella regione
	 */
	public void setNumPecore(int numPecore) {
		this.numPecore=numPecore;
	}
	/**
	 * @return Ritorna il tipo di terreno della regione
	 */
	public TipoTerreno getTipo() {
		return tipo;
	}

	/**
	 * Override del metodo toString ereditato dalla classe Object, 
	 * usato per avere una stampa leggibile delle informazioni contenute nella classe
	 */
	@Override
	public String toString() {
		return "Regione [numPecore=" + numPecore + ", pecoraNera=" + pecoraNera
				+ ", lupo=" + lupo + ", tipo=" + tipo + "colore=" + colore +"]" +"\n";
	}

	/**
	 * Override del metodo {@link VerticeGrafo#isRegione()} ereditato da VerticeGrafo, 
	 * usato per confermare che l'istanza corrente della classe è una regione
	 */
	@Override
	public boolean isRegione() {
		return true;
	}
	
	/**
	 * Aumenta numero pecore della regione di un'unità.
	 */
	public void addPecora() {
		numPecore++;
	}

	/**
	 * Diminuisce numero pecore della regione di un'unità.
	 */
	public void removePecora() {
			numPecore--;
	}

	/**
	 * Rimuovere pecora nera dalla regione.
	 */
	public void removePecoraNera() {
		setPecoraNera(false);
	}
	
	/**
	 * Aggiunge pecora nera alla regione.
	 */
	public void addPecoraNera() {
		setPecoraNera(true);	
	}
	
	/**
	 * Rimuove il lupo dalla regione.
	 */
	public void removeLupo() {
		setLupo(false);
	}
	
	/**
	 * Aggiunge il lupo alla regione.
	 */
	public void addLupo() {
		setLupo(true);
	}

}
