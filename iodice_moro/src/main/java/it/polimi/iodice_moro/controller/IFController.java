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

public interface IFController extends Remote{

	public void spostaPecora(String idRegione) throws NotAllowedMoveException, RemoteException, IllegalClickException;
	
	public void accoppiamento1(String idRegione) throws NotAllowedMoveException,RemoteException, IllegalClickException;
	
	public void sparatoria1(String idRegione) throws NotAllowedMoveException, RemoteException, IllegalClickException;

	public void spostaPecoraNera(String idRegPecoraNera)
			throws NotAllowedMoveException, RemoteException;
	
	public void acquistaTessera(String idRegione) throws IllegalClickException, NotAllowedMoveException, RemoteException;

	public void spostaPedina(String idStrada) throws IllegalClickException, NotAllowedMoveException, RemoteException;

	public Color creaGiocatore(String nome) throws RemoteException, PartitaIniziataException;

	
	/**
	 * Setta la posizione del pastore dato il suo colore che lo identifica univocamente
	 * @param colore
	 * @param idStrada
	 * @throws RemoteException 
	 * @throws NotAllowedMoveException 
	 * @throws Exception 
	 */
	public void setStradaGiocatore(Color colore, String idStrada) throws IllegalClickException, NotAllowedMoveException, RemoteException;
	
	/**
	 * Stabilisce se una mossa può essere effettuata.
	 * @param mossaDaEffettuare Mossa che il giocatore vuole effettuare.
	 * @return Ritorna true in caso positivo, false altrimenti.
	 */
	public boolean mossaPossibile(TipoMossa mossaDaEffettuare) throws RemoteException;

	
	public void iniziaPartita() throws RemoteException;


	/**
	 * Ritorna l'elenco delle posizioni di tutte le regioni, con i colori loro assegnati
	 * @return
	 */
	public Map<String, Point> getPosRegioni() throws RemoteException;

	public Map<String, Point> getPosStrade() throws RemoteException;

	
	/**
	 * Ritorna gli ID delle due regioni adiacenti alla strada in cui si trova il pastore
	 * @return Ritorna la lista di ID delle regioni adiacenti alla strada in cui si trova il pastore (giocatore corrente)
	 */
	public List<String> getIDRegioniAd() throws RemoteException;
	
	/**
	 * Ritorna i colori dei giocatori e i loro nomi
	 * @return
	 */
	public Map<Color, String> getGiocatori() throws RemoteException;
	
	public void setView(IFView view2) throws RemoteException;

	public void end() throws RemoteException;

	public void addView(IFView view, Color coloreGiocatore) throws RemoteException, PartitaIniziataException;

	public void cambiaPastore(String idStrada) throws RemoteException, IllegalClickException;

	public void sparatoria2(String hexString) throws RemoteException, IllegalClickException, NotAllowedMoveException;
	
	


}