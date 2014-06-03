package it.polimi.iodice_moro.controller;


import it.polimi.iodice_moro.model.Giocatore;
import it.polimi.iodice_moro.model.Regione;
import it.polimi.iodice_moro.model.StatoPartita;
import it.polimi.iodice_moro.model.Strada;
import it.polimi.iodice_moro.model.TipoMossa;
import it.polimi.iodice_moro.model.TipoTerreno;
import it.polimi.iodice_moro.view.View;
import it.polimi.iodice_moro.view.ThreadAnimazione;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Controller {
	
	/**
	 * Istanza del model di StatoPartita.
	 */
	private StatoPartita statoPartita;	
	
	private static final Logger logger =  Logger.getLogger("it.polimi.iodice_moro.controller");
	
	private View view;
	
	/**
	 * Costruttore del controller del giocatore.
	 * @param statopartita Istanza statopartita.
	 * @param giocatore Istanza del giocatore gestito.
	 */
	public Controller(View view) {
		this.statoPartita = new StatoPartita();
		this.view=view;
	}
	public Controller(StatoPartita statoPartita){
		this.statoPartita= statoPartita;
	}
	
	/**
	 * @return Ritorna riferimento a StatoPartita.
	 */
	public StatoPartita getStatoPartita() {
		return statoPartita;
	}

	
	/**
	 * Aggiunge un recinto alla strada, se sono disponibili.
	 * @param strada Strada sulla quale aggiungere il recinto.
	 */
	private void aggiungiRecinto(Strada strada) {
		/*
		 * Controllo che ci siano recinti disponibili
		 */
		strada.setRecinto(true);
		if(statoPartita.getNumRecinti()>0) {
			/*
			 * Se ci sono recinti disponibili, uso il recinto e decremento il numero di recinti in StatoPartita
			 */
			statoPartita.decNumRecinti();
		}
		if(statoPartita.getNumRecinti()<=0){
			statoPartita.setTurnoFinale();
		}

	}
	
	/**
	 * Sposta la pecora nell'altra regione adiacente alla posizione attuale
	 * del giocatore.
	 * Utilizza {@link StatoPartita#getAltraRegione} per ottenere l'altra regione adiacente.
	 * @param regionepPecora regione da cui spostare la pecora
	 * @throws Exception Se il giocatore non si trova in una strada confinante
	 * alla posizione della pecora da spostare o se non ci sono pecore da spostare.
	 */
	public void spostaPecora(Regione regionePecora) throws Exception {
		Giocatore giocatore = statoPartita.getGiocatoreCorrente();
		/*
		 * Controlliamo che la regione da cui prelevare la pecora sia vicino alla strada dove si trova il giocatore
		 */
		if(!statoPartita.getStradeConfini(regionePecora).contains(giocatore.getPosition())) {
			throw new Exception();
		}
		/*
		 * Ora preleviamo la regione in cui spostare la pecora
		 */
		Regione regAdiacente = statoPartita.getAltraRegione(regionePecora, giocatore.getPosition());
		/*
		 * Controlliamo che ci sia disponibilità di pecore da spostare
		 */
		if(regionePecora.getNumPecore()>0) {
			regionePecora.removePecora();
			regAdiacente.addPecora();
		} else {
			throw new Exception();
		}
		statoPartita.getGiocatoreCorrente().setUltimaMossa(TipoMossa.SPOSTA_PECORA);

	}
	
	public void spostaPecora(String idRegione) throws Exception{
		Regione regSorg=statoPartita.getRegioneByID(idRegione);
		System.out.println("Sposta pecora controller");
		spostaPecora(statoPartita.getRegioneByID(idRegione));
	
		String idregD=statoPartita.getAltraRegione(regSorg, statoPartita.getGiocatoreCorrente().getPosition()).getColore();
		System.out.println("Sposta pecora animazione");
		ThreadAnimazione r = new ThreadAnimazione(view, idRegione,idregD);
		Thread t = new Thread(r);
		t.start();
		view.modificaQtaPecora(idRegione, regSorg.getNumPecore());
		view.modificaQtaPecora(idregD, statoPartita.getRegioneByID(idregD).getNumPecore());
		//view.spostaPecoraBianca(idRegione, statoPartita.getAltraRegione(regSorg, statoPartita.getGiocatoreCorrente().getPosition()).getColore());
	}
	
	
	/**
	 * Metodo che sposta la pecora nera dala regione in cui si trova alla regione adiacente
	 * @param regionePecora Regione dove si trova la pecora.
	 * @param regAdiacente Regione dove deve essere spostata le pecora.
	 * @throws Exception se non ci sono pecore nere da spostare.
	 * @see #checkSpostamentoNera
	 */
	public void spostaPecoraNera(Regione regionePecora, Regione regAdiacente) throws Exception {
		if(regionePecora.isPecoraNera()) {
			regionePecora.removePecoraNera();
			regAdiacente.addPecoraNera();
			statoPartita.setPosPecoraNera(regAdiacente);
		} else {
			throw new Exception();
		}
	}
	
	public void spostaPecoraNera(String idRegPecoraNera) throws Exception{
		Regione regionePecora=statoPartita.getRegioneByID(idRegPecoraNera);
		Regione regAdiacente=statoPartita.getAltraRegione(regionePecora, statoPartita.getGiocatoreCorrente().getPosition());
		spostaPecoraNera(regionePecora,regAdiacente);
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
		if(costoTessera>4) {
			throw new Exception("Le tessere di questo tipo sono finite");
		}
		if (costoTessera > giocatore.getSoldi() || giocatore.getSoldi()==0) {
			throw new Exception("Non abbastanza soldi");
		} else {
			giocatore.decrSoldi(statoPartita.getCostoTessera(tipo));
			giocatore.addTessera(tipo);
			statoPartita.incCostoTessera(tipo);
		}
	}
	
	public void acquistaTessera(String idRegione) throws Exception{
		acquistaTessera(statoPartita.getRegioneByID(idRegione).getTipo());
	}
	
	/**
	 * Cambia la posizione corrente del giocatore.
	 * @param nuovastrada Nuova posizione.
	 * @throws Exception Se nuova ìposizione è già occupata da un recinto.
	 * @throws Exception Se non ha abbastanza soldi per muoversi.
	 */
	public void spostaPedina (Strada nuovastrada) throws Exception {
		Giocatore giocatore = statoPartita.getGiocatoreCorrente();
		if(nuovastrada==giocatore.getPosition()){
			throw new Exception();
		}
		if(nuovastrada.isRecinto()) {
			throw new Exception();
		}
		if(pagaSpostamento(nuovastrada, giocatore)) {
			if(giocatore.getSoldi()==0) {
				throw new Exception();
			} else {
				giocatore.decrSoldi();
			}
		}
		this.aggiungiRecinto(giocatore.getPosition());
		giocatore.setPosition(nuovastrada);
		giocatore.setUltimaMossa(TipoMossa.SPOSTA_PASTORE);
	}
	
	public void spostaPedina(String idStrada) throws Exception{
		Strada oldStreet = statoPartita.getGiocatoreCorrente().getPosition();
		spostaPedina(statoPartita.getStradaByID(idStrada));
		if(!statoPartita.isTurnoFinale()){
			view.addCancelloNormale(oldStreet.getColore());
		} else{
			view.addCancelloFinale(oldStreet.getColore());
		}
	}

	/**
	 * Controlla se il giocatore deve pagare lo spamento. Cioè controlla
	 * se la strada è contenuta nelle strada adiacenti alla posizione corrente 
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
	public void checkSpostaPecoraNera() {
		int valoreDado = lanciaDado();
		/*
		 * Preleviamo la posizione della pecora nera e le strade che circondano la regione in cui si trova la nera
		 */
		Regione posNera=statoPartita.getPosPecoraNera();
		List<Strada> stradeConfini=statoPartita.getStradeConfini(posNera);
		/*
		 * Controllo se nelle strade che circondano la regione in cui si trova la nera c'è una strada che 
		 * come numero di casella corrisponda a quella che ho generato con il metodo lanciaDado()
		 * Se è cosi, allora sposto la pecora nera nella regione speculare
		 * alla strada rispetto alla regione in cui si trova la nera
		 */
		for(Strada strada : stradeConfini) {
			if(strada.getnCasella()==valoreDado && !strada.isRecinto()) {
				Regione nuovaRegionePecora=statoPartita.getAltraRegione(posNera, strada);
				try {
					spostaPecoraNera(posNera, nuovaRegionePecora);
					return;
				} catch (Exception e) {
					logger.log(Level.SEVERE, "Non ci sono pecore da spostare", e);
				}
			}
		}
	}
	
	/**
	 * Metodo per generare un numero casuale compreso tra 0 (escluso) e 6 (incluso), 
	 * servirà per generare la posizione in cui si sposta la pecora nera
	 * @return Ritorna valore compreso tra 0 e 6 (incluso).
	 */
	private int lanciaDado() {
		Random random=new Random();
		return random.nextInt(7);		
	}
	
	/**
	 * Calcola punteggi associati ad ogni giocatore.
	 * @return Ritorna una tabella hash con il punteggio relativo ad ogni giocatore.
	 */
	private Map<Giocatore, Integer> calcolaPunteggio() {
		List<Giocatore>listaGiocatori=statoPartita.getGiocatori();
		Map<Giocatore, Integer> punteggi = new HashMap<Giocatore , Integer>();

		for(Giocatore giocatore : listaGiocatori) {
			Map<String, Integer> tesserePossedute = giocatore.getTesserePossedute();
			Integer punteggio = 0;
			
			for(Map.Entry<String, Integer> elemento : tesserePossedute.entrySet()) {
				String nomeTessera = elemento.getKey();
				Integer numeroTessere = elemento.getValue();
				//Nota: precondizione è che a ogni tipo di tessera corrisponda una regione.
				List<Regione> regioniByTipo = statoPartita.getRegioniByString(TipoTerreno.parseInput(nomeTessera));
				for(Regione regioneTipoX : regioniByTipo) {
					punteggio = punteggio + numeroTessere*regioneTipoX.getNumPecore();
					if(regioneTipoX.isPecoraNera()) {
						punteggio=punteggio+2*numeroTessere;
					}
				}
			}
			punteggi.put(giocatore, punteggio);
		}

		return punteggi;
	}
	
	/**
	 * Crea nuovo giocatore.
	 * @param nome Nome del giocatore.
	 * @param posizione Strada su cui dovrà essere posizionato.
	 */
	public void creaGiocatore(String nome, Strada posizione) {
		Giocatore nuovoGiocatore = new Giocatore(nome, posizione);
		statoPartita.addGiocatore(nuovoGiocatore);
	}
	
	public void creaGiocatore(String nome, String idStrada){
		Strada str= statoPartita.getStradaByID(idStrada);
		creaGiocatore(nome,str);
	}
	
	public void creaGiocatore(String nome, Strada posizione, Strada posizione2) {
		Giocatore nuovoGiocatore = new Giocatore(nome, posizione, posizione2);
		statoPartita.addGiocatore(nuovoGiocatore);
	}
	
	/**
	 * Stabilisce se una mossa può essere effettuata.
	 * @param mossaDaEffettuare Mossa che il giocatore vuole effettuare.
	 * @return Ritorna true in caso positivo, false altrimenti.
	 */
	public boolean mossaPossibile(TipoMossa mossaDaEffettuare) {
		Giocatore giocatoreCorrente=statoPartita.getGiocatoreCorrente();
		TipoMossa ultimaMossa=giocatoreCorrente.getUltimaMossa();
		boolean pastoreSpostato = giocatoreCorrente.isPastoreSpostato();
		if(ultimaMossa.equals(mossaDaEffettuare)&&!mossaDaEffettuare.equals(TipoMossa.SPOSTA_PASTORE)) {
			return false;
		}
		if(!mossaDaEffettuare.equals(TipoMossa.SPOSTA_PASTORE)&&giocatoreCorrente.getNumMosse()==2&&!pastoreSpostato) {
			return false;
		}
		else {
			return true;
		}
	}
	
