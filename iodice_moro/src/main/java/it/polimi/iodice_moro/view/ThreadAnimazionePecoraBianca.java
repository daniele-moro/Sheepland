package it.polimi.iodice_moro.view;

public class ThreadAnimazionePecoraBianca implements Runnable {
	
	private View view;
	private String ids;
	private String idd;
	
	public ThreadAnimazionePecoraBianca(View view2, String ids, String idd){
		this.view=view2;
		this.idd=idd;
		this.ids=ids;
	}

	@Override
	public void run() {
		view.spostaPecoraBianca(ids, idd);

	}

}
