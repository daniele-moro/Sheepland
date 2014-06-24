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
	
	private static final Logger LOGGER =  Logger.getLogger("it.polimi.iodice_moro.controller");

	/**
	 * Costante per l'assegnamento dei colori ai giocatori
	 */
	private static final Color[] VETTCOLORI = {new Color(255,0,0), new Color(0,255,0), new Color(0,0,255), new Color(255,255,0)};
	
	/**
	 * Riferimento alla view collegata al controller
	 */
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
	
	/**
	 * Costruttore del controller.
	 * @param statoPartita Istanza dello Stato Partita.
	 * @throws RemoteException Se ci sono problemi di rete.
	 */
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
	private synchronized void spostaPecora(Regione regionePecora) throws NotAllowedMoveException, RemoteException {
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
	public synchronized void spostaPecora(String idRegione) throws NotAllowedMoveException, RemoteException, IllegalClickException{
		Regione regSorg=statoPartita.getRegioneByID(idRegione);
		
		if(regSorg == null){
			throw new IllegalClickException("Non hai cliccato su una regione!!");
		}
		
		System.out.println("Sposta pecora controller");
		spostaPecora(statoPartita.getRegioneByID(idRegione));
		aggiornaTurno(TipoMossa.SPOSTA_PECORA);
		String idregD=statoPartita.getAltraRegione(regSorg, statoPartita.getGiocatoreCorrente().getPosition()).getColore();
		System.out.println("Sposta pecora animazione");

		view.spostaPecoraBianca(idRegione, idregD);
		view.modificaQtaPecora(idRegione, regSorg.getNumPecore()," ");
		view.modificaQtaPecora(idregD, statoPartita.getRegioneByID(idregD).getNumPecore()," ");
		
		checkTurnoGiocatore(TipoMossa.SPOSTA_PECORA);
	}
	
	public synchronized void accoppiamento1(String idRegione) throws NotAllowedMoveException ,RemoteException, IllegalClickException{
		Regione reg = statoPartita.getRegioneByID(idRegione);
		if(reg==null){
			throw new IllegalClickException("Non hai cliccato su una regione!!");
		}
		int numPecore=reg.getNumPecore();
		System.out.println("accoppiamento controller");
		accoppiamento1(reg);
		aggiornaTurno(TipoMossa.ACCOPPIAMENTO1);
		if(numPecore>reg.getNumPecore()){
			view.modificaQtaPecora(idRegione, reg.getNumPecore(), "Accoppiamento avvenuto!!");
		}
		checkTurnoGiocatore(TipoMossa.ACCOPPIAMENTO1);
		
	}
	
	/**
	 * Metodo per effettuare la mossa accoppiamento 1.
	 * @param regione Regione su cui effettuare il controllo.
	 * @throws NotAllowedMoveException Se la mossa non può essere effettuata nella regione.
	 * @throws RemoteException In caso di problemi di rete.
	 * @see IFController#accoppiamento1(String)
	 */
	private synchronized void accoppiamento1(Regione regione) throws NotAllowedMoveException,RemoteException{
		Giocatore giocatore = statoPartita.getGiocatoreCorrente();
		//Controlliamo che la regione su cui fare l'accoppiamento sia vicino alla strada dove si trova il giocatore
		if(!statoPartita.getStradeConfini(regione).contains(giocatore.getPosition())) {
			throw new NotAllowedMoveException("Non puoi fare questa mossa in questa regione");
		}
		
		//Controllo che ci siano almeno due pecore nella regione selezionata
		if(regione.getNumPecore()<2){
			throw new NotAllowedMoveException("Non ci sono almeno due pecore su questa regione!!");
		}
		//Lancio il dado
		int num = lanciaDado();
		//Controllo che il numero generato dal dado sia lo stesso della casella dove si trova il pastore
		if(num==giocatore.getPosition().getnCasella()){
			//aumento di uno il numero di pecore nella regione
			regione.addPecora();
		}
	}
	
	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.controller.IFController#sparatoria1 (java.lang.String)
	 */
	public synchronized void sparatoria1(String idRegione) throws NotAllowedMoveException, RemoteException, IllegalClickException{
		Regione reg = statoPartita.getRegioneByID(idRegione);
		if(reg==null){
			throw new IllegalClickException("Non hai cliccato su una regione!!");
		}
		int numPecore=reg.getNumPecore();
		System.out.println("sparatoria1 controller");
		sparatoria1(reg);
		aggiornaTurno(TipoMossa.SPARATORIA1);
		if(numPecore>reg.getNumPecore()){
			view.modificaQtaPecora(idRegione, reg.getNumPecore(), "Pecora uccisa!");
		}
		checkTurnoGiocatore(TipoMossa.SPARATORIA1);
	}
	
	/**
	 * Metodo per effettuare la mossa sparatoria 1.
	 * @param regione Regione su cui effettuare la mossa.
	 * @throws NotAllowedMoveException Se non può essere effettuata la mossa nella regione.
	 * @throws RemoteException In caso di problemi di rete.
	 * @see IFController#sparatoria1(String)
	 */
	private synchronized void sparatoria1(Regione regione) throws NotAllowedMoveException, RemoteException{
		Giocatore giocatore = statoPartita.getGiocatoreCorrente();
		//Controlliamo che la regione su cui fare la sparatoria sia vicino alla strada dove si trova il giocatore
		if(!statoPartita.getStradeConfini(regione).contains(giocatore.getPosition())) {
			throw new NotAllowedMoveException("Non puoi effettuare una sparatoria su questa regione");
		}
		
		//controllo che nella regione selezionata ci sia almeno una pecora
		if(regione.getNumPecore()<1){
			throw new NotAllowedMoveException("Non c'è almeno una pecora in questa region!!");
		}
		
		//lancio il dado
		int num = lanciaDado();
		//Controllo che il numero generato sia uguale al numero della casella su cui è posizionato il pastore
		if(num==statoPartita.getGiocatoreCorrente().getPosition().getnCasella()){
			//Decremento il numero di pecore di una unità
			regione.removePecora();
		}
	}
	
	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.controller.IFController#sparatoria2 (java.lang.String)
	 */
	public synchronized void sparatoria2(String idRegione) throws NotAllowedMoveException, RemoteException, IllegalClickException{
		Regione reg = statoPartita.getRegioneByID(idRegione);
		if(reg==null){
			throw new IllegalClickException("Non hai cliccato su una regione!!");
		}
		System.out.println("sparatoria2 controller");
		int numPecore = reg.getNumPecore();
		sparatoria2(reg);
		aggiornaTurno(TipoMossa.SPARATORIA2);
		if(numPecore>reg.getNumPecore()){
		view.modificaQtaPecora(idRegione, reg.getNumPecore(),"Sparatoria 2 avvenuta!!");
		}
		for(Giocatore giocatore : statoPartita.getGiocatori()) {
			view.modSoldiGiocatore(giocatore.getColore(), giocatore.getSoldi());
		}
		checkTurnoGiocatore(TipoMossa.SPARATORIA2);
	}
	
	/**
	 * Metodo per effettuare la mossa sparatoria 2.
	 * @param reg Regione su cui effettuare la mossa.
	 * @throws RemoteException Se ci sono problemi di rete.
	 * @throws NotAllowedMoveException Se la mossa non può esserre effettuata sulla regione.
	 * @see IFController#sparatoria2(String)
	 */
	private void sparatoria2(Regione reg) throws RemoteException, NotAllowedMoveException {
		Giocatore giocatoreCorrente = statoPartita.getGiocatoreCorrente();
		List<Strada> stradeAdiacenti = statoPartita.getStradeConfini(reg);
		int numeroGiocatoriSuReg = 0;
		for(Giocatore giocatore : statoPartita.getGiocatori()) {
			if(!(giocatore.equals(giocatoreCorrente)) 
					&&stradeAdiacenti.contains(giocatore.getPosition())||stradeAdiacenti.contains(giocatore.getPosition2())) {
				numeroGiocatoriSuReg++;
			}	
		}
		if((giocatoreCorrente.getSoldi()-numeroGiocatoriSuReg*2)<0) {
			throw new NotAllowedMoveException("Giocatore non ha abbastanza soldi");
		}
		
		int numPecore = reg.getNumPecore();
		sparatoria1(reg);
		if(numPecore>reg.getNumPecore()) {
			int lancioDado;
			for(Giocatore giocatore : statoPartita.getGiocatori()) {
				if((stradeAdiacenti.contains(giocatore.getPosition())||stradeAdiacenti.contains(giocatore.getPosition2()))
						&&!(giocatore.equals(giocatoreCorrente))) {
					lancioDado=lanciaDado();
					if(lancioDado>5) {
						giocatore.incrSoldi(2);
						giocatoreCorrente.decrSoldi(2);
					}
				}
			}
		}
	}
	
	
	/**
	 * Metodo che sposta la pecora nera dalla regione in cui si trova alla regione adiacente
	 * @param regionePecora Regione dove si trova la pecora.
	 * @param regAdiacente Regione dove deve essere spostata le pecora.
	 * @throws NotAllowedMoveException se non ci sono pecore nere da spostare.
	 * @see #checkSpostamentoNera
	 */
	private synchronized void spostaPecoraNera(Regione regionePecora, Regione regAdiacente) throws NotAllowedMoveException, RemoteException {
		if(regionePecora.isPecoraNera()) {
			regionePecora.removePecoraNera();
			regAdiacente.addPecoraNera();
			statoPartita.setPosPecoraNera(regAdiacente);
		} else {
			throw new NotAllowedMoveException("Non ci sono pecore nere da spostare");
		}
	}
	
	/**
	 * Metodo che sposta la pecora nera dala regione in cui si trova alla regione adiacente
	 * @param regionePecora Regione dove si trova la pecora.
	 * @param regAdiacente Regione dove deve essere spostata le pecora.
	 * @throws NotAllowedMoveException se non ci sono pecore nere da spostare.
	 * @see #checkSpostamentoNera
	 */
	@Override
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
	 * @see {@link Controller#spostaLupo(Regione, Regione)}
	 */
	private synchronized void spostaLupo(Regione regioneLupo, Regione regAdiacente) throws NotAllowedMoveException, RemoteException {
		if(regioneLupo.isLupo()) {
			regioneLupo.removeLupo();
			regAdiacente.addLupo();
			statoPartita.setPosLupo(regAdiacente);
			if(regAdiacente.getNumPecore()>0) {
				regAdiacente.removePecora();
				System.out.println("Il lupo mangia la pecora");
				view.modificaQtaPecora(regAdiacente.getColore(), regAdiacente.getNumPecore(),"Il Lupo ha mangiato!");
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
	 * @see Controller#acquistaTessera(String)
	 */
	private synchronized void acquistaTessera(TipoTerreno tipo) throws NotAllowedMoveException, RemoteException {
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
		if(!acquistoValido){
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
	 * @throws Exception Se nuova posizione è già occupata da un recinto.
	 * @throws Exception Se non ha abbastanza soldi per muoversi.
	 * @see Controller#spostaPedina(String)
	 */
	private synchronized void spostaPedina (Strada nuovastrada) throws NotAllowedMoveException, RemoteException {
		Giocatore giocatore = statoPartita.getGiocatoreCorrente();
		isStradaOccupata(nuovastrada);
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
		System.out.println("PERCORSO DA FARE");
		
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
		List<Strada> stradeAttraversate = statoPartita.dijkstraTraStrade(oldStreet, newStreet);
		List<String> idStradeAttraversate = new ArrayList<String>();
		for(Strada s : stradeAttraversate){
			idStradeAttraversate.add(s.getColore());
		}
		view.spostaPastore(idStradeAttraversate, statoPartita.getGiocatoreCorrente().getColore());
		
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
		 * come numero di casella corrisponda a quella che ho generato con il lanciaDado()
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
					LOGGER.log(Level.SEVERE, "Non ci sono pecore da spostare", e);
				}
			}
		}
	}
	
	//TODO DA REIMPLEMENTARE
	/**
	 * Controlla se dev'essere effettuato il movimento del lupo. Viene lanciato un dado e se
	 * il risultato è lo stesso di una delle caselle adiacenti il lupo viene spostato in quella
	 * direzione. Se caselle sono tutte occupate da recinti, il lupo può scavalcarli, altrimenti
	 * se la direzione verso la quale spostarsi è occupata da un recinto dovrà essere lanciato
	 * il dado.
	 * @throws RemoteException
	 */
	public synchronized void checkSpostaLupo() throws RemoteException {
		//Variabile locale per sapere se tutte le regioni sono recintate
		boolean puoScavalcare=true;
		//Numero di tentativi di spostamento effettuati
		int tentativi=0;
		//Variabile per indicare se il lupo è stato spostato
		boolean lupoSpostato=false;
		//Variabile per indicare se il dado può essere lanciato due volte
		boolean secondaChance=true;
		
		/*
		 * Preleviamo la posizione del lupo
		 * e delle regioni che circondano la regione in cui si trova.
		 */
		Regione posLupo=statoPartita.getPosLupo();
		//Preleviamo le strade confinanti con la regione dove si trova il lupo
		List<Strada> stradeConfini=statoPartita.getStradeConfini(posLupo);
		
		/*
		 * Controllo se siamo nel caso che tutte le regioni hanno recinti.
		 */
		for(Strada strada : stradeConfini) {
			if(!strada.isRecinto()) {
				puoScavalcare=false;
			}	
		}
		
		/*
		 * Controllo se nelle strade che circondano la regione in cui si trova il lupo c'è una strada che 
		 * come numero di casella corrisponda a quella che ho generato con il metodo lanciaDado()
		 * Se è cosi, allora sposto il lupo nella regione speculare
		 * alla strada rispetto alla regione in cui si trova il lupo
		 * Ci sono due possibilità che il lupo possa spostarsi, se tutte le strade sono recintate salta,
		 * se la strada su cui dovrebbe passare è recintata viene rilanciato il dado
		 */
		do{
			//Lancio il dado per sapere dove dovrà spostarsi il lupo
			int valoreDado = lanciaDado();
			System.out.println("Lancio dado LUPO: " +valoreDado);
			for(Strada strada : stradeConfini){
				if(strada.getnCasella()==valoreDado){
					secondaChance=true;
					if(!strada.isRecinto() || puoScavalcare){
						Regione regioneDavanti= statoPartita.getAltraRegione(posLupo, strada);
						try {
							System.out.println("SPOSTA LUPO");
							spostaLupo(posLupo,regioneDavanti);
						} catch (NotAllowedMoveException e) {
							LOGGER.log(Level.SEVERE, "Mossa non consentita!", e);
						}
						lupoSpostato=true;
					}
				}
			}
			tentativi++;
		}while(tentativi<2 && !lupoSpostato && secondaChance);
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
		if(statoPartita.getGiocatori().size()>=4){
			throw new PartitaIniziataException();
		}
		Giocatore nuovoGiocatore = new Giocatore(nome);
		statoPartita.addGiocatore(nuovoGiocatore);
		nuovoGiocatore.setColore(VETTCOLORI[statoPartita.getGiocatori().indexOf(nuovoGiocatore)]);
		return nuovoGiocatore.getColore();
	}
	
	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.controller.IFController#setStradaGiocatore(java.awt.Color,java.lang.String)
	 */
	@Override
	public synchronized void setStradaGiocatore(Color colore, String idStrada) throws NotAllowedMoveException, RemoteException{
		
		Strada strada = statoPartita.getStradaByID(idStrada);
		
		//Controllo che la posizione non sia già occupata da un altro pastore
		isStradaOccupata(strada);
		//Inserisco la posizone iniziale del giocatore, controllando che sia nulla la posizione
		if(statoPartita.getGiocatoreCorrente().getPosition()==null){
			statoPartita.getGiocatoreCorrente().setPosition(strada);
			List<String> listaStr = new ArrayList<String>();
			listaStr.add(idStrada);
			view.spostaPastore(listaStr, colore);
		}else{
			//Se non è nulla allora devo controllare se sono nel caso di due gicatori
			if(statoPartita.getGiocatori().size()==2){
				//se ha già selezionato la prima posizione allora setto la seconda posizione
				statoPartita.getGiocatoreCorrente().setPosition2(strada);
				view.posiziona2Pastore(idStrada, colore);
			} else{
				throw new NotAllowedMoveException("non puoi posizionare due volte il pastore!!");
			}
		}
		
		
		//Ora devo trovare il prossimo giocatore, nel caso in cui ci siano più di due giocatori,
		//oppure se ci sono due giocatori e sono già state selezionate entrambe le posizioni dei pastori
		if(statoPartita.getGiocatori().size()!=2 ||
				(statoPartita.getGiocatori().size()==2 && statoPartita.getGiocatoreCorrente().getPosition2()!=null)){
			System.out.println("Cambio giocatore");
			statoPartita.setGiocatoreCorrente(statoPartita.getNextGamer());
		}
		
		//Controllo se la fase di selezione delle posizioni iniziali sia finita
		if(statoPartita.getGiocatori().indexOf(statoPartita.getGiocatoreCorrente())!=0
				|| (statoPartita.getGiocatori().size()==2 && statoPartita.getGiocatori().indexOf(statoPartita.getGiocatoreCorrente())==0 && statoPartita.getGiocatoreCorrente().getPosition2()==null)){
			System.out.println("Selezione del nuovo giocatore che deve selezionare la posizione");
			view.setGiocatoreCorrente(statoPartita.getGiocatoreCorrente().getColore());
			
		}else{
			//Finito inserimento delle posizioni dei giocatori
			/*Map<String,Point> posRegioni = new HashMap<String,Point>();
			for(Regione r: statoPartita.getRegioni()){
				posRegioni.put(r.getColore(),r.getPosizione());
			}
			
			Map<String,Point> posStrade = new HashMap<String,Point>();
			for(Strada s: statoPartita.getStrade()){
				posStrade.put(s.getColore(),s.getPosizione());
			}*/
			
			Map<Color, String> gioc= new HashMap<Color,String>();
			for(Giocatore g:statoPartita.getGiocatori()){
				gioc.put(g.getColore(), g.getNome());
			}

			//Setto posizioni delle regioni, delle strade e comunico i giocatori che partecipano alla partita
			//view.setPosizioniRegioni(posRegioni);
			//view.setPosizioniStrade(posStrade);
			view.setGiocatori(gioc);
			
			//inizializzo la mappa nella view
			view.initMappa();
			
			for(Giocatore g : statoPartita.getGiocatori()){
				//visualizza soldi modificati sul client
				if(view!=null){
					try {
						view.modSoldiGiocatore(g.getColore(), g.getSoldi());
					} catch (RemoteException e) {
						LOGGER.log(Level.SEVERE, "Problemi di rete", e);
					}
				}
			}
			
			checkSpostaPecoraNera();
			checkSpostaLupo();
			view.cambiaGiocatore(statoPartita.getGiocatoreCorrente().getColore());
			if(statoPartita.getGiocatori().size()==2){
				view.selezPast(statoPartita.getGiocatoreCorrente().getColore());
			}
			//DA qui inizia la partita vera e propria
		}
	}
	/** 
	 * Controlla se la strada è già occupata da un pastore.
	 * @param strada Strada su cui effettuare il controllo.
	 * @throws NotAllowedMoveException Se la trada è occupata.
	 */
	private void isStradaOccupata(Strada strada) throws NotAllowedMoveException {
		for(Giocatore g2: statoPartita.getGiocatori()){
			if(g2.getPosition()==strada|| g2.getPosition2()==strada){
				throw new NotAllowedMoveException("non puoi posizionare qui il tuo pastore, strada occupata!!");
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
		
		return true;
	}
	

	/**
	 * Controlla se il giocatore corrente può fare ancora mosse (n. mosse): se non può farle
	 * azzera le variabili di turno (fineturno() ) nel giocatore corrente e trova
	 * il prossimo giocatore (modifica il giocatore corrente in stato partita ).
	 * Inoltre se è il turno finale ed è l'ultimo giocatore mette finePartita().
	 * Ritorna il prossimo giocatore.
	 * @param mossaFatta Mossa appena effettuata.
	 * @return Prossimo giocatore.
	 * @throws RemoteException In caso di problemi di rete.
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
					checkSpostaPecoraNera();
					checkSpostaLupo();
					try{
						view.cambiaGiocatore(statoPartita.getGiocatoreCorrente().getColore());
					}catch (RemoteException e) {
						LOGGER.log(Level.SEVERE, "RemoteException errore", e);
					}
					if(statoPartita.getGiocatori().size()==2){
						//siamo nel caso di due giocatori, vuol dire che il giocatore deve selezionare quale pastore usare
						view.selezPast(statoPartita.getGiocatoreCorrente().getColore());
					}
				}
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
	 * @throws RemoteException In caso di problemi di rete.
	 */
	private void finePartita() throws RemoteException {
		if(view!=null){
			Map<Giocatore, Integer> listaPunteggi = calcolaPunteggio();
			Map<Giocatore, Integer> punteggiOrdinati = Controller.sortByValue(listaPunteggi);
			try{
				view.visualizzaPunteggi(punteggiOrdinati);
			}catch(RemoteException e){
				LOGGER.log(Level.SEVERE, "Problemi di rete");
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
			LOGGER.log(Level.SEVERE, "Problemi di rete");
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
			//comunico alla view qual è la tessera che gli è toccata
			try {
				view.modQtaTessera(tipoTessere.get(statoPartita.getGiocatori().indexOf(g)), 1, g.getColore());
			} catch (RemoteException e) {
				LOGGER.log(Level.SEVERE, "Problemi di rete", e);
			}
		}
		
		//Controllo se ci sono solo 2 giocatori, in questo caso vanno aumentati di 10 i danari
		if(statoPartita.getGiocatori().size()==2){
			for(Giocatore g : statoPartita.getGiocatori()){
				g.initSoldiDueGiocatori();
			}
		}

		//Set GIOCATORE CORRENTE
		System.out.println("SET GIOCATORE CORRENTE!");
		statoPartita.setGiocatoreCorrente(statoPartita.getGiocatori().get(0));
		try {
			view.setGiocatoreCorrente(statoPartita.getGiocatoreCorrente().getColore());
		} catch (RemoteException e) {
			LOGGER.log(Level.SEVERE, "Problemi di rete", e);
		}
	}

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
		if(!reg.isEmpty() && reg.size()<=2){
			regAD.add(reg.get(0).getColore());
			regAD.add(reg.get(1).getColore());
		}
		return regAD;
	}

	/**
	 * Imposta la view da utilizzare.
	 */
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
	
	/**
	 * Metodo statico per ordinare una Map<Giocatore, Integer> secondo il valore.
	 * @param map
	 * @return
	 */
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
	
	/**
	 * Precondizione: metodo chiamato solo dal client RMI. Serve per aggiungere una nuova view
	 * alla ViewRMI.
	 */
	public void addView(IFView view, Color coloreGiocatore) throws RemoteException, PartitaIniziataException {
		((ViewRMI)(this.view)).addView(view, coloreGiocatore);
		
	}
	
	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.controller.IFController#end
	 */
	@Override
	public void end() throws RemoteException {
		//Metodo per la chiusura dell'applicazione
		//Comando alla view di terminare l'applicazione
		//Usato nel caso un utente si disconnetta dal gioco
		view.close();
		
	}

	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.controller.IFController#cambiaPastore(java.lang.String)
	 */
	@Override
	public void cambiaPastore(String idStrada) throws RemoteException, IllegalClickException {
		//metodo usato per selezionare il pastore che si vuole usare nel caso di due giocatori
		Strada strada= statoPartita.getStradaByID(idStrada);
		if(strada==null){
			throw new IllegalClickException("Non hai cliccato su una strada!!");
		}
		Giocatore giocCorr= statoPartita.getGiocatoreCorrente();
		//Avrò come prima position sempre la posizione che seleziona l'utente, cioè il pastore che vuole usare l'utente
		if(giocCorr.getPosition2()==strada){
			giocCorr.setPosition2(giocCorr.getPosition());
			giocCorr.setPosition(strada);
			//cambio il pastore utilizzato anche nella view
			view.usaPast2(statoPartita.getGiocatoreCorrente().getColore());
		}else{
			if(giocCorr.getPosition()!=strada){
				//se nessuna delle due posizioni è la strada cliccata allora c'è un errore
				throw new IllegalClickException("Non hai cliccato su una posizione dei tuoi pastori!!");
			}
		}
		view.cambiaGiocatore(giocCorr.getColore());
	}
	
}
