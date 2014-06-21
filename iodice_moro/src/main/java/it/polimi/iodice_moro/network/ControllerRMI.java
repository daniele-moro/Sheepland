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
	
	/**
	 * Costruttore del ControllerRMI.
	 * @param ip Ip a cui ci si dovrà connettere.
	 */
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
	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.controller.IFController#spostaPecora(java.lang.String)
	 */
	public void spostaPecora(String idRegione) throws NotAllowedMoveException,
			RemoteException, IllegalClickException {
		controller.spostaPecora(idRegione);
		
	}

	@Override
	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.controller.IFController#accoppiamento1(java.lang.String)
	 */
	public void accoppiamento1(String idRegione)
			throws NotAllowedMoveException, RemoteException,
			IllegalClickException {
		controller.accoppiamento1(idRegione);
		
	}

	@Override
	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.controller.IFController#accoppiamento1(java.lang.String)
	 */
	public void sparatoria1(String idRegione) throws NotAllowedMoveException,
			RemoteException, IllegalClickException {
		controller.sparatoria1(idRegione);
		
	}

	@Override
	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.controller.IFController#spostaPecoraNera(java.lang.String)
	 */
	public void spostaPecoraNera(String idRegPecoraNera)
			throws NotAllowedMoveException, RemoteException {
		controller.spostaPecoraNera(idRegPecoraNera);
		
	}

	@Override
	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.controller.IFController#acquistaTessera(java.lang.String)
	 */
	public void acquistaTessera(String idRegione) throws IllegalClickException,
			NotAllowedMoveException, RemoteException {
		controller.acquistaTessera(idRegione);
		
	}

	@Override
	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.controller.IFController#sspostaPedina(java.lang.String)
	 */
	public void spostaPedina(String idStrada) throws IllegalClickException,
			NotAllowedMoveException, RemoteException {
		controller.spostaPedina(idStrada);
		
	}

	@Override
	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.controller.IFController#creaGiocatore(java.lang.String)
	 */
	public Color creaGiocatore(String nome) throws RemoteException, PartitaIniziataException {
		if(partitaIniziata || controller.getGiocatori().size()>=4) {
			throw new PartitaIniziataException();
		} else{
			return controller.creaGiocatore(nome);
		}
	}

	@Override
	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.controller.IFController#setStradaGiocatore(java.lang.String)
	 */
	public void setStradaGiocatore(Color colore, String idStrada)
			throws IllegalClickException, NotAllowedMoveException,
			RemoteException {
		controller.setStradaGiocatore(colore, idStrada);
		
	}

	@Override
	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.controller.IFController#mossaPossibile(java.lang.String)
	 */
	public boolean mossaPossibile(TipoMossa mossaDaEffettuare)
			throws RemoteException {
		return controller.mossaPossibile(mossaDaEffettuare);
	}

	@Override
	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.controller.IFController#iniziaPartita
	 */
	public void iniziaPartita() throws RemoteException {
		controller.iniziaPartita();
		
	}

	@Override
	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.controller.IFController#getPosRegioni
	 */
	public Map<String, Point> getPosRegioni() throws RemoteException {
		return controller.getPosRegioni();
	}

	@Override
	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.controller.IFController#getPosStrade
	 */
	public Map<String, Point> getPosStrade() throws RemoteException {
		return controller.getPosStrade();
	}

	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.controller.IFController#getIDRegioniAd
	 */
	@Override
	public List<String> getIDRegioniAd() throws RemoteException {
		return controller.getIDRegioniAd();
	}

	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.controller.IFController#getGiocatori
	 */
	@Override
	public Map<Color, String> getGiocatori() throws RemoteException {
		return controller.getGiocatori();
	}

	@Override
	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.controller.IFController#setView(IFView)
	 */
	public void setView(IFView view2) throws RemoteException {
		controller.setView(view2);
		
	}

	@Override
	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.controller.IFController#end
	 */
	public void end() throws RemoteException {
		controller.end();
		
	}

	@Override
	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.controller.IFController#addView(View.IFView, Color)
	 */
	public void addView(IFView view, Color coloreGiocatore)
			throws RemoteException, PartitaIniziataException {
		controller.addView(view, coloreGiocatore);
		
	}

	@Override
	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.controller.IFController#cambiaPastore1(java.lang.String)
	 */
	public void cambiaPastore(String idStrada) throws RemoteException,
			IllegalClickException {
		controller.cambiaPastore(idStrada);
		
	}

	/* (non-Javadoc)
	 * @see it.polimi.iodice_moro.controller.IFController#sparatoria2(java.lang.String)
	 */
	@Override	
	public void sparatoria2(String idStrada) throws RemoteException,
			IllegalClickException, NotAllowedMoveException {
		controller.sparatoria2(idStrada);
		
	}


}