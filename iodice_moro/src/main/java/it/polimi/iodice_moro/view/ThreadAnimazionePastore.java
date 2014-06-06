package it.polimi.iodice_moro.view;

import java.awt.Color;

public class ThreadAnimazionePastore implements Runnable {

	private IFView view;
	private String ids;
	private String idd;
	private Color colore;
	
	public ThreadAnimazionePastore(IFView view2, String ids, String idd, Color colore){
		this.view=view2;
		this.idd=idd;
		this.ids=ids;
		this.colore=colore;
	}

	@Override
	public void run() {
		view.spostaPastore(ids, idd, colore);

	}
	
}
