package it.polimi.iodice_moro.model;

import java.util.HashMap;
import java.util.Map;

public class Giocatore {

	public static final int SOLDI_INIT = 20;
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
	private Strada position;
	
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
	}
	
	/**
	 * Costruttore classe giocatore.
	 */
	public Giocatore (String nome, Strada position) {
		this(nome);
		this.position=position;
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
	 * @return Ritorna posizione giocatore.
	 */
	public Strada getPosition() {
		return position;
	}

	/**
	 * Cambia la posizione del giocatore.
	 * @param position Nuova posizione giocatore.
	 */
	public void setPosition(Strada position) {
		this.position = position;
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
		if(numMosse==3){
			return true;
		} else{
			return false;
		}
	}
	
}
