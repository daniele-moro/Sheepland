package it.polimi.iodice_moro.controller;

import java.awt.Color;
import java.awt.Point;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import it.polimi.iodice_moro.model.Giocatore;
import it.polimi.iodice_moro.model.TipoTerreno;
import it.polimi.iodice_moro.view.IFView;

public class FakeView implements IFView {

	private Map<Color, String> posGiocatori;
	private Map<String,Point> posStrade;
	private Map<String, Point> posRegioni;
	private int lancioDado;
	
	public FakeView() {
		posGiocatori = new HashMap<Color, String>();
		posStrade = new HashMap<String, Point>();
		posRegioni = new HashMap<String,Point>();
	}

	public Map<Color, String> getPosGiocatori() {
		return posGiocatori;
	}
	
	public int getLancioDado() {
		return lancioDado;
	}

	public Map<String, Point> getPosStrade() {
		return posStrade;
	}

	public Map<String, Point> getPosRegioni() {
		return posRegioni;
	}

	@Override
	public void initMappa() throws RemoteException {
		// TODO Auto-generated method stub

	}

	@Override
	public void cambiaGiocatore(Color color) throws RemoteException {
		// TODO Auto-generated method stub

	}

	@Override
	public void addCancelloNormale(String stradaID) throws RemoteException {

	}

	@Override
	public void addCancelloFinale(String stradaID) throws RemoteException {

	}

	@Override
	public void spostaPecoraBianca(String s, String d) throws RemoteException {

	}

	@Override
	public void spostaPastore(String s, String d, Color colore)
			throws RemoteException {

	}

	@Override
	public void spostaPecoraNera(String s, String d) throws RemoteException {

	}

	@Override
	public void spostaLupo(String s, String d) throws RemoteException {

	}

	@Override
	public void modificaQtaPecora(String idReg, int num) throws RemoteException {

	}

	@Override
	public void modQtaTessera(TipoTerreno tess, int num, Color colore)
			throws RemoteException {

	}

	@Override
	public void modSoldiGiocatore(Color coloreGiocatoreDaModificare, int soldi)
			throws RemoteException {

	}

	@Override
	public void incPrezzoTessera(TipoTerreno tess) throws RemoteException {

	}

	@Override
	public void visualizzaPunteggi(Map<Giocatore, Integer> punteggiOrdinati)
			throws RemoteException {
	
	}

	@Override
	public void setGiocatoreCorrente(Color colore) throws RemoteException {
		
	}

	@Override
	public void attendiGiocatori() throws IOException {
	}

	@Override
	public void visRisDado(int numero) throws RemoteException {
		lancioDado=numero;

	}


	@Override
	public void setPosizioniRegioni(Map<String, Point> posizioniRegioni)
			throws RemoteException {
		this.posRegioni=posizioniRegioni;

	}

	@Override
	public void setPosizioniStrade(Map<String, Point> posizioniCancelli)
			throws RemoteException {
		this.posStrade=posizioniCancelli;
	}

	@Override
	public void setGiocatori(Map<Color, String> giocatori)
			throws RemoteException {
		this.posGiocatori=giocatori;

	}

	@Override
	public void close() throws RemoteException {
		// TODO Auto-generated method stub

	}

	@Override
	public void posiziona2Pastore(String idStrada, Color colore)
			throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void selezPast(Color colore) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void usaPast2(Color colore) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

}
