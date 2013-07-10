package com.malcom.library.android.utils.encoding;

import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * Resource bundle utility class to work allways with UTF-8 character encoding.
 * 
 * @author Malcom Ventures, S.L.
 * @since  2012
 *
 */
public abstract class UTF8ResourceBundle
{
	public static final ResourceBundle getBundle(String baseName)
	{
		ResourceBundle bundle = ResourceBundle.getBundle(baseName);
		return createUtf8PropertyResourceBundle(bundle);
	}

	public static final ResourceBundle getBundle(String baseName, Locale locale)
	{
		ResourceBundle bundle = ResourceBundle.getBundle(baseName, locale);
		return createUtf8PropertyResourceBundle(bundle);
	}

	private static ResourceBundle createUtf8PropertyResourceBundle(ResourceBundle bundle)
	{
		if (!(bundle instanceof PropertyResourceBundle))
			return bundle;

		return new Utf8PropertyResourceBundle((PropertyResourceBundle) bundle);
	}

	private static class Utf8PropertyResourceBundle extends ResourceBundle
	{
		private static final String UTF_8 = "UTF-8";

		private static final String ISO_8859_1 = "ISO-8859-1";

		private final PropertyResourceBundle bundle;

		private Utf8PropertyResourceBundle(final PropertyResourceBundle bundle)
		{
			this.bundle = bundle;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.ResourceBundle#getKeys()
		 */
		public Enumeration<String> getKeys()
		{
			return bundle.getKeys();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.ResourceBundle#handleGetObject(java.lang.String)
		 */
		protected Object handleGetObject(String key)
		{
			String value = (String) bundle.handleGetObject(key);
			try
			{
				return (value == null) ? "[key " + key + "]" : new String(value.getBytes(ISO_8859_1), UTF_8);
			} catch (UnsupportedEncodingException e)
			{
				// Shouldn't fail - but should we still add logging message?
				return null;
			}
		}

	}

}
