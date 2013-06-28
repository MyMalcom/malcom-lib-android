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
	
	public static String getDeviceCityLocation(Context context) {
		String res = "";

		if(getGPSStatus(context)){
			
			try{
				LocationManager locationManager = (LocationManager) context.getSystemService(Service.LOCATION_SERVICE);
				Location lastKnownLocation = locationManager.getLastKnownLocation(mostAccurateLocationProvider(context));
				
				try {
					Geocoder gcd = new Geocoder(context, Locale.getDefault());
					List<Address> addresses = gcd.getFromLocation(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), 1);
					if (addresses.size() > 0) { 
					    res = addresses.get(0).getLocality();
					}
				} catch (IOException e) {
					// TODO: handle exception
				}
				
			}catch(SecurityException e){
				Log.i(TAG, "We could not obtain the city from the location due to :" + e.getMessage());
			}	
		}
		
		return res;
	}
	
	public static JSONObject getLocationJson(Context context) {
		
		JSONObject locationJson = new JSONObject();

		if(getGPSStatus(context)){
			try{
				LocationManager locationManager = (LocationManager) context.getSystemService(Service.LOCATION_SERVICE);
				Location lastKnownLocation = locationManager.getLastKnownLocation(mostAccurateLocationProvider(context));
			
				// TODO: handle exception
				try {
					if (lastKnownLocation != null)
					{
						Float accuracy = new Float(lastKnownLocation.getAccuracy());
						locationJson.put("accuracy", accuracy.longValue());
						locationJson.put("latitude", lastKnownLocation.getLatitude());
						locationJson.put("longitude", lastKnownLocation.getLongitude());
					} else
					{
						locationJson.put("accuracy", 0);
						locationJson.put("latitude", 0);
						locationJson.put("longitude", 0);
					}
				} catch (JSONException e) {
					Log.i(TAG, "JSONException = " + e.getMessage());
				}
				
			} catch (SecurityException  e) {
				Log.i(TAG, "SecurityException = " + e.getMessage());
			}
		}
				
		return locationJson;
	}
	
	private static boolean getGPSStatus(Context context){
		
	   String allowedLocationProviders =
	   Settings.System.getString(context.getContentResolver(),
	   Settings.System.LOCATION_PROVIDERS_ALLOWED);

	   if (allowedLocationProviders == null) {
	      allowedLocationProviders = "";
	   }

	   return allowedLocationProviders.contains(LocationManager.GPS_PROVIDER);
	}
	
	private static String mostAccurateLocationProvider(Context context) {
		String best_provider = "gps";
		
		try{
			
			LocationManager locationManager = (LocationManager) context.getSystemService(Service.LOCATION_SERVICE);
			List<String> providers = locationManager.getAllProviders();

			if (providers.size() != 0)
			{
				best_provider = providers.get(0);
				Location best_provider_location = locationManager.getLastKnownLocation(best_provider);

				for (int i = 1; i < providers.size(); i++)
				{
					String current_provider = providers.get(i);
					Location current_provider_location = locationManager.getLastKnownLocation(current_provider);
					// if the last known location of the current provider is better
					// than the one of the best provider, the current provider is
					// considered the better now
					if (current_provider_location != null && best_provider_location != null)
					{
						if (current_provider_location.getAccuracy() > best_provider_location.getAccuracy())
						{
							best_provider = current_provider;
							best_provider_location = current_provider_location;
						}
					}
					// if there is no best provider and the current provider has a
					// last known location, the current provider is considered the
					// better now
					else if (current_provider_location != null && best_provider_location == null)
					{
						best_provider = current_provider;
						best_provider_location = current_provider_location;
					}

				}
			}
			
		}catch (SecurityException e) {
			// TODO: handle exception
			Log.i(TAG, "SecurityException = " + e.getMessage());
		}

		return best_provider;
	}
}
