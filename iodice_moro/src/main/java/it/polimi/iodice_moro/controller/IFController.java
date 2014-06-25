package it.polimi.iodice_moro.controller;

import it.polimi.iodice_moro.exceptions.IllegalClickException;
import it.polimi.iodice_moro.exceptions.NotAllowedMoveException;
import it.polimi.iodice_moro.exceptions.PartitaIniziataException;
import it.polimi.iodice_moro.model.TipoMossa;
import it.polimi.iodice_moro.view.IFView;

import java.awt.Color;
import java.awt.Point;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

/**
 * Classe che rappresenta l'interfaccia del controller, usata per implementare RMI e Socket
 *
 */
public interface IFController extends Remote{

	/**
	 * Metodo chiamato dalla view per spostare la pecora nell'altra regione adiacente alla
	 * posizione del giocatore corrente.
	 * @param idRegione Regione dove si trova la pecora da spostare.
	 * @throws NotAllowedMoveException Se il giocatore non può effettuare la mossa.
	 * @throws RemoteException
	 * @throws IllegalClickException Se il giocatore ha cliccato un area che non corrisponde a 
	 * un area dove può essere applicato il metodo.
	 */
	public void spostaPecora(String idRegione) throws NotAllowedMoveException, RemoteException, IllegalClickException;
	
	/**
	 * Metodo lanciato su una regione con almeno due pecore. Viene lanciato un dado e
	 * se il risultato è uguale al numero della casella su cui si trova il giocatore verrà
	 * aggiunta un'altra pecora nella regione.
	 * @param idRegione Regione su cui effettuare i controlli.
	 * @throws NotAllowedMoveException Se la mossa non può essere effettuata in quella regione.
	 * @throws RemoteException
	 * @throws IllegalClickException Se l'utente ha cliccato su un'area dove questa mossa non ha senso.
	 */
	public void accoppiamento1(String idRegione) throws NotAllowedMoveException,RemoteException, IllegalClickException;
	
	/**
	 * Viene lanciato un dado e se il risultato è uguale al numero della casella su cui è 
	 * posizionato il pastore che ha chiamato la mossa, allora il numero di pecore della regione
	 * diminuirà di un'unità.
	 * @param idRegione Regione su cui effettuare la mossa.
	 * @throws NotAllowedMoveException Se non ci sono pecore nella regione.
	 * @throws RemoteException
	 * @throws IllegalClickException Se il giocatore ha cliccato su un'area dove la mossa non ha senso.
	 */
	public void sparatoria1(String idRegione) throws NotAllowedMoveException, RemoteException, IllegalClickException;

	/**
	 * Metodo chiamato dalla view per effettuare il movimento della pecora nera
	 * con le stesse modalità di quello del movimento della pecora bianca. Controlla se il giocatore corrente può fare ancora mosse (n. mosse): se non può farle
	 * azzera le variabili di turno (fineturno() ) nel giocatore corrente e trova
	 * il prossimo giocatore (modifica il StatoPartita.giocatoreCorrente ).
	 * Inoltre se è il turno finale ed è l'ultimo giocatore mette finePartita().
	 * Ritorna il prossimo giocatore.
	 * @param idRegPecoraNera Posizione della pecora nera.
	 * @throws NotAllowedMoveException Se non è presente una pecora nera da spostare.
	 * @throws RemoteException
	 */
	public void spostaPecoraNera(String idRegPecoraNera)
			throws NotAllowedMoveException, RemoteException;
	
	/**
	 * Alla fine dell'esecuzione del metodo verrà aggiunta una tessere al giocatore corrente
	 * dello stesso tipo della regione passata come parametro.
	 * @param idRegione Id della regione il cui tipo dovrà essere acquistato.
	 * @throws IllegalClickException Se l'utente ha cliccato su un'area dove non ha senso
	 * effettuare questa mossa.
	 * @throws NotAllowedMoveException Se questa mossa non è consentita, cioè se le tessere di
	 * quel tipo sono esaurite, se non ha abbastanza soldi per comprarle o se il tipo della regione
	 * cliccata non corrisponde a uno dei tipo adiacenti alla posizione attuale del giocatore.
	 * @throws RemoteException
	 */
	public void acquistaTessera(String idRegione) throws IllegalClickException, NotAllowedMoveException, RemoteException;

	/**
	 * Sposta la pedina nella posizione passata come parametro.
	 * @param idStrada Nuova posizione della pedina.
	 * @throws IllegalClickException Se non ha senso spostare la pedina in quest'area.
	 * @throws NotAllowedMoveException Se la strada è già occupata o se non ha abbastanza soldi
	 * per muoversi in quella posizione.
	 * @throws RemoteException
	 */
	public void spostaPedina(String idStrada) throws IllegalClickException, NotAllowedMoveException, RemoteException;

