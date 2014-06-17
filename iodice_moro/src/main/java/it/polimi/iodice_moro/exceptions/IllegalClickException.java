package it.polimi.iodice_moro.exceptions;

public class IllegalClickException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public IllegalClickException() {
		//
	}

	public IllegalClickException(String message) {
		super(message);
	}

	public IllegalClickException(Throwable cause) {
		super(cause);
	}

	public IllegalClickException(String message, Throwable cause) {
		super(message, cause);
	}


}
