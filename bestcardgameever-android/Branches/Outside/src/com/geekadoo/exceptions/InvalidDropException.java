package com.geekadoo.exceptions;

/**
 * An exception designed to be thrown when an illegal Yaniv is attempted.
 * @author Elad
 */
public class InvalidDropException extends Exception {
	public InvalidDropException(String code, String message) {
		super(message);
	}
	private static final long serialVersionUID = -2690350835355321831L;
}