package it.polimi.iodice_moro.controller;

import it.polimi.iodice_moro.model.*;

public class Controller {
	
	/**
	 * Istanza del model di StatoPartita.
	 */
	private StatoPartita statopartita;
	
	/**
	 * Riferimento al giocatore gestito dal controller.
	 */
	private Giocatore giocatore;
	
	/**
	 * Costruttore del controller del giocatore.
	 * @param statopartita
	 * @param giocatore
	 */
	public Controller(StatoPartita statopartita, Giocatore giocatore) {
		super();
		this.statopartita = statopartita;
		this.giocatore = giocatore;
	}

	/**
	 * @return Riferimento a istanza di StatoPartita.
	 */
	public StatoPartita getStatoPartita() {
		return statopartita;
	}
	
	/**
	 * @return Istanza del giocatore gestito dal controller.
	 */
	public Giocatore getGiocatore() {
		return giocatore;
	}
	
	/**
	 * Aggiunge un recinto alla strada, se sono disponibili.
	 * @param strada
	 */
	public void aggiungiRecinto(Strada strada) {
		if(statopartita.getNumRecinti()>0) 
			strada.setRecinto(true);
		else 
			statopartita.setTurnoFinale();
	}
	
	/**
	 * Sposta la pecora nell'altra regione adiacente alla posizione attuale
	 * del giocatore.
	 * Utilizza {@link StatoPartita.getAltraRegione} per ottenere l'altra regione adiacente.
	 * @param Regionepecora regione in cui si trova la pecora.
	 * @param Stradagiocatore strada in cui si trova il giocatore.
	 * 
	 */
	public void spostaPecora(Regione regionepecora, Strada stradagiocatore) {
		Regione regadiacente = statopartita.getAltraRegione(regionepecora, stradagiocatore);
		try {
			regionepecora.removePecora();
			regadiacente.addPecora();
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("Non ci sono pecore da spostare");
		}
		
	}
	
	
}
