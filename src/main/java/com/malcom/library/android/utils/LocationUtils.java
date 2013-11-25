package com.malcom.library.android.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.malcom.library.android.MCMDefines;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.Context.LOCATION_SERVICE;

public class LocationUtils {

	public static final String RECONSTRUCTION_PROVIDER = "reconstruction";

	private static final String PREFERENCES_NAME = "com.malcom.location";
	private static final String LATITUDE_KEY = "location.latitude";
	private static final String LONGITUDE_KEY = "location.longitude";

	public static String getDeviceCityLocation(Context context) {
        String res = "";
        Location lastKnownLocation = getLocation(context);

        //lastKnowLocation could be null, so instead of throw an NPE return an empty String
        if ( lastKnownLocation == null )
            return res;

        try {
            Geocoder gcd = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = gcd.getFromLocation(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), 1);
            if (addresses.size() > 0) {
                res = addresses.get(0).getLocality();
            }
        } catch (IOException e) {
            Log.e(MCMDefines.LOG_TAG,"Error getting city location: "+e.getMessage());
        } catch (NullPointerException npe) {
            Log.e(MCMDefines.LOG_TAG,"Error getting city location: "+npe.getMessage());
        }

        return res;
    }

    /**
     * Gets the most accurate {@link Location} from the enabled providers
	 * or tries to reconstruct it from latitude and longitude coordinates stored in shared preferences.
	 *
	 * In case the {@link Location} is reconstructed its provider will be {@link #RECONSTRUCTION_PROVIDER} and
	 * it will only have values for latitude and longitude. The other values will be defaults.
	 *
	 * Returns null if location is not available anywhere.
     */
    public static Location getLocation(Context context)
	{
		return findMostAccurateLastKnownLocation(context);
    }

	public static JSONObject getLocationJson(Context context) {

        JSONObject locationJson = new JSONObject();

        Location lastKnownLocation = getLocation(context);

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
            Log.i(MCMDefines.LOG_TAG, "JSONException = " + e.getMessage());
        }

        return locationJson;
    }

	/**
	 * Tries to find the most accurate 'lastKnownLocation', or returns null if none is found.
	 *
	 * Searches all providers for the most accurate 'lastKnownLocation' and, if not found,
	 * retrieves the last location retrieved (which is stored in the shared preferences).
	 */
    public static Location findMostAccurateLastKnownLocation(Context context)
    {
		LocationManager locationManager = (LocationManager) context.getSystemService( LOCATION_SERVICE );

		List<String> providers = locationManager.getProviders(true);

		Location best = null;

		for (String provider : providers)
		{
			Location current = locationManager.getLastKnownLocation(provider);
			if (current != null && (best == null || current.getAccuracy() > best.getAccuracy()))
				best = current;
		}

		if (best == null)
			best = getLocationFromSharedPreferences(context);
		else
			storeLocationInSharedPreferences(best, context);

		return best;
    }

	/**
	 * Tries to reconstruct the latest location from coordinates stored in the shared preferences.
	 * Returns null if the location coordinates are not found in the shared preferences.
	 */
	private static Location getLocationFromSharedPreferences(Context context)
	{
		final SharedPreferences prefs =
				context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);

		if (prefs.contains(LATITUDE_KEY) && prefs.contains(LONGITUDE_KEY))
		{
			double latitude = PreferencesUtils.getDouble(prefs, LATITUDE_KEY, 0L);
			double longitude = PreferencesUtils.getDouble(prefs, LONGITUDE_KEY, 0L);

			Location reconstructedLocation = new Location(RECONSTRUCTION_PROVIDER);
			reconstructedLocation.setLatitude(latitude);
			reconstructedLocation.setLongitude(longitude);

			return reconstructedLocation;

		} else {

			return null;
		}
	}

	private static void storeLocationInSharedPreferences(Location location, Context context) {

		final SharedPreferences prefs =
				context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);

		SharedPreferences.Editor editor = prefs.edit();
		PreferencesUtils.putDouble(editor, LATITUDE_KEY, location.getLatitude());
		PreferencesUtils.putDouble(editor, LONGITUDE_KEY, location.getLongitude());
		editor.commit();
	}
}
