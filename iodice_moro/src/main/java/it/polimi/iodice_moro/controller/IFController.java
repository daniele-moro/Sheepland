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

	public abstract void spostaPecora(String idRegione) throws NotAllowedMoveException, RemoteException, IllegalClickException;
	
	public abstract void accoppiamento1(String idRegione) throws NotAllowedMoveException,RemoteException, IllegalClickException;
	
	public abstract void sparatoria1(String idRegione) throws NotAllowedMoveException, RemoteException, IllegalClickException;

	public abstract void spostaPecoraNera(String idRegPecoraNera)
			throws NotAllowedMoveException, RemoteException;
	
	public abstract void acquistaTessera(String idRegione) throws IllegalClickException, NotAllowedMoveException, RemoteException;

	public abstract void spostaPedina(String idStrada) throws IllegalClickException, NotAllowedMoveException, RemoteException;

	public abstract Color creaGiocatore(String nome) throws RemoteException, PartitaIniziataException;

	
	/**
	 * Setta la posizione del pastore dato il suo colore che lo identifica univocamente
	 * @param colore
	 * @param idStrada
	 * @throws RemoteException 
	 * @throws NotAllowedMoveException 
	 * @throws Exception 
	 */
	public abstract void setStradaGiocatore(Color colore, String idStrada) throws IllegalClickException, NotAllowedMoveException, RemoteException;
	
	/**
	 * Stabilisce se una mossa può essere effettuata.
	 * @param mossaDaEffettuare Mossa che il giocatore vuole effettuare.
	 * @return Ritorna true in caso positivo, false altrimenti.
	 */
	public abstract boolean mossaPossibile(TipoMossa mossaDaEffettuare) throws RemoteException;

	
	public abstract void iniziaPartita() throws RemoteException;


	/**
	 * Ritorna l'elenco delle posizioni di tutte le regioni, con i colori loro assegnati
	 * @return
	 */
	public abstract Map<String, Point> getPosRegioni() throws RemoteException;

	public abstract Map<String, Point> getPosStrade() throws RemoteException;

	
	/**
	 * Ritorna gli ID delle due regioni adiacenti alla strada in cui si trova il pastore
	 * @return Ritorna la lista di ID delle regioni adiacenti alla strada in cui si trova il pastore (giocatore corrente)
	 */
	public abstract List<String> getIDRegioniAd() throws RemoteException;
	
	/**
	 * Ritorna i colori dei giocatori e i loro nomi
	 * @return
	 */
	public abstract Map<Color, String> getGiocatori() throws RemoteException;
	
	public abstract void setView(IFView view2) throws RemoteException;

	public abstract void end() throws RemoteException;

	public abstract void addView(IFView view, Color coloreGiocatore) throws RemoteException, PartitaIniziataException;

	public abstract void cambiaPastore(String idStrada) throws RemoteException, IllegalClickException;
	
	


}