package com.geekadoo.exceptions;

/**
 * An exception designed to be thrown when an illegal Yaniv is attempted.
 * @author Elad
 */
public class TestDebugException extends RuntimeException {
	public TestDebugException(String code, String message) {
		super(message);
	}
	private static final long serialVersionUID = -2690350835355321831L;
}