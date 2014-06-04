package it.polimi.iodice_moro.controller;


import it.polimi.iodice_moro.model.Giocatore;
import it.polimi.iodice_moro.model.Regione;
import it.polimi.iodice_moro.model.StatoPartita;
import it.polimi.iodice_moro.model.Strada;
import it.polimi.iodice_moro.model.TipoMossa;
import it.polimi.iodice_moro.model.TipoTerreno;
import it.polimi.iodice_moro.view.ThreadAnimazionePastore;
import it.polimi.iodice_moro.view.ThreadAnimazionePecoraNera;
import it.polimi.iodice_moro.view.View;
import it.polimi.iodice_moro.view.ThreadAnimazionePecoraBianca;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
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

	private static final Color[] vettColori = {new Color(255,0,0), new Color(0,255,0), new Color(0,0,255), new Color(255,255,0)};
	
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
			throw new Exception("Non puoi spostare pecore da questa regione");
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
			throw new Exception("Non ci sono pecore da spostare!!");
		}
	}
	
	public void spostaPecora(String idRegione) throws Exception{
		Regione regSorg=statoPartita.getRegioneByID(idRegione);
		System.out.println("Sposta pecora controller");
		spostaPecora(statoPartita.getRegioneByID(idRegione));
		aggiornaTurno(TipoMossa.SPOSTA_PECORA);
		String idregD=statoPartita.getAltraRegione(regSorg, statoPartita.getGiocatoreCorrente().getPosition()).getColore();
		System.out.println("Sposta pecora animazione");
		ThreadAnimazionePecoraBianca r = new ThreadAnimazionePecoraBianca(view, idRegione,idregD);
		Thread t = new Thread(r);
		t.start();
		view.modificaQtaPecora(idRegione, regSorg.getNumPecore());
		view.modificaQtaPecora(idregD, statoPartita.getRegioneByID(idregD).getNumPecore());
		checkTurnoGiocatore(TipoMossa.SPOSTA_PECORA);
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
		aggiornaTurno(TipoMossa.SPOSTA_PECORA);
		System.out.println("Sposta pecora animazione");
		ThreadAnimazionePecoraNera r = new ThreadAnimazionePecoraNera(view, regionePecora.getColore(),regAdiacente.getColore());
		Thread t = new Thread(r);
		t.start();
		checkTurnoGiocatore(TipoMossa.SPOSTA_PECORA);
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
		if(tipo.equals(TipoTerreno.SHEEPSBURG)){
			throw new Exception("Non puoi acquistare tessere di sheepsburg");
		}
		boolean acquistoValido=false;
		for(Regione r :statoPartita.getRegioniADStrada(giocatore.getPosition())){
			if(r.getTipo()==tipo){
				acquistoValido=true;
			}
		}
		if(acquistoValido==false){
			throw new Exception("Non puoi acquistate tessere di questo terreno!");
		}
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
		aggiornaTurno(TipoMossa.COMPRA_TESSERA);
		view.modQtaTessera(statoPartita.getRegioneByID(idRegione).getTipo(), statoPartita.getGiocatoreCorrente().getTesserePossedute().get(statoPartita.getRegioneByID(idRegione).getTipo().toString()));
		if(statoPartita.getCostoTessera( statoPartita.getRegioneByID(idRegione).getTipo())<=4){
			view.incPrezzoTessera(statoPartita.getRegioneByID(idRegione).getTipo());
		}
		
		checkTurnoGiocatore(TipoMossa.COMPRA_TESSERA);
	}
	
	/**
	 * Cambia la posizione corrente del giocatore.
	 * Controlla che la posizione di destinazione non sia già occupata da un recinto o da un'altra pedina
	 * @param nuovastrada Nuova posizione.
	 * @throws Exception Se nuova ìposizione è già occupata da un recinto.
	 * @throws Exception Se non ha abbastanza soldi per muoversi.
	 */
	public void spostaPedina (Strada nuovastrada) throws Exception {
		Giocatore giocatore = statoPartita.getGiocatoreCorrente();
		for(Giocatore g: statoPartita.getGiocatori()){
			if(g.getPosition()==nuovastrada){
				throw new Exception("Non puoi spostare qui il tuo pastore, strada occupata!");
			}
			if(g.getPosition2()==nuovastrada){
				throw new Exception("Non puoi spostare qui il tuo pastore, strada occupata!");
			}
		}
		if(nuovastrada.isRecinto()) {
			throw new Exception("Non puoi sposare qui il tuo pastore, strada con recinto!");
		}
		if(pagaSpostamento(nuovastrada, giocatore)) {
			if(giocatore.getSoldi()==0) {
				throw new Exception("Non hai abbastanza soldi per sposatarti!!");
			} else {
				giocatore.decrSoldi();
			}
		}
		//L'aggiunta del recinto viene fatta dal metodo chiamante!!
		this.aggiungiRecinto(giocatore.getPosition());
		giocatore.setPosition(nuovastrada);
	}
	
	public void spostaPedina(String idStrada) throws Exception{
		Strada oldStreet = statoPartita.getGiocatoreCorrente().getPosition();
		spostaPedina(statoPartita.getStradaByID(idStrada));
		aggiornaTurno(TipoMossa.SPOSTA_PASTORE);
		ThreadAnimazionePastore r = new ThreadAnimazionePastore(view, oldStreet.getColore(),idStrada, statoPartita.getGiocatoreCorrente().getColore());
		Thread t = new Thread(r);
		if(!statoPartita.isTurnoFinale()){
			view.addCancelloNormale(oldStreet.getColore());
		} else{
			view.addCancelloFinale(oldStreet.getColore());
		}
		t.start();
		checkTurnoGiocatore(TipoMossa.SPOSTA_PASTORE);
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
	
	
	public void creaGiocatore(String nome){
		statoPartita.addGiocatore(new Giocatore(nome));
	}
	
	/**
	 * Setta la posizione del pastore dato il suo colore che lo identifica univocamente
	 * @param colore
	 * @param idStrada
	 * @throws Exception 
	 */
	public void setStradaGiocatore(Color colore, String idStrada) throws Exception{
		Strada strada = statoPartita.getStradaByID(idStrada);
		for(Giocatore g: statoPartita.getGiocatori()){
			if(g.getColore().equals(colore)){
				for(Giocatore g2: statoPartita.getGiocatori()){
					if(g2.getPosition()==strada|| g2.getPosition2()==strada){
						throw new Exception("non puoi posizionare qui il tuo pastore!!");
					}
				}
				g.setPosition(strada);
			}
		}
		for(Giocatore g: statoPartita.getGiocatori()){
			if(g.getPosition()==null){
				view.setGiocatoreCorrente(g.getColore());
				return;
			}
		}
		//inizializzo la Mappa nella view
		view.initMappa();
		//inizializzo le tessere del giocatore corrente
		for(String t:statoPartita.getGiocatoreCorrente().getTesserePossedute().keySet()){
			if(!t.equals(TipoTerreno.SHEEPSBURG.toString())){
				view.modQtaTessera(TipoTerreno.parseInput(t),statoPartita.getGiocatoreCorrente().getTesserePossedute().get(t));
			}
		}
		view.cambiaGiocatore(statoPartita.getGiocatoreCorrente().getColore());
	}
	
	/**
	 * Metodo nel caso di due giocatori in cui ogni pastore ha due pedine
	 * @param colore
	 * @param idStrada
	 * @param idStrada2
	 */
	public void setStradaGiocatore(Color colore, String idStrada, String idStrada2){
		Strada strada = statoPartita.getStradaByID(idStrada);
		Strada strada2 = statoPartita.getStradaByID(idStrada2);
		for(Giocatore g: statoPartita.getGiocatori()){
			if(g.getColore().equals(colore)){
				g.setPosition(strada, strada2);
			}
			
		}
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
				if(view!=null){
					view.cambiaGiocatore(statoPartita.getGiocatoreCorrente().getColore());
					for(String t:statoPartita.getGiocatoreCorrente().getTesserePossedute().keySet()){
						System.out.println("QUI");
						if(!t.equals(TipoTerreno.SHEEPSBURG.toString())){
							view.modQtaTessera(TipoTerreno.parseInput(t),statoPartita.getGiocatoreCorrente().getTesserePossedute().get(t));
						}
					}
				}
				//checkSpostaPecoraNera();
				
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
		if(view!=null){
			view.disattivaGiocatore();
		}
		
		
	}
	
	
	public void iniziaPartita(){
		List<Regione> listaRegioni = statoPartita.getRegioni();
		//Inizializzazione delle pecore nelle regioni
		for(Regione regione : listaRegioni) {
			if(!regione.getTipo().equals(TipoTerreno.SHEEPSBURG)) {
				regione.setNumPecore(1);
			} else {
				regione.setPecoraNera(true);
			}
		}
		
		//Inizializzazione dei colori associati ai giocatori
		for(Giocatore g: statoPartita.getGiocatori()){
			g.setColore(vettColori[statoPartita.getGiocatori().indexOf(g)]);
		}
		statoPartita.setGiocatoreCorrente(statoPartita.getGiocatori().get(0));
		view.setGiocatoreCorrente(statoPartita.getGiocatoreCorrente().getColore());
		
		//Metto in una lista i possibili valori delle tessere iniziali e li mescolo in modo
		//da assegnare in modo random e univoco la tessera iniziale ai giocatori.
		List<TipoTerreno> tipoTessere = new ArrayList<TipoTerreno>();
		tipoTessere.add(TipoTerreno.BOSCO);
		tipoTessere.add(TipoTerreno.MONTAGNA);
		tipoTessere.add(TipoTerreno.PALUDI);
		tipoTessere.add(TipoTerreno.PIANURA);
		tipoTessere.add(TipoTerreno.SABBIA);
		tipoTessere.add(TipoTerreno.COLTIVAZIONI);
		Collections.shuffle(tipoTessere);
		
		for(Giocatore g: statoPartita.getGiocatori()) {
			g.addTessera(tipoTessere.get(statoPartita.getGiocatori().indexOf(g)));
			//g.getTesserePossedute().put(tipoTessere.get(statoPartita.getGiocatori().indexOf(g)).toString(), 1);
		}
		//inizializziamo le altre tessere dei giocatori a 0
		/*for(Giocatore g: statoPartita.getGiocatori()){
			for(TipoTerreno t:TipoTerreno.values()){
				g.getTesserePossedute().get(TipoTerreno.BOSCO)
			}
		}*/
		
		//TODO
		//Chiama il metodo della view per inizializzare l'interfaccia.
	}
		
		/*
		List<Giocatore> listaGiocatori = statoPartita.getGiocatori();
		for (int i = 0; i<listaGiocatori.size(); i++) {
			listaGiocatori.get(i).setColore(colori[i]);
		}
		*/
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
	
	/**
	 * Ritorna i colori dei giocatori e i loro nomi
	 * @return
	 */
	public Map<Color, String> getGiocatori() {
		Map<Color, String> gioc= new HashMap<Color,String>();
		for(Giocatore g:statoPartita.getGiocatori()){
			gioc.put(g.getColore(), g.getNome());
		}
		return gioc;
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
