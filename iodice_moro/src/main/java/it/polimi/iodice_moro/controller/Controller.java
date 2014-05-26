package it.polimi.iodice_moro.controller;


import it.polimi.iodice_moro.model.Giocatore;
import it.polimi.iodice_moro.model.Regione;
import it.polimi.iodice_moro.model.StatoPartita;
import it.polimi.iodice_moro.model.Strada;
import it.polimi.iodice_moro.model.TipoMossa;
import it.polimi.iodice_moro.model.TipoTerreno;

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
	
	public static final Logger logger =  Logger.getLogger("it.polimi.iodice_moro.controller");
	
	
	/**
	 * Costruttore del controller del giocatore.
	 * @param statopartita Istanza statopartita.
	 * @param giocatore Istanza del giocatore gestito.
	 */
	public Controller(StatoPartita statopartita) {
		this.statoPartita = statopartita;
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
		}else{
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
		}
		else throw new Exception();

	}
	
	/**
	 * Metodo che sposta la pecora nera dala regione in cui si trova alla regione adiacente
	 * @param regionePecora Regione dove si trova la pecora.
	 * @param regAdiacente Regione dove deve essere spostata le pecora.
	 * @throws Exception se non ci sono pecore nere da spostare.
	 * @see #checkSpostamentoNera
	 */
	private void spostaPecoraNera(Regione regionePecora, Regione regAdiacente) throws Exception {
		if(regionePecora.isPecoraNera()==true) {
			regionePecora.removePecoraNera();
			regAdiacente.addPecoraNera();
			statoPartita.setPosPecoraNera(regAdiacente);
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
		if(costoTessera>4) {
			throw new Exception("Le tessere di questo tipo sono finite");
		}
		if (costoTessera > giocatore.getSoldi() || giocatore.getSoldi()==0) {
			throw new Exception("Non abbastanza soldi");
		}
		else {
			giocatore.decrSoldi(statoPartita.getCostoTessera(tipo));
			giocatore.addTessera(tipo);
			statoPartita.incCostoTessera(tipo);
		}
	}
	
	/**
	 * Cambia la posizione corrente del giocatore.
	 * @param nuovastrada Nuova posizione.
	 * @throws Exception Se nuova ìposizione è già occupata da un recinto.
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
				}
				catch (Exception e) {
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
	
	/**
	 * Stabilisce se una mossa può essere effettuata.
	 * @param mossaDaEffettuare Mossa che il giocatore vuole effettuare.
	 * @return Ritorna true in caso positivo, false altrimenti.
	 */
	public boolean mossaPossibile(TipoMossa mossaDaEffettuare) {
		Giocatore giocatoreCorrente=statoPartita.getGiocatoreCorrente();
		TipoMossa ultimaMossa=giocatoreCorrente.getUltimaMossa();
		boolean pastoreSpostato = giocatoreCorrente.isPastoreSpostato();
		if(!ultimaMossa.equals(mossaDaEffettuare)&&!mossaDaEffettuare.equals(TipoMossa.SPOSTA_PASTORE)) {
			return false;
		}
		if(!mossaDaEffettuare.equals(TipoMossa.SPOSTA_PASTORE)&&giocatoreCorrente.getNumMosse()==2&&!pastoreSpostato) {
			return false;
		}
		else return true;
	}
	
//___________________________________________________________________________________________________________________
	/*
	 * Cambia l'ultima mossa nel giocatore corrente. Incremente numero mosse giocatore corrente.
	 * Controlla se il giocatore corrente può fare ancora mosse (n. mosse): se non può farle
	 * azzera le variabili di turno (fineturno() ) nel giocatore corrente e trova
	 * il prossimo giocatore (modifica il StatoPartita.giocatoreCorrente ).
	 * Inoltre se è il turno finale ed è l'ultimo giocatore mette finePartita().
	 * Ritorna il prossimo giocatore.
	 */
	public Giocatore checkTurnoGiocatore(TipoMossa mossaFatta) {
		/*
		 * Aggiornamento variabili di turno
		 */
		aggiornaTurno(mossaFatta);
		/*
		 * Controllo se il giocatore può ancora fare mossa
		 */
		if(!otherMoves(statoPartita.getGiocatoreCorrente())){
			/*
			 * controlliamo se è finita la partita e se il giocatore corrente è l'ultimo
			 */
			if(isPartitaFinita()){
				finePartita();
				return null;
			}else{
				/*
				 * Se la partita non è finita bisogna trovare il prossimo giocatore
				 */
				statoPartita.setGiocatoreCorrente(nextGamer());
			}
		}
		return statoPartita.getGiocatoreCorrente();
	} 
	
	private boolean isPartitaFinita(){
		return statoPartita.isTurnoFinale() && isLast(statoPartita.getGiocatoreCorrente());
	}
	
	/**MODEL??
	 * Metodo per calcolare qual'è il giocatore del prossimo turno
	 * @return Prossimo giocatore
	 */
	private Giocatore nextGamer() {
		int indice=statoPartita.getIndex(statoPartita.getGiocatoreCorrente());
		/*
		 * Incremento l'indice a cui si trova il giocatore facendone il modulo rispetto alla lunghezza della
		 * lista di giocatori
		 */
		indice=(indice+1)%statoPartita.getGiocatori().size();
		return statoPartita.getGiocatori().get(indice);
	}
	
	/**MODEL??
	 * Metodo per verificare se il giocatore corrente è l'ultimo giocatore nell'ordine di gioco
	 * @param giocatoreCorrente
	 * @return
	 */
	private boolean isLast(Giocatore giocatoreCorrente) {
		return statoPartita.getGiocatori().get(statoPartita.getGiocatori().size()-1).equals(giocatoreCorrente);
	}


	/**
	 * Metodo che controlla se il giocatore può ancora effettuare mosse
	 * @param giocatore Giocatore di cui controllare se è possibile effettuare mosse
	 * @return Valore che indica se può o meno effettuare ancora mosse
	 */
	private boolean otherMoves(Giocatore giocatore) {
		if(giocatore.getNumMosse()>=3){
			return false;
		} else{
			return true;
		}
	}

	/**
	 * Aggiorna le variabili del turno del giocatore corrente che ha appena fatto la mossa
	 * @param giocatore Giocatore corrente che ha appena finito la sua mossa
	 * @param mossaFatta Mossa che ha appena effettuato il giocatore
	 */
	private void aggiornaTurno(TipoMossa mossaFatta){
		Giocatore giocatore= statoPartita.getGiocatoreCorrente();
		giocatore.setUltimaMossa(mossaFatta);
		giocatore.incNumMosse();
		if(mossaFatta.equals(TipoMossa.SPOSTA_PASTORE)){
			giocatore.setPastoreSpostato(true);
		}
	}
	
	/**
	 * Metodo inovocato alla fine della partita, dovrà aggioranre la view
	 */
	private void finePartita(){
		//TODO
	}
	
	
	
//______________________________________________________________________________________________________________
	/*
	 * SERVE??? o si gestisce tutto da view, la view quindi chiamera il metodo corretto per eseguire la mossa
	 * e una volta eseguita eseguira il checkTurnoGiocatore,inoltre prima di fare la mossa esegue il mossaPOssibile,
	 * per controllare se la mossa che deve essere fatta è possibile
	 * inoltre la view si occuperà anche di avviare ad inizio turno il metodo checkSpostaNera
	 *
	/**
	 * Metodo per gestire le mosse da fare nel turno del giocatore corrente
	 * @param mossa Mossa che deve essere compiuta
	 * @param obj Oggetto che rappresenta a secoda della mossa che deve fare, un TipoTerreno, una Strada o una Regione
	 * @throws Exception
	 *
	public void gestisciTurno(TipoMossa mossa, Object obj) throws Exception{
		if(!mossaPossibile(mossa)){return;}
		switch(mossa){
		
		case COMPRA_TESSERA:
			if(obj instanceof TipoTerreno){
				this.acquistaTessera((TipoTerreno)obj);
			} else{
				throw new ClassCastException("Oggetto non è un istanza di TipoTerreno");
			}
			break;
		case SPOSTA_PASTORE:
			if(obj instanceof Strada){
				this.spostaPedina((Strada)obj);
			} else{
				throw new ClassCastException("Oggetto non è un istanza di Strada");
			}
			break;
		case SPOSTA_PECORA:
			if(obj instanceof Regione){
				this.spostaPecora((Regione)obj);
			} else{
				throw new ClassCastException("Oggetto non è un istanza di Regione");
			}
			break;
		case NO_MOSSA:
		default:
			break;
		}
		this.checkTurnoGiocatore(mossa);
	}*/
}
