package com.malcom.library.android.exceptions;

/**
 * Exception used for core module initialization problems when the 
 * module is not initialized. 
 * 
 * @author Malcom Ventures, S.L.
 * @since  2012
 *
 */
public class CoreNotInitializedException extends RuntimeException {

	private static final long serialVersionUID = -1390587936663595894L;

	
	public CoreNotInitializedException() {
		
	}

	public CoreNotInitializedException(String arg0) {
		super(arg0);		
	}

	public CoreNotInitializedException(Throwable arg0) {
		super(arg0);		
	}

	public CoreNotInitializedException(String arg0, Throwable arg1) {
		super(arg0, arg1);		
	}

}
