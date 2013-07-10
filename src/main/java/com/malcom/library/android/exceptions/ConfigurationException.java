package com.malcom.library.android.exceptions;

/**
 * Used for configuration module exceptions when parsing the configuration data 
 * contained in the received config.json.
 * 
 * @author Malcom Ventures, S.L.
 * @since  2012
 *
 */
public class ConfigurationException extends Exception {
	
	private static final long serialVersionUID = -3007553604214285779L;
	
	public static int CONFIGURATION_EXCEPTION_NO_CONFIG_DATA = -1;
	public static int CONFIGURATION_EXCEPTION_BAD_SERVER_CONFIG_PATH = -2;
	
	private int errorCode = 0;
	
	public ConfigurationException() {
		
	}

	public ConfigurationException(String arg0, int errorCode) {
		super(arg0);
		this.errorCode = errorCode;
	}

	public ConfigurationException(Throwable arg0, int errorCode) {
		super(arg0);
		this.errorCode = errorCode;
	}

	public ConfigurationException(String arg0, Throwable arg1, int errorCode) {
		super(arg0, arg1);
		this.errorCode = errorCode;
	}

	public int getErrorCode(){
		return errorCode;
	}
}
