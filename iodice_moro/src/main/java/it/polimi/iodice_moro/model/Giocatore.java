package it.polimi.iodice_moro.model;

import java.awt.Color;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * La classe Giocatore rappresenta l'istanza del giocatore, con tutte le variabili che riguardano
 * il suo stato e i metodi per modificarli.
 * @author Antonio Iodice, Daniele Moro
 */

public class Giocatore implements Serializable{

	
	private static final long serialVersionUID = -46069195325859567L;
	
	public static final int SOLDI_INIT = 20;
	public static final int SOLDI_2_GIOCATORI = 30;
	//ATTRIBUTI
	private String nome;
	private int soldi;
	
	//Attributi di gestione del turno
	/**
	 * Attributo per gestire l'ultima mossa che il giocatore 
	 * rappresentato da questa istanza della classe Giocatore ha fatto nel turno corrente
	 */
	private TipoMossa ultimaMossa;
	/**
	 * Numero di mosse che il gicatore ha fatto nel turno corrente
	 */
	private int numMosse;
	/**
	 * Attributo per sapere se nel turno corrente il gicoatore ha gia spostato il proprio pastore
	 */
	private boolean pastoreSpostato;
	
	/**
	 * Posizione giocatore nella mappa
	 */
	private transient Strada position;
	
	/**
	 * Seconda posizione del giocatore, usato per le partite con due giocatori
	 */
	private transient Strada position2;
	
	/**
	 * Colore associato al giocatore
	 */
	private Color colore;
	
	/**
	 * Tabella hash con l'elenco delle tessere e il relativo
	 * numero di tessere possedute.
	 */
	private Map<String,Integer> tesserePossedute= new HashMap<String,Integer>();

	
	/**
	 * Costruttore classe Giocatore.
	 */
	public Giocatore (String nome) {
		this.nome=nome;
		pastoreSpostato=false;
		numMosse=0;
		ultimaMossa=TipoMossa.NO_MOSSA;
		soldi=SOLDI_INIT;
		initTessere();
		position=null;
		this.colore=null;
	}
	
	/**
	 * Costruttore classe giocatore.
	 */
	public Giocatore (String nome, Strada position) {
		this(nome);
		this.position=position;
	}
	
	/**
	 * Costruttore con parametri
	 * @param nome Nome del giocatore da creare
	 * @param colore Colore associato al giocatore
	 */
	public Giocatore (String nome, Color colore) {
		this(nome);
		this.colore=colore;
	}
	
	/**
	 * Inizializza la tabella {@link Giocatore#tesserePossedute}.
	 */
	private void initTessere(){
		for(TipoTerreno t: TipoTerreno.values()){
			tesserePossedute.put(t.toString(), 0);
			}
	}

	/**
	 * Ritorna il Colore del giocatore
	 * @return Colore del giocatore
	 */
	public Color getColore(){
		return colore;
	}
	
	
	/**
	 * @return Ritorna nome del giocatore.
	 */
	public String getNome() {
		return nome;
	}

	/**
	 * @return Ritorna quantità soldi giocatore.
	 */
	public int getSoldi() {
		return soldi;
	}

	/**
	 * Decrementa i soldi del giocatore di un'unità.
	 */
	public void decrSoldi() {
		soldi--;
	}
	
	/**
	 * Decrementa i soldi del giocatore di una quantità pari a val.
	 * @param val Valore da decrementare.
	 */
	public void decrSoldi(int val) {
		soldi=soldi-val;
	}


	/**
	 * @return Ritorna il valore dell'attributo ultimaMossa.
	 */
	public TipoMossa getUltimaMossa() {
		return ultimaMossa;
	}

	/**
	 * Imposta il valore dell'attributo ultimamossa.
	 * @param ultimaMossa Valore da impostare.
	 */
	public void setUltimaMossa(TipoMossa ultimaMossa) {
		this.ultimaMossa = ultimaMossa;
	}

	/**
	 * @return Ritorna numero mosse effettuate dal giocatore.
	 */
	public int getNumMosse() {
		return numMosse;
	}

	/**
	 * Aumenta numero mosse effettuate dal giocatore di un'unità.
	 */
	public void incNumMosse() {
		this.numMosse++;
	}

	/** 
	 * @return Ritorna true se è stato spostato il pastore durante il turno.
	 */
	public boolean isPastoreSpostato() {
		return pastoreSpostato;
	}

	/**
	 * Cambia il valore di {@link Giocatore#pastoreSpostato}.
	 * @param pastoreSpostato Valore da impostare.
	 */
	public void setPastoreSpostato(boolean pastoreSpostato) {
		this.pastoreSpostato = pastoreSpostato;
	}
	
	/**
	 * @return Ritorna posizione principale del giocatore
	 */
	public Strada getPosition() {
		return position;
	}
	
	/**
	 * @return Ritorna la seconda posizione del giocatore
	 */
	public Strada getPosition2(){
		return position2;
	}

	/**
	 * Cambia la posizione del giocatore, cambia la posizione principale
	 * @param position Nuova posizione giocatore.
	 */
	public void setPosition(Strada position) {
		this.position = position;
	}
	
	/**
	 * Cambia la posizione del giocatore.
	 * @param position Nuova posizione giocatore.
	 * @param position2 Seconda nuova posizione del giocatore.
	 */
	public void setPosition2(Strada position2) {
		this.position2=position2;
	}
	
	/**
	 * Cambia la posizione in uso dal giocatore. 
	 * Postcondizione è che se la posizione passata per parametro non è una di quelle del
	 * giocatore, il metodo non funziona come dovrebbe.
	 * @param pos Nuova posizione corrente.
	 */
	public void setPosizioneTurno(Strada pos) {
		if(!(position.equals(pos))&&position2.equals(pos)) {
			position2=position;
			position=pos;
		}
	}

	/**
	 * @return Ritorna l'elenco delle tessere possedute con il numero di tessere
	 * acquistate per ogni tipo di terreno
	 */
	public Map<String,Integer> getTesserePossedute() {
		return tesserePossedute;
	}

	/**
	 * Metodo che aggiunge la tessera appena acquistata all'elenco di tessere possedute dal giocatore
	 * @param tessera Tessera del terreno che si è comperato
	 */
	public void addTessera(TipoTerreno tessera) {
		int num=tesserePossedute.get(tessera.toString());
		num++;
		tesserePossedute.put(tessera.toString(), num);
	}

	
	/**
	 * Metodo chiamato alla fine del turno del giocatore per azzerare
	 * gli attributi usati per la gestione del turno.
	 */
	public void azzeraTurno(){
		numMosse=0;
		ultimaMossa=TipoMossa.NO_MOSSA;
		pastoreSpostato=false;
	}
	
	/**
	 * Controlla se il numero di mosse è pari a 3
	 * @return true: Numero di mosse uguale a 3 false: Numero di mosse diverso da 3
	 */
	public boolean treMosse(){
		return numMosse==3;
	}

	/**
	 * @param color Colore che è stato assegnato al giocatore
	 */
	public void setColore(Color color) {
		this.colore=color;
		
	}

	/**
	 * Inizializza i soldi alla quantià di soldi utilizzati per le partite con due giocatori
	 */
	public void initSoldiDueGiocatori() {
		this.soldi=SOLDI_2_GIOCATORI;
		
	}

	/**
	 * Aumenta i soldi del giocatore di un valore pari al parametro.
	 * @param soldiAggiunti Soldi da aggiungere.
	 */
	public void incrSoldi(int soldiAggiunti) {
		this.soldi=this.soldi+soldiAggiunti;
		
	}
}
