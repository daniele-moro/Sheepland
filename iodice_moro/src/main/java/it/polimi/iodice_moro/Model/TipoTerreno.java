package it.polimi.iodice_moro.Model;


public enum TipoTerreno {
SABBIA, MONTAGNA, PIANURA, BOSCO, PALUDI, COLTIVAZIONI, SHEEPSBURG;
	
	// una specie di costruttore che mi serve per instanziare l'enumeratore corretto
	public static TipoTerreno parseInput(String input){
		return Enum.valueOf(TipoTerreno.class,  input.toUpperCase());  //Porto la stringa di input in maiuscolo, il metodo valueOf confronta il secondo parametro con tutte le possibili istanze dell'enumeratore, se non matcha con nessuna della possibilita mi torna un eccezione di tipo illegal argument exception
	}
	
	
	@Override
	//il metodo ritorna il valore dell'enum in minuscolo
	public String toString(){
		return super.toString().toLowerCase();
	}


}
