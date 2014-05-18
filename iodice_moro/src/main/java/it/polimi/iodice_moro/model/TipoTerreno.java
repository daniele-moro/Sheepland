package it.polimi.iodice_moro.model;


/**
 * Enumeratore per il tipo del terreno della mappa
 * @author Antonio Iodice, Daniele Moro
 *
 */

public enum TipoTerreno {
SABBIA, MONTAGNA, PIANURA, BOSCO, PALUDI, COLTIVAZIONI, SHEEPSBURG;

	/**
	 * 
	 * @param input Stringa con il valore che deve assumere l'enum
	 * @return Istanza dell'enumeratore corretta
	 * @throws IllegalArgumentException
	 */
	public static TipoTerreno parseInput(String input) throws IllegalArgumentException{
		/*
		 * Porto la stringa di input in maiuscolo, il metodo valueOf confronta il secondo parametro con tutte le possibili istanze dell'enumeratore,
		 * se non matcha con nessuna della possibilita mi torna un eccezione di tipo IllegalArgumentException
		 */
		return Enum.valueOf(TipoTerreno.class,  input.toUpperCase());  
	}
	
	/**
	 * Override del metodo toString.
	 * Lo scopo e' stampare il valore dell'enumeratore in minuscolo
	 */
	@Override
	public String toString(){
		return super.toString().toLowerCase();
	}
	
	/**
	 * @return Vettore con tutti i possibili valori che puo' assumere l'enumeratore TipoTerreno
	 */
	public static TipoTerreno[] getTipi(){
		return TipoTerreno.class.getEnumConstants();
	}
	
}
