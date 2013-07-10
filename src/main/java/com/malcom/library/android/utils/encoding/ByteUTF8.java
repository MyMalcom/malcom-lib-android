package com.malcom.library.android.utils.encoding;

import java.io.UnsupportedEncodingException;

/**
 * 
 * Utility class for getting the bytes of a string
 * in UTF-8.
 * 
 * @author Malcom Ventures, S.L.
 * @since  2012
 *
 */
public class ByteUTF8
{
	public static byte[] getBytes(String string)
	{
		try { return string.getBytes("UTF8"); }
		catch (UnsupportedEncodingException e) { return new byte[0]; }
	}
}
