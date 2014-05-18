package it.polimi.iodice_moro.model;

public class Regione extends VerticeGrafo{
	
	//ATTRIBUTI
	private int numPecore;
	private boolean pecoraNera;
	
	//Attributo immutabile
	private final TipoTerreno tipo;
	
	//Costruttore per l'inizializzazione della mappa nel caricamento da XML
	public Regione(String tipo){
		this.tipo=TipoTerreno.parseInput(tipo);
		this.numPecore=0;
		this.pecoraNera=false;
	}

	
	
	//GETTER & SETTER degli attributi
	public boolean isPecoraNera() {
		return pecoraNera;
	}
	public void setPecoraNera(boolean pecoraNera) {
		this.pecoraNera = pecoraNera;
	}
	public int getNumPecore() {
		return numPecore;
	}
	public TipoTerreno getTipo() {
		return tipo;
	}

	
	//toString usato solo per debug
	@Override
	public String toString() {
		return "Regione [numPecore=" + numPecore + ", pecoraNera=" + pecoraNera
				+ ", tipo=" + tipo + "]" +"\n";
	}

	//Metodo per verificare a runtime il tipo dinamico dell'istanza corrente
	@Override
	public boolean isRegione() {
		return true;
	}
	

}
