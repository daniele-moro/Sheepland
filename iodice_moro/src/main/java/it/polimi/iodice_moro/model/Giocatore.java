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
	
	private Strada position;
	private Map<String,Integer> tesserePossedute= new HashMap<String,Integer>();

	
	//CONSTRUCTOR
	Giocatore (String nome) {
		this.nome=nome;
		pastoreSpostato=false;
		numMosse=0;
		ultimaMossa=0;
		initTessere();
	}
	
	private void initTessere(){
		for(TipoTerreno t: TipoTerreno.values()){
			if(t.toString().equals("sheepsburg")){
				tesserePossedute.put(t.toString(), 0);
			}
		}
	}
	
	Giocatore (String nome, Strada position) {
		this(nome);
		this.position=position;
	}

	//GETTERS & SETTERS
	public String getNome() {
		return nome;
	}

	public int getSoldi() {
		return soldi;
	}

	public void setSoldi(int soldi) {
		this.soldi = soldi;
	}


	public int getUltimaMossa() {
		return ultimaMossa;
	}

	public void setUltimaMossa(int ultimaMossa) {
		this.ultimaMossa = ultimaMossa;
	}

	public int getNumMosse() {
		return numMosse;
	}

	public void incNumMosse() {
		this.numMosse++;
	}

	public boolean isPastoreSpostato() {
		return pastoreSpostato;
	}

	public void setPastoreSpostato(boolean pastoreSpostato) {
		this.pastoreSpostato = pastoreSpostato;
	}
	
	public Strada getPosition() {
		return position;
	}

	public void setPosition(Strada position) {
		this.position = position;
	}

	/**
	 * @return Ritorna l'elenco delle tessere possedute con il numero di tessere acuquistate per ogni tipo di terreno
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
	
	
	//Metodo chiamato alla fine del turno del giocatore per azzerare gli attributi usati per la gestione del turno
	/*public void fineTurno(){
		numMosse=0;
		ultimaMossa=-1;
		pastoreSpostato=false;
	}*/
}
