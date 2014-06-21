package it.polimi.iodice_moro.network;

import it.polimi.iodice_moro.controller.IFController;
import it.polimi.iodice_moro.exceptions.IllegalClickException;
import it.polimi.iodice_moro.exceptions.NotAllowedMoveException;
import it.polimi.iodice_moro.exceptions.PartitaIniziataException;
import it.polimi.iodice_moro.model.TipoMossa;
import it.polimi.iodice_moro.view.IFView;

import java.awt.Color;
import java.awt.Point;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControllerRMI implements IFController {

	IFController controller;
	private static final Logger LOGGER =  Logger.getLogger("it.polimi.iodice_moro.network");
	Boolean partitaIniziata;
	
	public ControllerRMI(String ip) {
		partitaIniziata=false;
		try {
			this.controller = (IFController)Naming.lookup("//"+ip+"/Server");
		} catch (MalformedURLException e) {
			LOGGER.log(Level.SEVERE, "URL non trovato", e);
		} catch (RemoteException e) {
			LOGGER.log(Level.SEVERE, "Errore di rete", e);
		} catch (NotBoundException e) {
			LOGGER.log(Level.SEVERE, "Il riferimenton passato non è associaot a nulla", e);
		}
		
	}

	@Override
	public void spostaPecora(String idRegione) throws NotAllowedMoveException,
			RemoteException, IllegalClickException {
		controller.spostaPecora(idRegione);
		
	}

	@Override
	public void accoppiamento1(String idRegione)
			throws NotAllowedMoveException, RemoteException,
			IllegalClickException {
		controller.accoppiamento1(idRegione);
		
	}

	@Override
	public void sparatoria1(String idRegione) throws NotAllowedMoveException,
			RemoteException, IllegalClickException {
		controller.sparatoria1(idRegione);
		
	}

	@Override
	public void spostaPecoraNera(String idRegPecoraNera)
			throws NotAllowedMoveException, RemoteException {
		controller.spostaPecoraNera(idRegPecoraNera);
		
	}

	@Override
	public void acquistaTessera(String idRegione) throws IllegalClickException,
			NotAllowedMoveException, RemoteException {
		controller.acquistaTessera(idRegione);
		
	}

	@Override
	public void spostaPedina(String idStrada) throws IllegalClickException,
			NotAllowedMoveException, RemoteException {
		controller.spostaPedina(idStrada);
		
	}

	@Override
	public Color creaGiocatore(String nome) throws RemoteException,
			PartitaIniziataException {
		if(partitaIniziata || controller.getGiocatori().size()>=4) {
			throw new PartitaIniziataException();
		}
		else return controller.creaGiocatore(nome);
	}

	@Override
	public void setStradaGiocatore(Color colore, String idStrada)
			throws IllegalClickException, NotAllowedMoveException,
			RemoteException {
		controller.setStradaGiocatore(colore, idStrada);
		
	}

	@Override
	public boolean mossaPossibile(TipoMossa mossaDaEffettuare)
			throws RemoteException {
		return controller.mossaPossibile(mossaDaEffettuare);
	}

	@Override
	public void iniziaPartita() throws RemoteException {
		controller.iniziaPartita();
		
	}

	@Override
	public Map<String, Point> getPosRegioni() throws RemoteException {
		return controller.getPosRegioni();
	}

	@Override
	public Map<String, Point> getPosStrade() throws RemoteException {
		return controller.getPosStrade();
	}

	@Override
	public List<String> getIDRegioniAd() throws RemoteException {
		return controller.getIDRegioniAd();
	}

	@Override
	public Map<Color, String> getGiocatori() throws RemoteException {
		return controller.getGiocatori();
	}

	@Override
	public void setView(IFView view2) throws RemoteException {
		controller.setView(view2);
		
	}

	@Override
	public void end() throws RemoteException {
		controller.end();
	}

	@Override
	public void addView(IFView view, Color coloreGiocatore)
			throws RemoteException, PartitaIniziataException {
		controller.addView(view, coloreGiocatore);
		
	}

	@Override
	public void cambiaPastore(String idStrada) throws RemoteException,
			IllegalClickException {
		controller.cambiaPastore(idStrada);
		
	}

	@Override
	public void sparatoria2(String idStrada) throws RemoteException,
			IllegalClickException, NotAllowedMoveException {
		controller.sparatoria2(idStrada);
		
	}



}