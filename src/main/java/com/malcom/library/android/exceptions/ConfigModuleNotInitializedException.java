package com.malcom.library.android.exceptions;

/**
 * Exception used for configuration module initialization problems when the 
 * module is not initialized or when is not fully initialized but is being used. 
 * 
 * @author Malcom Ventures, S.L.
 * @since  2012
 *
 */
public class ConfigModuleNotInitializedException extends RuntimeException {

	private static final long serialVersionUID = -7371920309308404865L;

	public ConfigModuleNotInitializedException() {
		
	}

	public ConfigModuleNotInitializedException(String arg0) {
		super(arg0);		
	}

	public ConfigModuleNotInitializedException(Throwable arg0) {
		super(arg0);		
	}

	public ConfigModuleNotInitializedException(String arg0, Throwable arg1) {
		super(arg0, arg1);		
	}

}
