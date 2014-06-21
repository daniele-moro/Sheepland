package it.polimi.iodice_moro.model;

/**
 * Enumeratore per il tipo di mossa effettuata dal giocatore
 * @author Antonio Iodice, Daniele Moro
 *
 */
public enum TipoMossa {
	/*
	 * Il valore NO_MOSSA rappresenta il fatto che il giocatore debba ancora fare una mossa 
	 * Il valore SELEZ_POSIZ rappresenta il fatto che il giocatore sta selezionando la posizione iniziale del pastore
	 * Il vaore G2_SELEZ_PAST rappresenta il fatto che il gicatore sta selezionando quale pastore dei due (nel caso di due giocatori) vuole utilizzare
	 */
	SPOSTA_PASTORE, SPOSTA_PECORA, COMPRA_TESSERA, NO_MOSSA, SELEZ_POSIZ, G2_SELEZ_PAST, ACCOPPIAMENTO1, SPARATORIA1, SPARATORIA2;
	
	/**
	 * Override del metodo toString.
	 * Lo scopo e' stampare il valore dell'enumeratore in minuscolo
	 */
	@Override
	public String toString(){
		return super.toString().toLowerCase();
	}
	
	/**
	 * Porto la stringa di input in maiuscolo, il metodo valueOf confronta il secondo parametro
	 * con tutte le possibili istanze dell'enumeratore,
	 * Se non matcha con nessuna della possibilita mi torna un eccezione di tipo 
	 * IllegalArgumentException
	 * @param input Stringa da trasformare.
	 * @return Valore dell'enumeratore corrispondente alla stringa in ingresso.
	 * @throws IllegalArgumentException Se non matcha con nessuna della possibilita mi torna un eccezione di tipo 
	 * IllegalArgumentException
	 */
	public static TipoMossa parseInput(String input){
		
		return Enum.valueOf(TipoMossa.class,  input.toUpperCase());  
	}

}
