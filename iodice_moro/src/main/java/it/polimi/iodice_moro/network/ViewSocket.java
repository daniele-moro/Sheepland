package it.polimi.iodice_moro.network;

import java.awt.Color;
import java.util.Map;

import it.polimi.iodice_moro.controller.Controller;
import it.polimi.iodice_moro.model.Giocatore;
import it.polimi.iodice_moro.model.TipoTerreno;
import it.polimi.iodice_moro.view.IFView;


//Utilizzata dal controller, quindi dal SERVER
public class ViewSocket implements IFView {
	
	private Controller controller;
	
	public void riceviMossa(){

	}
	public void attendiGiocatori(){
		
	}

	public ViewSocket() {
		
	}

	@Override
	public void initMappa() {
		// TODO Auto-generated method stub

	}

	@Override
	public void cambiaGiocatore(Color color) {
		// TODO Auto-generated method stub

	}

	@Override
	public void attivaGiocatore() {
		// TODO Auto-generated method stub

	}

	@Override
	public void disattivaGiocatore() {
		// TODO Auto-generated method stub

	}

	@Override
	public void addCancelloNormale(String stradaID) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addCancelloFinale(String stradaID) {
		// TODO Auto-generated method stub

	}

	@Override
	public void spostaPecoraBianca(String s, String d) {
		// TODO Auto-generated method stub

	}

	@Override
	public void spostaPastore(String s, String d, Color colore) {
		// TODO Auto-generated method stub

	}

	@Override
	public void spostaPecoraNera(String s, String d) {
		// TODO Auto-generated method stub

	}

	@Override
	public void modificaQtaPecora(String idReg, int num) {
		// TODO Auto-generated method stub

	}

	@Override
	public void modQtaTessera(TipoTerreno tess, int num) {
		// TODO Auto-generated method stub

	}

	@Override
	public void modSoldiGiocatore(Color coloreGiocatoreDaModificare, int soldi) {
		// TODO Auto-generated method stub

	}

	@Override
	public void incPrezzoTessera(TipoTerreno tess) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visualizzaPunteggi(Map<Giocatore, Integer> punteggiOrdinati) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setGiocatoreCorrente(Color colore) {
		// TODO Auto-generated method stub

	}

}
