package it.polimi.iodice_moro.view;

import it.polimi.iodice_moro.model.Giocatore;
import it.polimi.iodice_moro.model.StatoPartita;
import it.polimi.iodice_moro.model.TipoTerreno;

import java.awt.Color;
import java.awt.Point;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface IFView extends Remote{
	
	//INIZIALIZZA TUTTI GLI OGGETTI CHE SONO POSIZIONATI SOPRA LA MAPPA E I GIOCATORI
	public abstract void initMappa() throws RemoteException;

	/**
	 * Cambio giocatore attivo, attivando la label corrispondente al giocatore che deve giocare
	 * @param color Colore del giocatore che deve giocare
	 */
	public abstract void cambiaGiocatore(Color color) throws RemoteException;

	/**
	 * Attiva il giocatore collegato alla schermata di questa istanza
	 */
	public abstract void attivaGiocatore() throws RemoteException ;

	/**
	 * Disattiva il giocatore collegato alla schermata di questa istanza
	 */
	public abstract void disattivaGiocatore() throws RemoteException;

	/**
	 * Aggiunta del cancello normale alla strada collegata all'ID
	 * @param stradaID
	 */
	public abstract void addCancelloNormale(String stradaID) throws RemoteException;

	/**
	 * Aggiunta del cancello finale alla strada collegata all'ID
	 * @param stradaID
	 */
	public abstract void addCancelloFinale(String stradaID) throws RemoteException;

	/**
	 * Animazione di spostamento della pecora bianca
	 * @param s Id della regione da cui spostare la pecora
	 * @param d ID della regione su cui spostare la pecora
	 */
	public abstract void spostaPecoraBianca(String s, String d) throws RemoteException;

	public abstract void spostaPastore(String s, String d, Color colore) throws RemoteException;

	/**
	 * Animazione di spostamento della pecora nera
	 * @param s ID della regione da cui spostare la pecora
	 * @param d ID della regione su cui spostare la pecora
	 */
	public abstract void spostaPecoraNera(String s, String d) throws RemoteException;
	
	/**
	 * Animazione spostamento lupo.
	 * @param s ID della regione da cui spostare il lupo.
	 * @param d ID della regione su cui spostare il lupo.
	 */
	public abstract void spostaLupo(String s, String d) throws RemoteException;
	
	/**
	 * Cambia il numero di pecore della regione con ID passato come parametro
	 * @param idReg Id della regione di cui cambiare il numero di pecore
	 * @param num Numero di pecore presenti nella regione
	 */
	public abstract void modificaQtaPecora(String idReg, int num) throws RemoteException;

	/**
	 * Cambia il numero di tessere del terreno passato come parametro
	 * @param tess Terreno di cui cambiare il numero di tessere
	 * @param num Numero di tessere di quel terreno
	 */
	public abstract void modQtaTessera(TipoTerreno tess, int num, Color colore) throws RemoteException;

	public abstract void modSoldiGiocatore(Color coloreGiocatoreDaModificare,
			int soldi) throws RemoteException;

	public abstract void incPrezzoTessera(TipoTerreno tess) throws RemoteException;

	public abstract void visualizzaPunteggi(
			Map<Giocatore, Integer> punteggiOrdinati) throws RemoteException;

	/**
	 * Serve per settare, da parte del controller il giocatore corrente
	 * @param colore
	 */
	public abstract void setGiocatoreCorrente(Color colore)throws RemoteException;
	
	public abstract void attendiGiocatori() throws IOException;
	
	public abstract void visRisDado(int numero) throws RemoteException;

	abstract void setColore(Color coloreGiocatore) throws RemoteException;

	public abstract void setPosizioniRegioni(Map<String,Point> posizioniRegioni) throws RemoteException;

	public abstract void setPosizioniStrade(Map<String,Point> posizioniCancelli) throws RemoteException;

	public abstract void setGiocatori(Map<Color,String> giocatori) throws RemoteException;

	public abstract void close() throws RemoteException;


}