package it.polimi.iodice_moro.view;

import it.polimi.iodice_moro.model.Giocatore;
import it.polimi.iodice_moro.model.TipoTerreno;

import java.awt.Color;
import java.awt.Point;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public interface IFView extends Remote{
	
	/**
	 * Inizializza tutti i componenti che sono posizionati sopra la mappa
	 * @throws RemoteException
	 */
	public void initMappa() throws RemoteException;

	/**
	 * Cambio giocatore attivo, attivando la label corrispondente al giocatore che deve giocare
	 * @param color Colore del giocatore che deve giocare
	 * @throws RemoteException
	 */
	public void cambiaGiocatore(Color color) throws RemoteException;

	/**
	 * Aggiunta del cancello normale alla strada collegata all'ID
	 * @param stradaID
	 * @throws RemoteException
	 */
	public void addCancelloNormale(String stradaID) throws RemoteException;

	/**
	 * Aggiunta del cancello finale alla strada collegata all'ID
	 * @param stradaID
	 * @throws RemoteException
	 */
	public void addCancelloFinale(String stradaID) throws RemoteException;

	/**
	 * Animazione di spostamento della pecora bianca
	 * @param s Id della regione da cui spostare la pecora
	 * @param d ID della regione su cui spostare la pecora
	 * @throws RemoteException
	 */
	public void spostaPecoraBianca(String s, String d) throws RemoteException;

	/**
	 * Animazione di spostamento del pastore 
	 * @param listaMov Lista delle strade su cui il pastore si deve muovere
	 * @param colore Colore del pastore da spostare
	 * @throws RemoteException
	 */
	public void spostaPastore(List<String> listaMov, Color colore) throws RemoteException;

	/**
	 * Animazione di spostamento della pecora nera
	 * @param s ID della regione da cui spostare la pecora
	 * @param d ID della regione su cui spostare la pecora
	 * @throws RemoteException
	 */
	public void spostaPecoraNera(String s, String d) throws RemoteException;
	
	/**
	 * Animazione spostamento lupo.
	 * @param s ID della regione da cui spostare il lupo.
	 * @param d ID della regione su cui spostare il lupo.
	 * @throws RemoteException
	 */
	public void spostaLupo(String s, String d) throws RemoteException;
	
	/**
	 * Cambia il numero di pecore della regione con ID passato come parametro
	 * @param idReg Id della regione di cui cambiare il numero di pecore
	 * @param num Numero di pecore presenti nella regione
	 * @throws RemoteException
	 */
	public void modificaQtaPecora(String idReg, int num) throws RemoteException;

	/**
	 * Cambia il numero di tessere del terreno passato come parametro
	 * @param tess Terreno di cui cambiare il numero di tessere
	 * @param num Numero di tessere di quel terreno
	 * @throws RemoteException
	 */
	public void modQtaTessera(TipoTerreno tess, int num, Color colore) throws RemoteException;
	
	/**
	 * Cambia i soldi del giocatore visualizzati nell'interfaccia
	 * @param coloreGiocatoreDaModificare Colore del giocatore di cui modificare i soldi
	 * @param soldi Nuova quantità di soldi da visualizzare
	 * @throws RemoteException
	 */
	public void modSoldiGiocatore(Color coloreGiocatoreDaModificare, int soldi) throws RemoteException;

	/**
	 * Incrementa il prezzo visualizzato di una tessera
	 * @param tess TipoTerreno della tessera di cui modificare il prezzo
	 * @throws RemoteException
	 */
	public void incPrezzoTessera(TipoTerreno tess) throws RemoteException;

	/**
	 * Visualizzazione dei punteggi a fine partita
	 * @param punteggiOrdinati Map dei colori dei giocatori con collegati i punteggi ordinati in ordine decrescente
	 * @throws RemoteException
	 */
	public void visualizzaPunteggi(Map<Giocatore, Integer> punteggiOrdinati) throws RemoteException;

	/**
	 * Serve per settare, da parte del controller il giocatore corrente
	 * @param colore
	 * @throws RemoteException
	 */
	public void setGiocatoreCorrente(Color colore)throws RemoteException;
	
	/**
	 * Utilizzato per l'attesa dei giocatori nei gestori di rete
	 * @throws IOException
	 */
	public void attendiGiocatori() throws IOException;
	
	/**
	 * Visualizzazione del risultato di un lancio del dado
	 * @param numero Numero ottenuto dal lancio del dado, da visualizzare
	 * @throws RemoteException
	 */
	public void visRisDado(int numero) throws RemoteException;

	/**
	 * Setting delle posizioni in cui visualizzare la label della pecora bianca nelle regioni
	 * @param posizioniRegioni Map in cui abbiamo l'id e la posizione della regione
	 * @throws RemoteException
	 */
	public void setPosizioniRegioni(Map<String,Point> posizioniRegioni) throws RemoteException;

	/**
	 * Setting delle posizioni in cui visualizzare i cancelli sulle caselle nelle strade
	 * @param posizioniCancelli Map in cui abbiamo l'id e la posizione delle caselle
	 * @throws RemoteException
	 */
	public void setPosizioniStrade(Map<String,Point> posizioniCancelli) throws RemoteException;

	/**
	 * Setting dei colori associati ad ogni nome di giocatore
	 * @param giocatori Map in cui abbiamo l'ID (colore del giocatore) e il suo nome
	 * @throws RemoteException
	 */
	public void setGiocatori(Map<Color,String> giocatori) throws RemoteException;
	
	/**
	 * Chiusura dell'applicazione
	 * @throws RemoteException
	 */
	public void close() throws RemoteException;

	/**
	 * Metodo usato per posizionare il secondo pastore, 
	 * usato quando si gioca in due giocatori, in cui ogni giocatore può gestire due pastori
	 * @param idStrada ID della strada dove posizionare il secondo pastore
	 * @param colore Colore del pastore da posizionare
	 * @throws RemoteException
	 */
	public void posiziona2Pastore(String idStrada, Color colore) throws RemoteException;

	/**
	 * Metodo per comandare alla view che bisogna selezionare la posizione del secondo pastore
	 * @param colore Colore del pastore che deve fare il posizionamento del suo secondo pastore
	 * @throws RemoteException
	 */
	public void selezPast(Color colore) throws RemoteException;

	/**
	 * Metodo per comandare alla view che bisogna usare il secondo pastore al posto che il primo
	 * Usato nel caso in cui si giochi in due giocatori
	 * @param colore Colore del giocatore di cui cambiare il pastore utilizzato
	 * @throws RemoteException
	 */
	public void usaPast2(Color colore) throws RemoteException;


}