package it.polimi.iodice_moro.model;

/**
 * Enumeratore per il tipo di mossa effettuata dal giocatore
 * @author Antonio Iodice, Daniele Moro
 *
 */
public enum TipoMossa {
	/*
	 * Il valore NO_MOSSA rappresenta il fatto che il giocatore debba ancora fare una mossa 
	 */
	SPOSTA_PASTORE, SPOSTA_PECORA, COMPRA_TESSERA, NO_MOSSA, SELEZ_POSIZ;
	
	/**
	 * Override del metodo toString.
	 * Lo scopo e' stampare il valore dell'enumeratore in minuscolo
	 */
	@Override
	public String toString(){
		return super.toString().toLowerCase();
	}
	public static TipoMossa parseInput(String input){
		/*
		 * Porto la stringa di input in maiuscolo, il metodo valueOf confronta il secondo parametro con tutte le possibili istanze dell'enumeratore,
		 * se non matcha con nessuna della possibilita mi torna un eccezione di tipo IllegalArgumentException
		 */
		return Enum.valueOf(TipoMossa.class,  input.toUpperCase());  
	}

}
