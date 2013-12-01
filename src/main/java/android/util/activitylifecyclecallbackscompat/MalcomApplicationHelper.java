package android.util.activitylifecyclecallbackscompat;

import android.app.Application;
import android.os.Build;

/**
 * Helper for using activity lifecycle callbacks also when running on API level < 14 (ICS).
 *
 * In API < 14, we simulate the lifecycle callbacks using a base class like MalcomActivity,
 * so activities should also extend that base activity (or similar).
 */
public class MalcomApplicationHelper {

    // Make up this constant to avoid using a constant that doesn't exist in API < 14
    private static final int ICE_CREAM_SANDWICH = 14; // Build.VERSION_CODES.ICE_CREAM_SANDWICH

    public static final boolean PRE_ICS = Integer.valueOf(Build.VERSION.SDK) < ICE_CREAM_SANDWICH;

    private static final LifecycleHelper lifecycleHelper;
    static {
        lifecycleHelper = PRE_ICS ? new PreICSLifecycleHelper() : new PostICSLifeCycleHelper();
    }

    /**
     * Registers a callback for activity lifecycle events.
     * 
     * @param application The application where the lifecycle will be registered
     * @param callback The callback to register
	 *
	 * @deprecated Do not use this method unless you know what you're doing. It's already called in MalcomLib.init().
     */
    public static void registerActivityLifecycleCallbacks(Application application, MalcomActivityLifecycleCallbacksCompat callback) {
        lifecycleHelper.registerActivityLifecycleCallbacks(application, callback);
    }

    /**
     * Unregisters a previously registered callback.
     * 
     * @param application The application where the lifecycle will be unregistered
     * @param callback The callback to unregister
     */
    public static void unregisterActivityLifecycleCallbacks(Application application, MalcomActivityLifecycleCallbacksCompat callback) {
        lifecycleHelper.unregisterActivityLifecycleCallbacks(application, callback);
    }
}
