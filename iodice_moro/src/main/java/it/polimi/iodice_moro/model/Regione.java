package it.polimi.iodice_moro.model;
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
	 * Tipo del terreno. Attributo immutabile.
	 */
	private final TipoTerreno tipo;
	
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

	
	
	//GETTER & SETTER degli attributi
	/**
	 * @return Presenza o meno della PecoraNera
	 */
	public boolean isPecoraNera() {
		return pecoraNera;
	}
	/**
	 * @param pecoraNera Valore che indica la presenza o meno della pecora nera
	 */
	public void setPecoraNera(boolean pecoraNera) {
		this.pecoraNera = pecoraNera;
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
				+ ", tipo=" + tipo + "]" +"\n";
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
	 * Aggiunge pecora nera.
	 */
	public void addPecoraNera() {
		setPecoraNera(true);	
	}
}