	/**
	 * Crea un nuovo giocatore con nome assegnato come paramentro. Gli viene anche assegnato
	 * uno dei colori disponibili.
	 * @param nome Nome del giocatore
	 * @return Colore assegnato al nuovo giocatore.
	 * @throws RemoteException
	 * @throws PartitaIniziataException Se la partita è già iniziata e non può essere creato un
	 * nuovo giocatore.
	 */
	public Color creaGiocatore(String nome) throws RemoteException, PartitaIniziataException;

	
	/**
	 * Setta la posizione del pastore dato il suo colore che lo identifica univocamente
	 * @param colore Colore del pastore da spostare.
	 * @param idStrada Strada su cui posizionare il pastore.
	 * @throws RemoteException 
	 * @throws NotAllowedMoveException  Se il pastore è già stato assegnato.
	 * @throws IllegalClickException Se non ha senso posizionare il pastore nell'area selezionata. 
	 */
	public void setStradaGiocatore(Color colore, String idStrada) throws IllegalClickException, NotAllowedMoveException, RemoteException;
	
	/**
	 * Stabilisce se una mossa può essere effettuata.
	 * @param mossaDaEffettuare Mossa che il giocatore vuole effettuare.
	 * @return Ritorna true in caso positivo, false altrimenti.
	 * @throws RemoteException
	 */
	public boolean mossaPossibile(TipoMossa mossaDaEffettuare) throws RemoteException;
	
	/**
	 * Ritorna gli ID delle due regioni adiacenti alla strada in cui si trova il pastore
	 * @return Ritorna la lista di ID delle regioni adiacenti alla strada in cui si trova il pastore (giocatore corrente)
	 */
	public List<String> getIDRegioniAd() throws RemoteException;
	
	/**
	 * Ritorna i colori dei giocatori e i loro nomi.
	 * @return Mappa con associazione colore giocatore - nome giocatore.
	 * @throws RemoteException
	 */
	public Map<Color, String> getGiocatori() throws RemoteException;
	
	/**
	 * Imposta la view con cui il controller dovrà interfacciarsi.
	 * @param view2 View da impostare.
	 * @throws RemoteException
	 */
	public void setView(IFView view2) throws RemoteException;

	/**
	 * Metodo per la chiusura dell'applicazione. Comando alla view di terminare l'applicazione.
	 * Usando nel caso un utente di disconnetta dal gioco.
	 * @throws RemoteException
	 */
	public void end() throws RemoteException;

	/**
	 * Aggiunge la view alla lista dell'elenco delle view con cui dovrà interfacciarsi,
	 * con associato il nome del giocatore a cui quella view appartiene. Usato in RMI.
	 * @param view View da aggiungere.
	 * @param coloreGiocatore Colore del giocatore a cui è associata la view.
	 * @throws RemoteException
	 * @throws PartitaIniziataException Se la partita è già iniziata.
	 */
	public void addView(IFView view, Color coloreGiocatore) throws RemoteException, PartitaIniziataException;

	/**
	 * Metodo usato nel caso di partita con due giocatori. Cambia la posizione principale
	 * del giocatore con quella selezionata dall'utente all'inizio del turno tra le due
	 * disponibili. La seconda posizione diventerà quella che precedentemente era la prima.
	 * @param idStrada Strada selezionata dal'utente.
	 * @throws RemoteException
	 * @throws IllegalClickException Se la strada selezionata non è una delle due posizioni
	 * selezionabili dall'utente.
	 */
	public void cambiaPastore(String idStrada) throws RemoteException, IllegalClickException;

	/**
	 * Effettua le stesse mosse di sparatoria 1, ma in aggiunta se è stata uccisa una pecora
	 * viene lanciato un dado per ogni giocatore posizionato adiacentemente alla regione 
	 * cliccata. Per ogni lancio, se il risultato è uguale a 6 il giocatore corrente dovrà 
	 * donare due monete al giocatore a cui il lancio è riferito.
	 * @param hexString Id della regione su cui effettuare la mossa.
	 * @throws RemoteException Se ci sono problemi di rete.
	 * @throws IllegalClickException Se non ha senso effettuare la mossa sull'area cliccata.
	 * @throws NotAllowedMoveException Se non ci sono pecore nella regione o se il giocatore
	 * non ha abbastanza soldi per eventualmente pagare tutti i giocatori adiacenti alla regione.
	 */
	public void sparatoria2(String hexString) throws RemoteException, IllegalClickException, NotAllowedMoveException;
	
	


}