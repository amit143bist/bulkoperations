/**
 * 
 */
package com.docusign.exception;

/**
 * @author Amit.Bist
 *
 */
public class InvalidInputException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2034964405707560323L;

	/**
	 * 
	 */
	public InvalidInputException() {

	}

	/**
	 * @param message
	 */
	public InvalidInputException(String message) {

		super(message + "'s input value is invalid");
	}
	
	/**
	 * @param message
	 * @param envelopeId
	 */
	public InvalidInputException(String message, String envelopeId) {

		super(message + " is invalid for " + envelopeId);
	}

	/**
	 * @param cause
	 */
	public InvalidInputException(Throwable cause) {

		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public InvalidInputException(String message, Throwable cause) {

		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public InvalidInputException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {

		super(message, cause, enableSuppression, writableStackTrace);
	}

}
