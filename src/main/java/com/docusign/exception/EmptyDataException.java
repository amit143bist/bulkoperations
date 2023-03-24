/**
 * 
 */
package com.docusign.exception;

/**
 * @author Amit.Bist
 *
 */
public class EmptyDataException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4117719841271888177L;

	/**
	 * 
	 */
	public EmptyDataException() {

	}

	/**
	 * @param message
	 */
	public EmptyDataException(String message) {
	
		super(message + " field value cannot be empty/null");
	}

	/**
	 * @param cause
	 */
	public EmptyDataException(Throwable cause) {
		
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public EmptyDataException(String message, Throwable cause) {
		
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public EmptyDataException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		
		super(message, cause, enableSuppression, writableStackTrace);
	}

}