//___________________________________________________________________________________________________________________
	/*
	 * Controlla se il giocatore corrente può fare ancora mosse (n. mosse): se non può farle
	 * azzera le variabili di turno (fineturno() ) nel giocatore corrente e trova
	 * il prossimo giocatore (modifica il StatoPartita.giocatoreCorrente ).
	 * Inoltre se è il turno finale ed è l'ultimo giocatore mette finePartita().
	 * Ritorna il prossimo giocatore.
	 */
	public Giocatore checkTurnoGiocatore(TipoMossa mossaFatta) {
		/*
		 * Controllo se il giocatore non può fare più mosse
		 */
		if(statoPartita.getGiocatoreCorrente().treMosse()){
			/*
			 * controlliamo se è finita la partita e se il giocatore corrente è l'ultimo
			 */
			int lengthGamers = statoPartita.getGiocatori().size();
			if(
					 //Controllo se è il turno finale
					statoPartita.isTurnoFinale() 
					 //Prelevo la lista dei giocatori
					&& statoPartita.getGiocatori()
					 //Prelevo l'ultimo giocatore della lista
					.get(lengthGamers-1)
					 //Controllo se il giocatore corrente è l'ultimo della lista
					.equals(statoPartita.getGiocatoreCorrente()	)
					){
				finePartita();
				return null;
			}else{
				statoPartita.getGiocatoreCorrente().azzeraTurno();
				/*
				 * Se la partita non è finita bisogna trovare il prossimo giocatore
				 */
				statoPartita.setGiocatoreCorrente(statoPartita.getNextGamer());
			}
		}
		return statoPartita.getGiocatoreCorrente();
	} 

	/**
	 * Aggiorna le variabili del turno del giocatore corrente che ha appena fatto la mossa
	 * @param giocatore Giocatore corrente che ha appena finito la sua mossa
	 * @param mossaFatta Mossa che ha appena effettuato il giocatore
	 */
	public void aggiornaTurno(TipoMossa mossaFatta){
		Giocatore giocatore= statoPartita.getGiocatoreCorrente();
		giocatore.setUltimaMossa(mossaFatta);
		giocatore.incNumMosse();
		if(mossaFatta.equals(TipoMossa.SPOSTA_PASTORE)){
			giocatore.setPastoreSpostato(true);
		}
	}
	
	/**
	 * Metodo inovocato alla fine della partita, dovrà aggiornare la view
	 */
	private void finePartita(){
		//TODO
	}
	
	
	public void iniziaPartita(){
		statoPartita.setGiocatoreCorrente(statoPartita.getGiocatori().get(0));
	}
	
	//AGGIUNTE!!!
	//TODO
	/**
	 * Ritorna l'elenco delle posizioni di tutte le regioni, con i colori loro assegnati
	 * @return
	 */
	public Map<String, Point> getPosRegioni() {
		Map<String,Point> posRegioni = new HashMap<String,Point>();
		for(Regione r: statoPartita.getRegioni()){
			posRegioni.put(r.getColore(),r.getPosizione());
		}
		return posRegioni;
	}
	
	public Map<String, Point> getPosStrade() {
		Map<String,Point> posStrade = new HashMap<String,Point>();
		for(Strada s: statoPartita.getStrade()){
			posStrade.put(s.getColore(),s.getPosizione());
		}
		return posStrade;
	}


	/**
	 * Ritorna gli ID delle due regioni adiacenti alla strada in cui si trova il pastore
	 * @return Ritorna la lista di ID delle regioni adiacenti alla strada in cui si trova il pastore (giocatore corrente)
	 */
	public List<String >getIDRegioniAd(){
		List<String> regAD = new ArrayList<String>();
		List<Regione> reg=statoPartita.getRegioniADStrada(statoPartita.getGiocatoreCorrente().getPosition());
		if(reg.size()>0 && reg.size()<=2){
			regAD.add(reg.get(0).getColore());
			regAD.add(reg.get(1).getColore());
		}
		return regAD;
	}
	public void setView(View view2) {
		this.view=view2;
		
	}
	
/*	public Map<TipoTerreno, Integer> getPrezzoRegAdiacenti(){
		List<Regione> regAD= statoPartita.getRegioniADStrada(statoPartita.getGiocatoreCorrente().getPosition());
		Map<TipoTerreno, Integer> prezzi = new HashMap<TipoTerreno, Integer>();
	//	prezzi.put(regAD.get(0).ge\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\, value)statoPartita.getCostoTessera(regAD.get(0));
		
		
	}*/

	/*	public Map<String, Point> getPosStrade() {
			Map<String,Point> posRegioni = new HashMap<String,Point>();
			for(Strada s: statoPartita.getStrade()){
				posRegioni.put(s.getColore(),s.getPosizione());
			}
			return posRegioni;
		}*/
}
