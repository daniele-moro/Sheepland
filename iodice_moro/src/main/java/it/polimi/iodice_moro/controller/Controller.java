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
	private StatoPartita statoPartita;	
	
	
	/**
	 * Costruttore del controller del giocatore.
	 * @param statopartita Istanza statopartita.
	 * @param giocatore Istanza del giocatore gestito.
	 */
	public Controller(StatoPartita statopartita) {
		this.statoPartita = statopartita;
	}

	
	/**
	 * Aggiunge un recinto alla strada, se sono disponibili.
	 * @param strada Strada sulla quale aggiungere il recinto.
	 */
	private void aggiungiRecinto(Strada strada) {
		if(statoPartita.getNumRecinti()>0) {
			strada.setRecinto(true);
		}
		else {
			statoPartita.setTurnoFinale();
		}
	}
	
	/**
	 * Sposta la pecora nell'altra regione adiacente alla posizione attuale
	 * del giocatore.
	 * Utilizza {@link StatoPartita#getAltraRegione} per ottenere l'altra regione adiacente.
	 * @param Regionepecora regione in cui si trova la pecora.
	 * @param Stradagiocatore strada in cui si trova il giocatore.
	 * @throws Exception Se non ci sono pecore da spostare.
	 */
	public void spostaPecora(Regione regionePecora) throws Exception {
		Giocatore giocatore = statoPartita.getGiocatoreCorrente();
		Regione regAdiacente = statoPartita.getAltraRegione(regionePecora, giocatore.getPosition());

		if(regionePecora.getNumPecore()>0) {
			regionePecora.removePecora();
			regAdiacente.addPecora();
		}
		else throw new Exception();

	}
	
	/**
	 * Sposta pecora nera.
	 * @param regionePecora Regione dove si trova la pecora.
	 * @param regAdiacente Regione dove deve essere spostata le pecora.
	 * @throws Exception se non ci sono pecore nere da spostare.
	 * @see #checkSpostamentoNera
	 */
	public void spostaPecoraNera(Regione regionePecora, Regione regAdiacente) throws Exception {
		if(regionePecora.isPecoraNera()==true) {
			regionePecora.removePecoraNera();
			regAdiacente.addPecoraNera();
		}
		else throw new Exception();
	}


	/**
	 * Giocatore compra la tessera, decrementando i suoi soldi di un valore pari
	 * al costo attuale della tessera che compra.
	 * @param tipo Tipo della tessera che vuole comprare.
	 * @throws Exception Se il costo della tessera è maggiore dei soldi del giocatore.
	 */
	public void acquistaTessera(TipoTerreno tipo) throws Exception {
		Giocatore giocatore=statoPartita.getGiocatoreCorrente();
		int costoTessera=statoPartita.getCostoTessera(tipo);
		if (costoTessera > giocatore.getSoldi() ) {throw new Exception();}
		else {
			giocatore.decrSoldi(statoPartita.getCostoTessera(tipo));
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
		Giocatore giocatore = statoPartita.getGiocatoreCorrente();
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
		return !statoPartita.getStradeAdiacenti(giocatore.getPosition()).contains(strada);
	}
	
	/**
	 * Metodo avviato all'inizio del turno per valutare se la pecora nera deve essere
	 * spostata.
	 */
	private void checkSpostaPecoraNera() {
		int valoreDado = lanciaDado();

		Regione posNera=statoPartita.getPosPecoraNera();
		List<Strada> stradeConfini=statoPartita.getStradeConfini(posNera);

		for(Strada strada : stradeConfini) {
			if(strada.getnCasella()==valoreDado && !strada.isRecinto()) {
				Regione nuovaRegionePecora=statoPartita.getAltraRegione(posNera, strada);
				try {
					spostaPecoraNera(posNera, nuovaRegionePecora);
				}
				catch (Exception e) {
					e.printStackTrace(System.out);
				}
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
