package com.malcom.library.android;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.util.activitylifecyclecallbackscompat.ActivityLifecycleCallbacksCompat;

import com.malcom.library.android.module.core.MCMCoreAdapter;

/**
 * Created by PedroDuran on 23/07/13.
 */
public class MalcomActivityLifecycleCallbacks implements ActivityLifecycleCallbacksCompat {

    private static int openActivities;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (openActivities == 0) {
            //TODO: Pedro: StartBeacon
            Log.w("Pedro", "Se está arrancando la app - Se inicia la sesión");
            MCMCoreAdapter.getInstance().moduleStatsStartBeacon(activity.getApplicationContext(),true);
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {
        openActivities++;
    }

    @Override
    public void onActivityPaused(Activity activity) {
        openActivities--;
    }

    @Override
    public void onActivityStopped(Activity activity) {
        if (openActivities == 0) {
            //TODO: Pedro: EndBeacon
            Log.w("Pedro", "Se está cerrando la app - Se cierra la sesión");
            MCMCoreAdapter.getInstance().moduleStatsEndBeacon();
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
