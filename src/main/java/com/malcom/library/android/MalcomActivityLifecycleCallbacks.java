package com.malcom.library.android;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.util.activitylifecyclecallbackscompat.MalcomActivityLifecycleCallbacksCompat;

import com.malcom.library.android.module.core.MCMCoreAdapter;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by PedroDuran on 23/07/13.
 */
public class MalcomActivityLifecycleCallbacks implements MalcomActivityLifecycleCallbacksCompat {

//    private static int openActivities;
    private static AtomicInteger openActivities;
    private static AtomicInteger resumed;
    private static AtomicInteger stopped;

    // And add this public static function
    public static boolean isApplicationInForeground() {
        return resumed.get() > stopped.get();
    }

    public static boolean isApplicationClosing() {
        return (openActivities.get() == 0) && (resumed.get() == stopped.get());
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
        openActivities.getAndIncrement();
        resumed.getAndIncrement();
    }

    @Override
    public void onActivityPaused(Activity activity) {
        openActivities.getAndDecrement();
    }

    @Override
    public void onActivityStopped(Activity activity) {
        stopped.getAndIncrement();
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
