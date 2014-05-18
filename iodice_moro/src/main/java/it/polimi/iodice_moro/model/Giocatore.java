package it.polimi.iodice_moro.model;

import java.util.ArrayList;
import java.util.List;

public class Giocatore {

	//ATTRIBUTI
	private String nome;
	private int soldi;
	private int turno;
	private int ultimaMossa;
	private int numMosse;
	private boolean pastoreSpostato;
	private Strada position;

	//Lista di tessere (enum TipoTerreno)
	private List<TipoTerreno> listaTessere = new ArrayList<TipoTerreno>();
	
	//CONSTRUCTOR
	
	Giocatore (String nome) {
		this.nome=nome;
		pastoreSpostato=false;
		numMosse=0;
		ultimaMossa=0;
		//TURNO???
	}
	
	Giocatore (String nome, Strada position) {
		this(nome);
		this.position=position;
	}

	//GETTERS & SETTERS
	public String getNome() {
		return nome;
	}

	// Serve?
	public void setNome(String nome) {
		this.nome = nome;
	}

	public int getSoldi() {
		return soldi;
	}

	public void setSoldi(int soldi) {
		this.soldi = soldi;
	}

	public int getTurno() {
		return turno;
	}

	public void setTurno(int turno) {
		this.turno = turno;
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

	public void setNumMosse(int numMosse) {
		this.numMosse = numMosse;
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

	// Getter di listaTessere. Setter non implementato.
	public List<TipoTerreno> getListaTessere() {
		return listaTessere;
	}

	public void addTessera(TipoTerreno tessera) {
		listaTessere.add(tessera);
	}
	
	//
	public void mossaFatta(){
		numMosse++;
	}
}
