package it.polimi.iodice_moro.exceptions;

public class NotAllowedMoveException extends Exception {

	/**
	 * Eccezione lanciata quando l'utente prova a compiere una mossa non permesa dalle regole
	 * del gioco.
	 * @author Antonio Iodice, Daniele Moro
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs NotAllowedMoveException with no detail message.
	 */
	public NotAllowedMoveException() {
		
	}

	/**
	 * Constructs an NotAllowedMoveException with the specified detail message.
	 * @param message the detail message.
	 */
	public NotAllowedMoveException(String message) {
		super(message);
	}

	/**
	 * Constructs a new exception with the specified cause and a detail message 
	 * of (cause==null ? null : cause.toString()) (which typically contains the class and 
	 * detail message of cause). This constructor is useful for exceptions that are little more
	 * than wrappers for other throwables (for example, PrivilegedActionException).
	 * @param cause the cause (which is saved for later retrieval by the Throwable.getCause()
	 * method). (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
	 */
	public NotAllowedMoveException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructs a new exception with the specified detail message and cause. Note that
	 * the detail message associated with cause is not automatically 
	 * incorporated in this exception's detail message.
	 * @param message The detail message.
	 * @param cause the cause (which is saved for later retrieval by the Throwable.getCause() 
	 * method). (A null value is permitted, and indicates that the cause is nonexistent
	 * or unknown.)
	 */
	public NotAllowedMoveException(String message, Throwable cause) {
		super(message, cause);
	}
	

}
