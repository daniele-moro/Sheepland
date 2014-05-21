package it.polimi.iodice_moro.model;

import java.util.HashMap;
import java.util.Map;

public class Giocatore {

	//ATTRIBUTI
	private String nome;
	private int soldi;
	
	//Attributi di gestione del turno
	private int ultimaMossa;
	private int numMosse;
	private boolean pastoreSpostato;
	
	/**
	 * Posizione giocatore.
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
	Giocatore (String nome) {
		this.nome=nome;
		pastoreSpostato=false;
		numMosse=0;
		ultimaMossa=0;
		initTessere();
	}
	
	/**
	 * Costruttore classe giocatore.
	 */
	Giocatore (String nome, Strada position) {
		this(nome);
		this.position=position;
	}
	
	/**
	 * Inizializza la tabella {@link Giocatore#tesserePossedute}.
	 */
	private void initTessere(){
		for(TipoTerreno t: TipoTerreno.values()){
			if(!t.toString().equals("sheepsburg")){
				tesserePossedute.put(t.toString(), 0);
			}
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

	/*
	**
	 * Imposta quantità soldi giocatore.
	 * @param soldi Valore da impostare.
	 
	public void setSoldi(int soldi) {
		this.soldi = soldi;
	}
	*/

	/**
	 * Decrementa i soldi del giocatore di un'unità.
	 */
	public void decrSoldi() {
		soldi--;
	}


	/**
	 * @return Ritorna il valore dell'attributo ultimaMossa.
	 */
	public int getUltimaMossa() {
		return ultimaMossa;
	}

	/**
	 * Imposta il valore dell'attributo ultimamossa.
	 * @param ultimaMossa Valore da impostare.
	 */
	public void setUltimaMossa(int ultimaMossa) {
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

	/*
	public boolean devePagare(Strada strada) {
		List<> adiacenti = new ArrayList<>(statopartita.getStradeAdiacenti(strada));
		if(strada.equals(adiacenti.get(0)) || strada.equals(adiacenti.get(1)))
			return true;
		return false;
	}
	*/
	
	
	//Metodo chiamato alla fine del turno del giocatore per azzerare gli attributi usati per la gestione del turno
	/*public void fineTurno(){
		numMosse=0;
		ultimaMossa=-1;
		pastoreSpostato=false;
	}*/
}
