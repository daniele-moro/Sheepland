package it.polimi.iodice_moro.exceptions;

public class PartitaIniziataException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Constructs a PartitaIniziataException with no detail message.
	 */
	public PartitaIniziataException() {
		super();
	}

	/**
	 * Constructs a PartitaIniziataException with the specified detail message.
	 * @param message the detail message.
	 */
	public PartitaIniziataException(String message) {
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
	public PartitaIniziataException(Throwable cause) {
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
	public PartitaIniziataException(String message, Throwable cause) {
		super(message, cause);
	}

}
