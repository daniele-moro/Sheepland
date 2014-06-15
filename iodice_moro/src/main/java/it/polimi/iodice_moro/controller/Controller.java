package it.polimi.iodice_moro.controller;


import it.polimi.iodice_moro.exceptions.IllegalClickException;
import it.polimi.iodice_moro.exceptions.NotAllowedMoveException;
import it.polimi.iodice_moro.exceptions.PartitaIniziataException;
import it.polimi.iodice_moro.model.Giocatore;
import it.polimi.iodice_moro.model.Regione;
import it.polimi.iodice_moro.model.StatoPartita;
import it.polimi.iodice_moro.model.Strada;
import it.polimi.iodice_moro.model.TipoMossa;
import it.polimi.iodice_moro.model.TipoTerreno;
import it.polimi.iodice_moro.network.ViewRMI;
import it.polimi.iodice_moro.view.IFView;

import java.awt.Color;
import java.awt.Point;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Controller extends UnicastRemoteObject implements IFController {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 182542694625017227L;

	/**
	 * Istanza del model di StatoPartita.
	 */
	private StatoPartita statoPartita;	
	
	private static final Logger logger =  Logger.getLogger("it.polimi.iodice_moro.controller");

	private static final Color[] vettColori = {new Color(255,0,0), new Color(0,255,0), new Color(0,0,255), new Color(255,255,0)};
	
	private IFView view;
	
	
	/**
	 * Costruttore del controller del giocatore.
	 * @param statopartita Istanza statopartita.
	 * @param giocatore Istanza del giocatore gestito.
	 */
	public Controller(IFView view) throws RemoteException {
		this.statoPartita = new StatoPartita();
		this.view=view;
	}
	public Controller(StatoPartita statoPartita) throws RemoteException {
		this.statoPartita= statoPartita;
	}
	
	/**
	 * @return Ritorna riferimento a StatoPartita.
	 */
	public StatoPartita getStatoPartita() throws RemoteException {
		return statoPartita;
	}

	
	/**
	 * Aggiunge un recinto alla strada, se sono disponibili.
	 * @param strada Strada sulla quale aggiungere il recinto.
	 */
	private synchronized void aggiungiRecinto(Strada strada) throws RemoteException {
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
	public synchronized void spostaPecora(Regione regionePecora) throws NotAllowedMoveException, RemoteException {
		Giocatore giocatore = statoPartita.getGiocatoreCorrente();
		/*
		 * Controlliamo che la regione da cui prelevare la pecora sia vicino alla strada dove si trova il giocatore
		 */
		if(!statoPartita.getStradeConfini(regionePecora).contains(giocatore.getPosition())) {
			throw new NotAllowedMoveException("Non puoi spostare pecore da questa regione");
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
			throw new NotAllowedMoveException("Non ci sono pecore da spostare!!");
		}
	}
	
	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.controller.IFController#spostaPecora(java.lang.String)
	 */
	@Override
	public synchronized void spostaPecora(String idRegione) throws NotAllowedMoveException, RemoteException{
		Regione regSorg=statoPartita.getRegioneByID(idRegione);
		
		if(regSorg == null){
			throw new NotAllowedMoveException("Non hai cliccato su una regione!!");
		}
		
		System.out.println("Sposta pecora controller");
		spostaPecora(statoPartita.getRegioneByID(idRegione));
		aggiornaTurno(TipoMossa.SPOSTA_PECORA);
		String idregD=statoPartita.getAltraRegione(regSorg, statoPartita.getGiocatoreCorrente().getPosition()).getColore();
		System.out.println("Sposta pecora animazione");

		view.spostaPecoraBianca(idRegione, idregD);
		view.modificaQtaPecora(idRegione, regSorg.getNumPecore());
		view.modificaQtaPecora(idregD, statoPartita.getRegioneByID(idregD).getNumPecore());
		
		checkTurnoGiocatore(TipoMossa.SPOSTA_PECORA);
	}
	
	
	/**
	 * Metodo che sposta la pecora nera dala regione in cui si trova alla regione adiacente
	 * @param regionePecora Regione dove si trova la pecora.
	 * @param regAdiacente Regione dove deve essere spostata le pecora.
	 * @throws NotAllowedMoveException se non ci sono pecore nere da spostare.
	 * @see #checkSpostamentoNera
	 */
	public synchronized void spostaPecoraNera(Regione regionePecora, Regione regAdiacente) throws NotAllowedMoveException, RemoteException {
		if(regionePecora.isPecoraNera()) {
			regionePecora.removePecoraNera();
			regAdiacente.addPecoraNera();
			statoPartita.setPosPecoraNera(regAdiacente);
		} else {
			throw new NotAllowedMoveException("Non ci sono pecore nere da spostare");
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
	public synchronized void spostaPecoraNera(String idRegPecoraNera) throws NotAllowedMoveException, RemoteException{
		Regione regionePecora=statoPartita.getRegioneByID(idRegPecoraNera);
		Regione regAdiacente=statoPartita.getAltraRegione(regionePecora, statoPartita.getGiocatoreCorrente().getPosition());
		spostaPecoraNera(regionePecora,regAdiacente);
		aggiornaTurno(TipoMossa.SPOSTA_PECORA);
		System.out.println("Sposta pecora animazione");

		view.spostaPecoraNera(regionePecora.getColore(), regAdiacente.getColore());
		
		checkTurnoGiocatore(TipoMossa.SPOSTA_PECORA);
	}
	
	
	/**
	 * Spostamento lupo.
	 * @param regioneLupo Posizione attuale lupo.
	 * @param regAdiacente Nuova posizione lupo.
	 * @throws NotAllowedMoveException se non ci sono lupi.
	 * @throws RemoteException
	 */
	private synchronized void spostaLupo(Regione regioneLupo, Regione regAdiacente) throws NotAllowedMoveException, RemoteException {
		if(regioneLupo.isLupo()) {
			regioneLupo.removeLupo();
			regAdiacente.addLupo();
			statoPartita.setPosLupo(regAdiacente);
			if(regAdiacente.getNumPecore()>0) {
				regAdiacente.removePecora();
				System.out.println("Il lupo mangia la pecora");
				view.modificaQtaPecora(regAdiacente.getColore(), regAdiacente.getNumPecore());
			}
		} else {
			throw new NotAllowedMoveException("Non ci sono lupi.");
		}
	}

	/**
	 * Giocatore compra la tessera, decrementando i suoi soldi di un valore pari
	 * al costo attuale della tessera che compra.
	 * @param tipo Tipo della tessera che vuole comprare.
	 * @throws Exception Se il costo della tessera è maggiore dei soldi del giocatore.
	 */
	public synchronized void acquistaTessera(TipoTerreno tipo) throws NotAllowedMoveException, RemoteException {
		Giocatore giocatore=statoPartita.getGiocatoreCorrente();
		int costoTessera=statoPartita.getCostoTessera(tipo);
		if(tipo.equals(TipoTerreno.SHEEPSBURG)){
			throw new NotAllowedMoveException("Non puoi acquistare tessere di sheepsburg");
		}
		boolean acquistoValido=false;
		for(Regione r :statoPartita.getRegioniADStrada(giocatore.getPosition())){
			if(r.getTipo()==tipo){
				acquistoValido=true;
			}
		}
		if(acquistoValido==false){
			throw new NotAllowedMoveException("Non puoi acquistate tessere di questo terreno!");
		}
		if(costoTessera>4) {
			throw new NotAllowedMoveException("Le tessere di questo tipo sono finite");
		}
		if (costoTessera > giocatore.getSoldi() || giocatore.getSoldi()==0) {
			throw new NotAllowedMoveException("Non abbastanza soldi");
		} else {
			giocatore.decrSoldi(statoPartita.getCostoTessera(tipo));
			giocatore.addTessera(tipo);
			statoPartita.incCostoTessera(tipo);
		}
	}
	
	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.controller.IFController#acquistaTessera(java.lang.String)
	 */
	@Override
	public synchronized void acquistaTessera(String idRegione) throws IllegalClickException, NotAllowedMoveException, RemoteException{
		Regione reg = statoPartita.getRegioneByID(idRegione);
		if(reg==null){
			throw new IllegalClickException("Non hai cliccato su una regione!!");
		}
		acquistaTessera(reg.getTipo());
		aggiornaTurno(TipoMossa.COMPRA_TESSERA);
		
		view.modQtaTessera(statoPartita.getRegioneByID(idRegione).getTipo(),
				statoPartita.getGiocatoreCorrente().getTesserePossedute().get(statoPartita.getRegioneByID(idRegione).getTipo().toString()),
				statoPartita.getGiocatoreCorrente().getColore());
		
		view.modSoldiGiocatore(statoPartita.getGiocatoreCorrente().getColore(),
				statoPartita.getGiocatoreCorrente().getSoldi());
		
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
	public synchronized void spostaPedina (Strada nuovastrada) throws NotAllowedMoveException, RemoteException {
		Giocatore giocatore = statoPartita.getGiocatoreCorrente();
		for(Giocatore g: statoPartita.getGiocatori()){
			if(g.getPosition()==nuovastrada){
				throw new NotAllowedMoveException("Non puoi spostare qui il tuo pastore, strada occupata!");
			}
			if(g.getPosition2()==nuovastrada){
				throw new NotAllowedMoveException("Non puoi spostare qui il tuo pastore, strada occupata!");
			}
		}
		if(nuovastrada.isRecinto()) {
			throw new NotAllowedMoveException("Non puoi sposare qui il tuo pastore, strada con recinto!");
		}
		if(pagaSpostamento(nuovastrada, giocatore)) {
			if(giocatore.getSoldi()==0) {
				throw new NotAllowedMoveException("Non hai abbastanza soldi per sposatarti!!");
			} else {
				giocatore.decrSoldi();
			}
		}
		
		this.aggiungiRecinto(giocatore.getPosition());
		giocatore.setPosition(nuovastrada);
	}
	
	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.controller.IFController#spostaPedina(java.lang.String)
	 */
	@Override
	public synchronized void spostaPedina(String idStrada) throws IllegalClickException, NotAllowedMoveException, RemoteException{
		Strada oldStreet = statoPartita.getGiocatoreCorrente().getPosition();
		Strada newStreet = statoPartita.getStradaByID(idStrada);
		if(newStreet == null){
			throw new IllegalClickException("Non hai cliccato su una strada!");
		}
		
		//Memorizzo quanti recinti ci sono prima del movimento 
		//per capire dopo se il recinto usato è normale o finale
		int nRecinti = statoPartita.getNumRecinti();
		
		spostaPedina(newStreet);
		aggiornaTurno(TipoMossa.SPOSTA_PASTORE);
		
		if(!statoPartita.isTurnoFinale() || nRecinti>=1){
			view.addCancelloNormale(oldStreet.getColore());
		} else{
			view.addCancelloFinale(oldStreet.getColore());
		}
		
		view.spostaPastore(oldStreet.getColore(),idStrada, statoPartita.getGiocatoreCorrente().getColore());
		
		view.modSoldiGiocatore(statoPartita.getGiocatoreCorrente().getColore(),
				statoPartita.getGiocatoreCorrente().getSoldi());
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
	private boolean pagaSpostamento(Strada strada, Giocatore giocatore) throws RemoteException {
		return !statoPartita.getStradeAdiacenti(giocatore.getPosition()).contains(strada);
	}
	
	/**
	 * Metodo avviato all'inizio del turno per valutare se la pecora nera deve essere
	 * spostata.
	 */
	public synchronized void checkSpostaPecoraNera() throws RemoteException {
		int valoreDado = lanciaDado();
		System.out.println("VALORE DADO: "+valoreDado);
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
				//Controllo che non la pecora non si debba spostare su strade in cui ci sono presenti pastori
				for(Giocatore g : statoPartita.getGiocatori()){
					if(strada==g.getPosition() || strada==g.getPosition2()){
						return;
					}
				}
				Regione nuovaRegionePecora=statoPartita.getAltraRegione(posNera, strada);
				try {
					spostaPecoraNera(posNera, nuovaRegionePecora);
					if(view!=null){
						System.out.println("Spostamento automatico pecora nera!!");
						view.spostaPecoraNera(posNera.getColore(), nuovaRegionePecora.getColore());
					}
					return;
				} catch (NotAllowedMoveException e) {
					logger.log(Level.SEVERE, "Non ci sono pecore da spostare", e);
				}
			}
		}
	}
	
	public synchronized void checkSpostaLupo() throws RemoteException {
		boolean soloRecinti=false;
		boolean puoScavalcare=false;
		int valoreDado = lanciaDado();
		System.out.println("VALORE DADO: "+ valoreDado);
		
		
		
		/*
		 * Preleviamo la posizione del lupo
		 * e delle regioni che circondano la regione in cui si trova.
		 */
		Regione posLupo=statoPartita.getPosLupo();
		List<Strada> stradeConfini=statoPartita.getStradeConfini(posLupo);
		
		/*
		 * Controllo se siamo nel caso che tutte le regioni hanno recinti.
		 */
		int numeroStradeSenzaRecinto=stradeConfini.size();
		for(Strada strada : stradeConfini) {
			if(strada.isRecinto()) {
				numeroStradeSenzaRecinto--;
			}	
		}
		if(numeroStradeSenzaRecinto==0) {
			puoScavalcare=true;
		}
		/*
		 * Controllo se nelle strade che circondano la regione in cui si trova la nera c'è una strada che 
		 * come numero di casella corrisponda a quella che ho generato con il metodo lanciaDado()
		 * Se è cosi, allora sposto la pecora nera nella regione speculare
		 * alla strada rispetto alla regione in cui si trova la nera
		 */
		
		for(Strada strada : stradeConfini) {
			if(strada.getnCasella()==valoreDado) {
				if(!strada.isRecinto()) {
					//Controllo che non la pecora non si debba spostare su strade in cui ci sono presenti pastori
					for(Giocatore g : statoPartita.getGiocatori()){
						if(strada==g.getPosition() || strada==g.getPosition2()){
							return;
						}
					}
				}
				//Se la strada ha un recinto e il lupo non può scavalcare dev'esssere
				//rilanciato il dado.
				else if(strada.isRecinto() && !puoScavalcare) {
					checkSpostaLupo();
					return;
				}

				Regione nuovaRegioneLupo=statoPartita.getAltraRegione(posLupo, strada);
				try {
					spostaLupo(posLupo, nuovaRegioneLupo);
					if(view!=null){
						System.out.println("Spostamento automatico lupo!!");
						//TODO VIEW.spostaLupo();
						view.spostaLupo(posLupo.getColore(), nuovaRegioneLupo.getColore());
					}
					return;
				} catch (NotAllowedMoveException e) {
					logger.log(Level.SEVERE, "Non ci sono pecore da spostare", e);
				}
			}
		}
	}
	
	/**
	 * Metodo per generare un numero casuale compreso tra 0 (escluso) e 6 (incluso), 
	 * servirà per generare la posizione in cui si sposta la pecora nera e il lupo.
	 * @return Ritorna valore compreso tra 0 e 6 (incluso).
	 */
	private int lanciaDado() throws RemoteException {
		Random random=new Random();
		int numero=random.nextInt(6)+1;
		if(view!=null){
			view.visRisDado(numero);
		}
		return numero;	
	}
	
	/**
	 * Calcola punteggi associati ad ogni giocatore.
	 * Il punteggio viene calcolato come i danari che possiede il giocatore, sommati al valore associato ad ogni terreno
	 *  moltiplicato per il numero di tessere di quel terreno possedute.
	 *  Il valore di ogni terreno è calcolato come la somma del numero di pecore di quei terreni, 
	 *  tenendo conto che la pecora nera conta per 2
	 * @return Ritorna una tabella hash con il punteggio relativo ad ogni giocatore.
	 */
	private synchronized Map<Giocatore, Integer> calcolaPunteggio() throws RemoteException {
		List<Giocatore>listaGiocatori=statoPartita.getGiocatori();
		Map<Giocatore, Integer> punteggi = new HashMap<Giocatore , Integer>();

		for(Giocatore giocatore : listaGiocatori) {
			Map<String, Integer> tesserePossedute = giocatore.getTesserePossedute();
			
			//Il punteggio del giocatore parte dalla quantita di danari posseduti
			Integer punteggio = giocatore.getSoldi();
			
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
	public synchronized void creaGiocatore(String nome, Strada posizione) throws RemoteException {
		Giocatore nuovoGiocatore = new Giocatore(nome, posizione);
		statoPartita.addGiocatore(nuovoGiocatore);
	}
	
	
	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.controller.IFController#creaGiocatore(java.lang.String)
	 */
	@Override
	public synchronized Color creaGiocatore(String nome) throws RemoteException, PartitaIniziataException {
		if(statoPartita.getGiocatori().size()>=4) throw new PartitaIniziataException();
		Giocatore nuovoGiocatore = new Giocatore(nome);
		statoPartita.addGiocatore(nuovoGiocatore);
		nuovoGiocatore.setColore(vettColori[statoPartita.getGiocatori().indexOf(nuovoGiocatore)]);
		return nuovoGiocatore.getColore();
	}
	
	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.controller.IFController#setStradaGiocatore(java.awt.Color,java.lang.String)
	 */
	@Override
	public synchronized void setStradaGiocatore(Color colore, String idStrada) throws NotAllowedMoveException, RemoteException{
		
		Strada strada = statoPartita.getStradaByID(idStrada);
		
		//Controllo che la posizione non sia già occupata da un altro pastore
		for(Giocatore g: statoPartita.getGiocatori()){
			if(g.getColore().equals(colore)){
				for(Giocatore g2: statoPartita.getGiocatori()){
					if(g2.getPosition()==strada|| g2.getPosition2()==strada){
						throw new NotAllowedMoveException("non puoi posizionare qui il tuo pastore!!");
					}
				}
			}
		}
		view.spostaPastore("", idStrada, colore);
		//Setto la posizione del giocatore corrente
		statoPartita.getGiocatoreCorrente().setPosition(strada);
		
		//Ora devo trovare il prossimo giocatore
		statoPartita.setGiocatoreCorrente(statoPartita.getNextGamer());
		
		if(statoPartita.getGiocatoreCorrente()!=statoPartita.getGiocatori().get(0)){
			System.out.println("Selezione del nuovo giocatore che deve selezionare la posizione");
			view.setGiocatoreCorrente(statoPartita.getGiocatoreCorrente().getColore());
			
		}else{
			
			//comunico tutte le tessere che ogni giocatore possiede
			/*
			for(Giocatore g : statoPartita.getGiocatori()){
				for(Entry<String,Integer> tessere : g.getTesserePossedute().entrySet()){
					//comunico alla view il tipo di terreno della tessera, 
					//il numero di tessere di quel tipo ed il colore del giocatore a cui è associata la tessera
					if(!tessere.getKey().equals(TipoTerreno.SHEEPSBURG.toString())){
						view.modQtaTessera(TipoTerreno.parseInput(tessere.getKey()), tessere.getValue(), g.getColore());
					}
				}
			}*/
			//finito inserimento dei giocatori
			Map<String,Point> posRegioni = new HashMap<String,Point>();
			for(Regione r: statoPartita.getRegioni()){
				posRegioni.put(r.getColore(),r.getPosizione());
			}
			
			Map<String,Point> posStrade = new HashMap<String,Point>();
			for(Strada s: statoPartita.getStrade()){
				posStrade.put(s.getColore(),s.getPosizione());
			}
			
			Map<Color, String> gioc= new HashMap<Color,String>();
			for(Giocatore g:statoPartita.getGiocatori()){
				gioc.put(g.getColore(), g.getNome());
			}

			view.setPosizioniRegioni(posRegioni);
			view.setPosizioniStrade(posStrade);
			try{
			view.setGiocatori(gioc);
			}catch(RemoteException e){
				logger.log(Level.SEVERE, "Problema di rete", e);
				e.printStackTrace();
			}
			
			//inizializzo la mappa nelal view
			System.out.println("INIT MAPPA SERVER");
			view.initMappa();
			checkSpostaPecoraNera();
			checkSpostaLupo();
			view.cambiaGiocatore(statoPartita.getGiocatoreCorrente().getColore());
			//DA qui inizia la partita vera e propria
		}
	}
	
	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.controller.IFController#setStradaGiocatore(java.awt.Color, java.lang.String, java.lang.String)
	 */
	@Override
	public void setStradaGiocatore(Color colore, String idStrada, String idStrada2) throws RemoteException {
		Strada strada = statoPartita.getStradaByID(idStrada);
		Strada strada2 = statoPartita.getStradaByID(idStrada2);
		for(Giocatore g: statoPartita.getGiocatori()){
			if(g.getColore().equals(colore)){
				g.setPosition(strada, strada2);
			}
			
		}
	}
	
	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.controller.IFController#mossaPossibile(it.polimi.iodice_moro.model.TipoMossa)
	 */
	@Override
	public boolean mossaPossibile(TipoMossa mossaDaEffettuare) throws RemoteException  {
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
	public Giocatore checkTurnoGiocatore(TipoMossa mossaFatta) throws RemoteException {
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
				System.out.println("____________________FINE PARTITA_________________________________________");
				finePartita();
				return null;
			}else{
				statoPartita.getGiocatoreCorrente().azzeraTurno();
				/*
				 * Se la partita non è finita bisogna trovare il prossimo giocatore
				 */
				statoPartita.setGiocatoreCorrente(statoPartita.getNextGamer());
				if(view!=null){
					try{
						view.cambiaGiocatore(statoPartita.getGiocatoreCorrente().getColore());
					}catch (RemoteException e) {
						logger.log(Level.SEVERE, "RemoteException errore", e);
					}

					
					
					/*for(String t:statoPartita.getGiocatoreCorrente().getTesserePossedute().keySet()){
						if(!t.equals(TipoTerreno.SHEEPSBURG.toString())){
							view.modQtaTessera(TipoTerreno.parseInput(t),statoPartita.getGiocatoreCorrente().getTesserePossedute().get(t));
						}
					}*/
					//MODIFICATO:
					//aggiorno le tessere di tutti i giocatori, nel caso online è INUTILE!!!!
					//TODO DA SISTEMARE!!!!!!!!!!!!
					/*for(Giocatore g : statoPartita.getGiocatori()){
						for(Entry<String,Integer> tessere : g.getTesserePossedute().entrySet()){
							//comunico alla view il tipo di terreno della tessera, 
							//il numero di tessere di quel tipo ed il colore del giocatore a cui è associata la tessera
							if(!tessere.getKey().equals(TipoTerreno.SHEEPSBURG.toString())){
								view.modQtaTessera(TipoTerreno.parseInput(tessere.getKey()), tessere.getValue(), g.getColore());
							}
						}
					}*/
					
				}
				//Regione oldNera = statoPartita.getPosPecoraNera();
				checkSpostaPecoraNera();
				checkSpostaLupo();
			}
		}
		return statoPartita.getGiocatoreCorrente();
	} 

	/**
	 * Aggiorna le variabili del turno del giocatore corrente che ha appena fatto la mossa
	 * @param giocatore Giocatore corrente che ha appena finito la sua mossa
	 * @param mossaFatta Mossa che ha appena effettuato il giocatore
	 */
	public void aggiornaTurno(TipoMossa mossaFatta) throws RemoteException {
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
	private void finePartita() throws RemoteException {
		if(view!=null){
			//view.disattivaGiocatore();
			
			Map<Giocatore, Integer> listaPunteggi = calcolaPunteggio();
			Map<Giocatore, Integer> punteggiOrdinati = Controller.sortByValue(listaPunteggi);
			try{
				view.visualizzaPunteggi(punteggiOrdinati);
			}catch(RemoteException e){
				logger.log(Level.SEVERE, "Problemi di rete");
				e.printStackTrace();
			}
		}
		
		
	}
	
	
	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.controller.IFController#iniziaPartita()
	 */
	@Override
	public void iniziaPartita(){
		
		Map<String,Point> posRegioni = new HashMap<String,Point>();
		for(Regione r: statoPartita.getRegioni()){
			posRegioni.put(r.getColore(),r.getPosizione());
		}
		
		Map<String,Point> posStrade = new HashMap<String,Point>();
		for(Strada s: statoPartita.getStrade()){
			posStrade.put(s.getColore(),s.getPosizione());
		}
		try {
			view.setPosizioniRegioni(posRegioni);
			view.setPosizioniStrade(posStrade);
		} catch (RemoteException e1) {
			logger.log(Level.SEVERE, "Problemi di rete");
			e1.printStackTrace();
		}
		
		
		
		//questo metodo deve venire chiamato una sola volta all'inizio della partita,
		//quando tutti i gicatori sono pronti a giocare
		List<Regione> listaRegioni = statoPartita.getRegioni();
		
		//Inizializzazione delle pecore nelle regioni
		for(Regione regione : listaRegioni) {
			if(!regione.getTipo().equals(TipoTerreno.SHEEPSBURG)) {
				regione.setNumPecore(1);
			} else {
				statoPartita.setPosPecoraNera(regione);
				regione.setPecoraNera(true);
				statoPartita.setPosLupo(regione);
				regione.setLupo(true);
			}
		}
		
		//ASSEGNAMENTO DELLE TESSERE CASUALI
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
		
		//Chiama metodi della view a cui passa le regioni, le strade ed i giocatori
		//e poi chiama l'init_mappa sul client
		
		
		for(Giocatore g: statoPartita.getGiocatori()) {
			g.addTessera(tipoTessere.get(statoPartita.getGiocatori().indexOf(g)));
			//comunico alla view qual'è la tessera che gli è toccata
			try {
				view.modQtaTessera(tipoTessere.get(statoPartita.getGiocatori().indexOf(g)), 1, g.getColore());
			} catch (RemoteException e) {
				logger.log(Level.SEVERE, "Problemi di rete", e);
				e.printStackTrace();
			}
		}
		
		//Controllo se ci sono solo 2 giocatori, in questo caso vanno aumentati di 10 i danari
		if(statoPartita.getGiocatori().size()==2){
			for(Giocatore g : statoPartita.getGiocatori()){
				g.initSoldiDueGiocatori();
				//visualizza soldi modificati sul client
			}
		}

		//Set GIOCATORE CORRENTE
		System.out.println("SET GIOCATORE CORRENTE!");
		statoPartita.setGiocatoreCorrente(statoPartita.getGiocatori().get(0));
		try {
			view.setGiocatoreCorrente(statoPartita.getGiocatoreCorrente().getColore());
		} catch (RemoteException e) {
			logger.log(Level.SEVERE, "Problemi di rete", e);
			e.printStackTrace();
		}
	}

	
	//AGGIUNTE!!!
	//TODO
	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.controller.IFController#getPosRegioni()
	 */
	@Override
	public Map<String, Point> getPosRegioni() {
		Map<String,Point> posRegioni = new HashMap<String,Point>();
		for(Regione r: statoPartita.getRegioni()){
			posRegioni.put(r.getColore(),r.getPosizione());
		}
		return posRegioni;
	}
	
	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.controller.IFController#getPosStrade()
	 */
	@Override
	public Map<String, Point> getPosStrade() {
		Map<String,Point> posStrade = new HashMap<String,Point>();
		for(Strada s: statoPartita.getStrade()){
			posStrade.put(s.getColore(),s.getPosizione());
		}
		return posStrade;
	}


	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.controller.IFController#getIDRegioniAd()
	 */
	@Override
	public List<String >getIDRegioniAd(){
		List<String> regAD = new ArrayList<String>();
		List<Regione> reg=statoPartita.getRegioniADStrada(statoPartita.getGiocatoreCorrente().getPosition());
		if(reg.size()>0 && reg.size()<=2){
			regAD.add(reg.get(0).getColore());
			regAD.add(reg.get(1).getColore());
		}
		return regAD;
	}

	
	public void setView(IFView view2) {
		this.view=view2;
	}
	
	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.controller.IFController#getGiocatori()
	 */
	@Override
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
	

	
	public static Map<Giocatore, Integer> sortByValue(Map<Giocatore, Integer> map) {

		List<Map.Entry<Giocatore, Integer>> list = new LinkedList<Map.Entry<Giocatore,Integer>>(map.entrySet());    
		Collections.sort(list, new Comparator<Object>() {     
			@SuppressWarnings({ "rawtypes", "unchecked" })
			public int compare(Object o1, Object o2) {         
				return ((Comparable) ((Map.Entry<Giocatore,Integer>) (o2)).getValue()).compareTo(((Map.Entry<Giocatore,Integer>) (o1)).getValue());
			}});  
		Map<Giocatore, Integer> result = new LinkedHashMap<Giocatore, Integer>();    
		for (Iterator<Entry<Giocatore, Integer>> it = list.iterator(); it.hasNext();) {   

			Entry<Giocatore, Integer> entry = (Map.Entry<Giocatore,Integer>)it.next();   

			result.put(entry.getKey(), entry.getValue());    
		}     
		return result;   
	}
	/*public static void main(String args[]) {
		StatoPartita stato= new StatoPartita();
		Controller cont=new Controller(stato);
		ViewSocket view;
		try {
			view = new ViewSocket(cont);
			cont.setView(view);
			view.riceviMossa();
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Problemi di IO", e);
			e.printStackTrace();
		}
	}*/
	
	//Precondizione: Metoto chiamato solo dal client in RMI.
	public void addView(IFView view, Color coloreGiocatore) throws RemoteException, PartitaIniziataException {
		((ViewRMI)(this.view)).addView(view, coloreGiocatore);
		
	}
	@Override
	public void end() throws RemoteException {
		//Metodo per la chiusura dell'applicazione
		//Comando alla view di terminare l'applicazione
		//Usato nel caso un utente si disconnetta dal gioco
		view.close();
		
	}
	
}
