package it.polimi.iodice_moro.view;

public class ThreadAnimazione implements Runnable {
	
	private View view;
	private String ids;
	private String idd;
	
	public ThreadAnimazione(View view2, String ids, String idd){
		this.view=view2;
		this.idd=idd;
		this.ids=ids;
	}

	@Override
	public void run() {
		view.spostaPecoraBianca(ids, idd);

	}

}
