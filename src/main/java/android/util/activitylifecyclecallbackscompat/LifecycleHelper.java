package android.util.activitylifecyclecallbackscompat;

import android.app.Application;

/**
 * Registers and unregisters lifecycle callbacks
 */
public interface LifecycleHelper {

    void registerActivityLifecycleCallbacks(Application application, MalcomActivityLifecycleCallbacksCompat callback);

    void unregisterActivityLifecycleCallbacks(Application application, MalcomActivityLifecycleCallbacksCompat callback);
}
