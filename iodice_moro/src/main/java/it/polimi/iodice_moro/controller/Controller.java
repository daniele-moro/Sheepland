package it.polimi.iodice_moro.controller;


import it.polimi.iodice_moro.model.Giocatore;
import it.polimi.iodice_moro.model.Regione;
import it.polimi.iodice_moro.model.StatoPartita;
import it.polimi.iodice_moro.model.Strada;
import it.polimi.iodice_moro.model.TipoMossa;
import it.polimi.iodice_moro.model.TipoTerreno;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class Controller {
	
	/**
	 * Istanza del model di StatoPartita.
	 */
	private StatoPartita statopartita;	
	
	
	/**
	 * Costruttore del controller del giocatore.
	 * @param statopartita Istanza statopartita.
	 * @param giocatore Istanza del giocatore gestito.
	 */
	public Controller(StatoPartita statopartita) {
		this.statopartita = statopartita;
	}

	
	/**
	 * Aggiunge un recinto alla strada, se sono disponibili.
	 * @param strada Strada sulla quale aggiungere il recinto.
	 */
	private void aggiungiRecinto(Strada strada) {
		if(statopartita.getNumRecinti()>0) {
			strada.setRecinto(true);
		}
		else {
			statopartita.setTurnoFinale();
		}
	}
	
	/**
	 * Sposta la pecora nell'altra regione adiacente alla posizione attuale
	 * del giocatore.
	 * Utilizza {@link StatoPartita#getAltraRegione} per ottenere l'altra regione adiacente.
	 * @param Regionepecora regione in cui si trova la pecora.
	 * @param Stradagiocatore strada in cui si trova il giocatore.
	 * 
	 */
	public void spostaPecora(Regione regionePecora) {
		Giocatore giocatore = statopartita.getGiocatoreCorrente();
		Regione regAdiacente = statopartita.getAltraRegione(regionePecora, giocatore.getPosition());
		try {
			regionePecora.removePecora();
			regAdiacente.addPecora();
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("Non ci sono pecore da spostare");
		}
	}
	
	/**
	 * Sposta pecora nera.
	 * @param regionePecora Regione dove si trova la pecora.
	 * @param regAdiacente Regione dove deve essere spostata le pecora.
	 * @see #checkSpostamentoNera
	 */
	public void spostaPecoraNera(Regione regionePecora, Regione regAdiacente) {
		try {
			regionePecora.removePecoraNera();
			regAdiacente.addPecoraNera();
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("Non ci sono pecore da spostare");
		}
	}
	
	/**
	 * Giocatore compra la tessera, decrementando i suoi soldi di un valore pari
	 * al costo attuale della tessera che compra.
	 * @param tipo Tipo della tessera che vuole comprare.
	 * @throws Exception Se il costo della tessera è maggiore dei soldi del giocatore.
	 */
	public void acquistaTessera(TipoTerreno tipo) throws Exception {
		Giocatore giocatore=statopartita.getGiocatoreCorrente();
		int costoTessera=statopartita.getCostoTessera(tipo);
		if (costoTessera > giocatore.getSoldi() ){
			throw new Exception();
		}
		else {
			giocatore.decrSoldi(statopartita.getCostoTessera(tipo));
			giocatore.addTessera(tipo);
		}
	}
	
	/**
	 * Cambia la posizione corrente del giocatore.
	 * @param nuovastrada Nuova posizione.
	 * @throws Exception Se nuova posizione è già occupata da un recinto.
	 * @throws Exception Se non ha abbastanza soldi per muoversi.
	 */
	public void spostaPedina (Strada nuovastrada) throws Exception {
		Giocatore giocatore = statopartita.getGiocatoreCorrente();
		if(nuovastrada.isRecinto()) {
			throw new Exception();
		}
		if(pagaSpostamento(nuovastrada, giocatore)) {
			if(giocatore.getSoldi()==0) {
				throw new Exception();
			}
			else {
				giocatore.decrSoldi();
			}
		}
		this.aggiungiRecinto(giocatore.getPosition());
		giocatore.setPosition(nuovastrada);
	}

	/**
	 * Controlla se la strada è contenuta nelle strada adiacenti alla posizione corrente 
	 * del giocatore.
	 * @param strada Strada da controllare se è contenuta.
	 * @param giocatore Giocatore corrente.
	 * @return Ritorna true se la strada è contenuta.
	 */
	private boolean pagaSpostamento(Strada strada, Giocatore giocatore) {
		return !statopartita.getStradeAdiacenti(giocatore.getPosition()).contains(strada);
	}
	
	/**
	 * Metodo avviato all'inizio del turno per valutare se la pecora nera deve essere
	 * spostata, e chiama il metodo {@link #spostaPecoraNera(Regione, Regione)} per
	 * effettuarlo in caso di risposta positiva.
	 */
	private void checkSpostamentoNera() {
		int valoreDado = lanciaDado();
		Regione posNera=statopartita.getPosPecoraNera();
		List<Strada> stradeConfini=statopartita.getStradeConfini(posNera);
		for(Strada strada : stradeConfini) {
			if(strada.getnCasella()==valoreDado) {
				if(!strada.isRecinto()) {
					Regione nuovaRegionePecora=statopartita.getAltraRegione(posNera, strada);
					spostaPecoraNera(posNera, nuovaRegionePecora);
				}
				else return;
			}
		}
	}
	
	/**
	 * @return Ritorna valore compreso tra 0 e 6 (incluso).
	 */
	private int lanciaDado() {
		Random random=new Random();
		return random.nextInt(7);		
	}
	
	//Calcola punteggio. Cosa ritorna?
	private Map<Giocatore, Integer> calcolaPunteggio() {return null;} 
	
	public void creaGiocatore(String nome, Strada posizione) {}
	private void mossaPossibile(TipoMossa mossaDaEffettuare) {}
	
	/*
	 * Cambia l'ultima mossa nel giocatore corrente. Incremente numero mosse giocatore corrente.
	 * Controlla se il giocatore corrente può fare ancora mosse (n. mosse): se non può farle
	 * azzera le variabili di turno (fineturno() ) nel giocatore corrente e trova
	 * il prossimo giocatore (modifica il StatoPartita.giocatoreCorrente ).
	 * Inoltre se è il turno finale ed è l'ultimo giocatore mette finePartita().
	 * Ritorna il prossimo giocatore.
	 */
	private Giocatore aggiornaTurno(TipoMossa mossaFatta) {return null;} 
	
}
