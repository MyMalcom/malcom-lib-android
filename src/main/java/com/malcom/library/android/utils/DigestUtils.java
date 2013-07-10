package com.malcom.library.android.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.malcom.library.android.utils.encoding.base64.Base64;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

/**
 * Neither org.apache.commons.codec.digest.DigestUtils nor org.apache.commons.codec.binary.Hex 
 * take the ENCODING into account, they both use the system's default encoding which is wrong 
 * in a web environment.
 * 
 * @author Malcom Ventures, S.L.
 * @since  2012
 */
public class DigestUtils
{
	private static final String CP1252 = "CP1252";

	private static final char[] HEXITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

	private static final String MD5_ALGORITHM = "MD5";
	private static final String SHA1_ALGORITHM = "SHA1";
	private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
	
	private static final String UTF_8 = "UTF-8";

	/**
	 * Converts an array of bytes into an array of characters representing the hexidecimal values of each byte in order.
	 * The returned array will be double the length of the passed array, as it takes two characters to represent any
	 * given byte.
	 *
	 * @ascandroli this is by FARRRRR the fastest conversion I've found!
	 * @see: http://stackoverflow.com/questions/332079/in-java-how-do-i-convert-a-byte-array-to-a-string-of-hex-digits-while-keeping-le
	 *
	 * @param data a byte[] to convert to Hex characters
	 * @return A char[] containing hexidecimal characters
	 */
	public static String encodeHex(byte[] data)
	{
		char[] out = new char[data.length << 1]; // == new char[data.length * 2];

		for (int i = 0, j = 0; i < data.length; i++)
		{
			out[j++] = HEXITS[(0xF0 & data[i]) >>> 4]; // HEXITS[(data[i] & 0xFF) / 16];
			out[j++] = HEXITS[0x0F & data[i]]; // HEXITS[(data[i] & 0xFF) % 16];
		}

		return new String(out);
	}
	
	public static String md5Base64(byte[] data) {
		MessageDigest md5Instance = getMD5Instance();
		return new String(Base64.encode(md5Instance.digest(data)));
	}

	public static String md5Hex(byte[] data)
	{
		MessageDigest md5Instance = getMD5Instance();
		return encodeHex(md5Instance.digest(data));
	}

	public static String md5Hex(String message)
	{
		return md5Hex(message, CP1252);
	}
	
	public static String md5Base64(String message)
	{
		return md5Base64(message, CP1252);
	}
	
	public static String md5Hex(String message, String encoding)
	{
		try
		{
			return md5Hex(message.getBytes(encoding));
		} catch (UnsupportedEncodingException e)
		{
			throw new RuntimeException(e);
		}
	}

	public static String md5Base64(String message, String encoding)
	{
		try
		{
			return md5Base64(message.getBytes(encoding));
		} catch (UnsupportedEncodingException e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 * Genera el codigo SHA1 del mensaje pasado
	 *
	 * @param message al cual se le quiere calcular el codigo SHA1
	 * @return El codigo SHA1 del mensaje pasado
	 */
	public static String sha1Hex(String message)
	{
		// #note @ascandroli the defaultCharset is used because the old Sha1Utils wasn't specifying a charset. This could be removed if tested properly.
		return sha1Hex(message, Charset.defaultCharset().name());
	}

	public static String sha1Hex(String message, String encoding)
	{
		try
		{
			MessageDigest md = getSHA1Instance();
			return encodeHex(md.digest(message.getBytes(encoding)));
		} catch (UnsupportedEncodingException e)
		{
			throw new RuntimeException(e);
		}
	}

	public static MessageDigest getSHA1Instance()
	{
		try
		{
			return MessageDigest.getInstance(SHA1_ALGORITHM);
		} catch (NoSuchAlgorithmException e)
		{
			throw new RuntimeException(e);
		}
	}

	public static MessageDigest getMD5Instance()
	{
		try
		{
			return MessageDigest.getInstance(MD5_ALGORITHM);
		} catch (NoSuchAlgorithmException e)
		{
			throw new RuntimeException(e);
		}
	}


	/**
	 * Computes RFC 2104-compliant HMAC signature.
	 *
	 * @param data The data to be signed.
	 * @param key  The signing key.
	 * @return The Base64-encoded RFC 2104-compliant HMAC signature.
	 * @throws java.security.SignatureException
	 *          when signature generation fails
	 */
	public static String calculateRFC2104HMAC(String data, String key) throws java.security.SignatureException
	{
		try
		{

			SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(UTF_8), HMAC_SHA1_ALGORITHM);

			// Get an hmac_sha1 Mac instance and initialize with the signing key
			Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
			mac.init(signingKey);

			// Compute the hmac on input data bytes
			byte[] rawHmac = mac.doFinal(data.getBytes(UTF_8));
			//It is important to use here the same implementation of B64 used in Malcom!!.
			return new String(Base64.encode(rawHmac));

		} catch (Exception e)
		{
			throw new SignatureException("Failed to generate HMAC : " + e.getMessage());
		}
	}

}
