package com.malcom.library.android.utils;

import android.app.Service;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.util.Log;

import com.malcom.library.android.MCMDefines;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by PedroDuran on 02/07/13.
 */
public class LocationUtils {

    public static String getDeviceCityLocation(Context context) {
        String res = "";
        Location lastKnownLocation = getLocation(context);

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

    public static Location getLocation(Context context) {
        Location lastKnownLocation = null;

        try{
            LocationManager locationManager = (LocationManager) context.getSystemService(Service.LOCATION_SERVICE);
            lastKnownLocation = locationManager.getLastKnownLocation(mostAccurateLocationProvider(context));

        } catch (SecurityException  e) {
            Log.i(MCMDefines.LOG_TAG, "SecurityException = " + e.getMessage());
        }

        return lastKnownLocation;
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
        String best_provider = LocationManager.GPS_PROVIDER;

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
            Log.i(MCMDefines.LOG_TAG, "SecurityException = " + e.getMessage());
        }

        return best_provider;
    }
}
