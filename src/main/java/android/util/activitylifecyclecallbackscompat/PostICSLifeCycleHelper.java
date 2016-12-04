package android.util.activitylifecyclecallbackscompat;

import android.annotation.TargetApi;
import android.app.Application;
import android.os.Build;

/**
 * Implementation for Android versions after Ice Scream Sandwich
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class PostICSLifeCycleHelper implements LifecycleHelper {

    @Override
    public void registerActivityLifecycleCallbacks(Application application, MalcomActivityLifecycleCallbacksCompat callback) {
        application.registerActivityLifecycleCallbacks(new MalcomActivityLifecycleCallbacksWrapper(callback));
    }

    @Override
    public void unregisterActivityLifecycleCallbacks(Application application, MalcomActivityLifecycleCallbacksCompat callback) {
        application.unregisterActivityLifecycleCallbacks(new MalcomActivityLifecycleCallbacksWrapper(callback));
    }
}
