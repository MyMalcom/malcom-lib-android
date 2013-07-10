package com.malcom.library.android.module.stats;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.util.Log;

/**
 * Some beacon utilities.
 * 
 * @author Malcom Ventures, S.L.
 * @since  2012
 *
 */
public class BeaconUtils {

	private static final String TAG = "BeaconUtils";
	
	public static double timeIntervalSince1970(Date date) {
		return (double) date.getTime() / 1000;
	}
	
	public static String getDeviceModel() {
		return android.os.Build.MODEL;
	}
	
	public static String getDeviceOsKernel() {
		return android.os.Build.VERSION.RELEASE;
	}
	
	public static String getDeviceOs() {
		String res = "Unknown";
		
		//http://developer.android.com/about/dashboards/index.html
		switch(android.os.Build.VERSION.SDK_INT){
			case 3:
				res = "Cupcake(v1.5)";
				break;
			case 4:
				res = "Donut(v1.6)";
				break;			
			case 7:
				res = "Eclair(v2.1)";
				break;
			case 8:
				res = "Froyo(v2.2)";
				break;
			case 9:
				res = "Gingerbread(v2.3)";
				break;
			case 10:
				res = "Gingerbread(v2.3)";
				break;			
			case 12:
				res = "Honeycomb(v3.1)";
				break;
			case 13:
				res = "Honeycomb(v3.2)";
				break;
			case 14:
				res = "Ice Cream Sandwich(v4.0)";
				break;
			case 15:
				res = "Ice Cream Sandwich(v4.0)";
				break;
			case 16:
				res = "JellyBean(v4.1)";
				break;			
		}
		
		return res; 
	}
	
	public static String getDevicePlatform() {
		return "ANDROID";
	}
	
	public static String getDeviceTimeZone() {
		return TimeZone.getDefault().getID();
	}
	
	public static String getDeviceIsoCountry() {
		return Locale.getDefault().getCountry();		
	}
	
	public static String getDeviceIsoLanguage() {		
		return Locale.getDefault().getLanguage();
	}
	
	public static String getApplicationVersion(Context context) {
		String version;
		try {
			version = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			version = "0.0";
		}
		
		return version;
	}
}
