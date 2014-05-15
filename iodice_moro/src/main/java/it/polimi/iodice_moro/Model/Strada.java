package it.polimi.iodice_moro.Model;

public class Strada extends VerticeGrafo {
	
	//ATTRIBUTI
	private final int nCasella;//Attributo immutabile
	private boolean recinto;
	
	//Costruttore usato per l'inizializzazione della mappa caricata da XML
	public Strada(int nCasella){
		this.nCasella=nCasella;
		this.recinto=false;
	}

	
	//GETTER & SETTER degli attributi
	public boolean isRecinto() {
		return recinto;
	}
	public void setRecinto(boolean recinto) {
		this.recinto = recinto;
	}
	public int getnCasella() {
		return nCasella;
	}


	//toString usato solo per debug
	@Override
	public String toString() {
		return "Strada [nCasella=" + nCasella + ", recinto=" + recinto + "]";
	}



	//Metodo per verificare a runtime il tipo dinamico dell'istanza corrente
	@Override
	public boolean isRegione() {
		return false;
	}

}
