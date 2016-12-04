package android.util.activitylifecyclecallbackscompat;

import android.app.Application;

/**
 * Implementation for Android versions before Ice Scream Sandwich
 */
public class PreICSLifecycleHelper implements LifecycleHelper {

    @Override
    public void registerActivityLifecycleCallbacks(Application application, MalcomActivityLifecycleCallbacksCompat callback) {
        MalcomMainLifecycleDispatcher.get().registerActivityLifecycleCallbacks(callback);
    }

    @Override
    public void unregisterActivityLifecycleCallbacks(Application application, MalcomActivityLifecycleCallbacksCompat callback) {
        MalcomMainLifecycleDispatcher.get().unregisterActivityLifecycleCallbacks(callback);
    }
}
