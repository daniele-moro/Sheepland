package it.polimi.iodice_moro.controller;

import it.polimi.iodice_moro.model.Giocatore;
import it.polimi.iodice_moro.model.StatoPartita;
import it.polimi.iodice_moro.model.Strada;
import it.polimi.iodice_moro.model.TipoMossa;
import it.polimi.iodice_moro.model.TipoTerreno;
import it.polimi.iodice_moro.view.IFView;
import it.polimi.iodice_moro.view.View;

import java.awt.Color;
import java.awt.Point;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public interface IFController extends Remote{

	//OK
	public abstract void spostaPecora(String idRegione) throws Exception;

	//OK
	public abstract void spostaPecoraNera(String idRegPecoraNera)
			throws Exception;
	
	//OK
	public abstract void acquistaTessera(String idRegione) throws Exception;

	
	//OK
	public abstract void spostaPedina(String idStrada) throws Exception;

	
	//OK
	public abstract Color creaGiocatore(String nome) throws RemoteException ;

	
	//OK
	/**
	 * Metodo nel caso di due giocatori in cui ogni pastore ha due pedine
	 * @param colore
	 * @param idStrada
	 * @param idStrada2
	 */
	public abstract void setStradaGiocatore(Color colore, String idStrada,
			String idStrada2) throws RemoteException ;
	
	
	/**
	 * Setta la posizione del pastore dato il suo colore che lo identifica univocamente
	 * @param colore
	 * @param idStrada
	 * @throws Exception 
	 */
	public abstract void setStradaGiocatore(Color colore, String idStrada) throws Exception;
	
	//OK
	/**
	 * Stabilisce se una mossa può essere effettuata.
	 * @param mossaDaEffettuare Mossa che il giocatore vuole effettuare.
	 * @return Ritorna true in caso positivo, false altrimenti.
	 */
	public abstract boolean mossaPossibile(TipoMossa mossaDaEffettuare) throws RemoteException;

	
	//OK
	public abstract void iniziaPartita() throws RemoteException;

	/*
		List<Giocatore> listaGiocatori = statoPartita.getGiocatori();
		for (int i = 0; i<listaGiocatori.size(); i++) {
			listaGiocatori.get(i).setColore(colori[i]);
		}
	 */
	//AGGIUNTE!!!
	
	//OK
	/**
	 * Ritorna l'elenco delle posizioni di tutte le regioni, con i colori loro assegnati
	 * @return
	 */
	public abstract Map<String, Point> getPosRegioni() throws RemoteException;

	//OK
	public abstract Map<String, Point> getPosStrade() throws RemoteException;

	
	//OK
	/**
	 * Ritorna gli ID delle due regioni adiacenti alla strada in cui si trova il pastore
	 * @return Ritorna la lista di ID delle regioni adiacenti alla strada in cui si trova il pastore (giocatore corrente)
	 */
	public abstract List<String> getIDRegioniAd() throws RemoteException;
	
	//OK
	/**
	 * Ritorna i colori dei giocatori e i loro nomi
	 * @return
	 */
	public abstract Map<Color, String> getGiocatori() throws RemoteException;
	
	public abstract void setView(IFView view2) throws RemoteException;

	void addView(IFView view, Color coloreGiocatore) throws RemoteException;


}