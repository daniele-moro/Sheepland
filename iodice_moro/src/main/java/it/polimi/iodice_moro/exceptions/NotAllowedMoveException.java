package it.polimi.iodice_moro.exceptions;

public class NotAllowedMoveException extends Exception {

	public NotAllowedMoveException() {
		
	}

	public NotAllowedMoveException(String message) {
		super(message);
	}

	public NotAllowedMoveException(Throwable cause) {
		super(cause);
	}

	public NotAllowedMoveException(String message, Throwable cause) {
		super(message, cause);
	}
	

}
