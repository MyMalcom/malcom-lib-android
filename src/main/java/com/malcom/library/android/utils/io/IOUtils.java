package com.malcom.library.android.utils.io;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;

/**
 * Some I/O utility methods.
 * 
 * @author	Malcom Ventures, S.L.
 * @since	2012
 * 
 */
public class IOUtils
{

	public static String convertStreamToString(InputStream is) throws IOException {
		/*
		 * To convert the InputStream to String we use the
		 * BufferedReader.readLine() method. We iterate until the BufferedReader
		 * return null which means there's no more data to read. Each line will
		 * appended to a StringBuilder and returned as String.
		 */
		if (is != null) {
			StringBuilder sb = new StringBuilder();
			String line;
		
			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is, "UTF-8"));
				while ((line = reader.readLine()) != null) {
					sb.append(line).append("\n");
				}
			} finally {
				is.close();
			}
			return sb.toString();
		} else {
			return "";
		}
	}
	
	public static ByteArrayInputStream inputStreamToByteArrayInputStream(InputStream is) throws IOException
	{
		byte[] buff = new byte[8000];

		int bytesRead = 0;

		ByteArrayOutputStream bao = new ByteArrayOutputStream();

		while ((bytesRead = is.read(buff)) != -1)
		{
			bao.write(buff, 0, bytesRead);
		}

		byte[] data = bao.toByteArray();

		return new ByteArrayInputStream(data);
	}

	/**
	 * Compare two input stream
	 * 
	 * @param input1
	 *            the first stream
	 * @param input2
	 *            the second stream
	 * @return true if the streams contain the same content, or false otherwise
	 * @throws IOException
	 * @throws IllegalArgumentException
	 *             if the stream is null
	 */
	public static boolean contentEquals(InputStream input1, InputStream input2) throws IOException
	{
		boolean error = false;
		try
		{
			byte[] buffer1 = new byte[1024];
			byte[] buffer2 = new byte[1024];
			try
			{
				int numRead1 = 0;
				int numRead2 = 0;
				while (true)
				{
					numRead1 = input1.read(buffer1);
					numRead2 = input2.read(buffer2);
					if (numRead1 > -1)
					{
						if (numRead2 != numRead1)
							return false;
						// Otherwise same number of bytes read
						if (!Arrays.equals(buffer1, buffer2))
							return false;
						// Otherwise same bytes read, so continue ...
					} else
					{
						// Nothing more in stream 1 ...
						return numRead2 < 0;
					}
				}
			} finally
			{
				input1.close();
			}
		} catch (IOException e)
		{
			error = true; // this error should be thrown, even if there is an
			// error closing stream 2
			throw e;
		} catch (RuntimeException e)
		{
			error = true; // this error should be thrown, even if there is an
			// error closing stream 2
			throw e;
		} finally
		{
			try
			{
				input2.close();
			} catch (IOException e)
			{
				if (!error)
					throw e;
			}
		}
	}

	/**
	 * Read and return the entire contents of the supplied {@link InputStream
	 * stream}. This method always closes the stream when finished reading.
	 * 
	 * @param stream
	 *            the stream to the contents; may be null
	 * @return the contents, or an empty byte array if the supplied reader is
	 *         null
	 * @throws IOException
	 *             if there is an error reading the content
	 */
	public static byte[] readBytes(InputStream stream) throws IOException
	{
		if (stream == null)
			return new byte[] {};

		byte[] buffer = new byte[1024];
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		boolean error = false;
		try
		{
			int numRead = 0;
			while ((numRead = stream.read(buffer)) > -1)
			{
				output.write(buffer, 0, numRead);
			}
		} catch (IOException e)
		{
			error = true; // this error should be thrown, even if there is an
						  // error closing stream
			throw e;
		} catch (RuntimeException e)
		{
			error = true; // this error should be thrown, even if there is an
						  // error closing stream
			throw e;
		} finally
		{
			try
			{
				stream.close();
			} catch (IOException e)
			{
				if (!error)
					throw e;
			}
		}
		output.flush();
		return output.toByteArray();
	}

	/**
	 * Read and return the entire contents of the supplied {@link InputStream}.
	 * This method always closes the stream when finished reading.
	 * 
	 * @param stream
	 *            the streamed contents; may be null
	 * @return the contents, or an empty string if the supplied stream is null
	 * @throws IOException
	 *             if there is an error reading the content
	 */
	public static String read(InputStream stream) throws IOException
	{
		return stream == null ? "" : read(new InputStreamReader(stream));
	}

	/**
	 * Read and return the entire contents of the supplied {@link Reader}. This
	 * method always closes the reader when finished reading.
	 * 
	 * @param reader
	 *            the reader of the contents; may be null
	 * @return the contents, or an empty string if the supplied reader is null
	 * @throws IOException
	 *             if there is an error reading the content
	 */
	public static String read(Reader reader) throws IOException
	{
		if (reader == null)
			return "";
		StringBuilder sb = new StringBuilder();
		boolean error = false;
		try
		{
			int numRead = 0;
			char[] buffer = new char[1024];
			while ((numRead = reader.read(buffer)) > -1)
			{
				sb.append(buffer, 0, numRead);
			}
		} catch (IOException e)
		{
			error = true; // this error should be thrown, even if there is an
			// error closing reader
			throw e;
		} catch (RuntimeException e)
		{
			error = true; // this error should be thrown, even if there is an
			// error closing reader
			throw e;
		} finally
		{
			try
			{
				reader.close();
			} catch (IOException e)
			{
				if (!error)
					throw e;
			}
		}
		return sb.toString();
	}
}
