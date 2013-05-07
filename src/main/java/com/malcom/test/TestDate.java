package com.malcom.test;

import java.util.Date;

public class TestDate {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//Date date = dateWithTimeIntervalSince1970(1358961186);
		Date date = dateWithTimeIntervalSince1970(1358961803);
		

		System.out.println("Date: "+date);

		  }

		public static Date dateWithTimeIntervalSince1970(double nsinterval)

		 {

		  return new Date(new Double(nsinterval * 1000).longValue());



		 }

}
