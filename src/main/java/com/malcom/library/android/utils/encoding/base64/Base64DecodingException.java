package com.malcom.library.android.utils.encoding.base64;


import java.io.IOException;

/**
 * Exception that is thrown when an unexpected character is encountered during
 * Base64 decoding. One could catch this exception and use the unexpected
 * character for some other purpose such as including it with data that comes at
 * the end of a Base64 encoded section of an email message.
 * 
 * @author Stephen Ostermiller
 *         http://ostermiller.org/contact.pl?regarding=Java+Utilities
 * @since ostermillerutils 1.00.00
 */
public class Base64DecodingException extends IOException
{
	/**
	 * Serial version ID
	 */
	private static final long serialVersionUID = 2411555227634603928L;

	private char c;

	/**
	 * Construct an new exception.
	 * 
	 * @param message
	 *            message later to be returned by a getMessage() call.
	 * @param c
	 *            character that caused this error.
	 * 
	 * @since ostermillerutils 1.00.00
	 */
	public Base64DecodingException(String message, char c)
	{
		super(message);
		this.c = c;
	}

	/**
	 * Get the character that caused this error.
	 * 
	 * @return the character that caused this error.
	 * 
	 * @since ostermillerutils 1.00.00
	 */
	public char getChar()
	{
		return c;
	}
}

