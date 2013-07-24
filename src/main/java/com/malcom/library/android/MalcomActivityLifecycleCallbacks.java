package com.malcom.library.android;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.util.activitylifecyclecallbackscompat.MalcomActivityLifecycleCallbacksCompat;

import com.malcom.library.android.module.core.MCMCoreAdapter;

/**
 * Created by PedroDuran on 23/07/13.
 */
public class MalcomActivityLifecycleCallbacks implements MalcomActivityLifecycleCallbacksCompat {

    private static int openActivities;
    private static int resumed;
    private static int stopped;

    // And add this public static function
    public static boolean isApplicationInForeground() {
        return resumed > stopped;
    }

    public static boolean isApplicationClosing() {
        return (openActivities == 0) && (resumed == stopped);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (!isApplicationInForeground()) {
            Log.d(MCMDefines.LOG_TAG, "Malcom session starts");
            MCMCoreAdapter.getInstance().moduleStatsStartBeacon(activity.getApplicationContext(),true);
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {
        openActivities++;
        resumed++;
    }

    @Override
    public void onActivityPaused(Activity activity) {
        openActivities--;
    }

    @Override
    public void onActivityStopped(Activity activity) {
        stopped++;
        if (isApplicationClosing()) {
            Log.d(MCMDefines.LOG_TAG, "Malcom session stops");
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
