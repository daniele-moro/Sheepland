package it.polimi.iodice_moro.exceptions;

public class IllegalClickException extends Exception {

	/**
	 * Eccezione lanciata quando l'utente clicca con il mouse in un'area che non è compatibile
	 * con l'azione da compiere.
	 * @author Antonio Iodice, Daniele Moro
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs an IllegalClickException with no detail message.
	 */
	public IllegalClickException() {
		//
	}

	/**
	 * Constructs an IllegalClickException with the specified detail message.
	 * @param message the detail message.
	 */
	public IllegalClickException(String message) {
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
	public IllegalClickException(Throwable cause) {
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
	public IllegalClickException(String message, Throwable cause) {
		super(message, cause);
	}


}
