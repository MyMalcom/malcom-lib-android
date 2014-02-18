package android.util.activitylifecyclecallbackscompat.app;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;

import android.util.activitylifecyclecallbackscompat.MalcomApplicationHelper;
import android.util.activitylifecyclecallbackscompat.MalcomMainLifecycleDispatcher;

/**
 * Extension of {@link Activity} that dispatches its life cycle calls to registered listeners.
 */
public class MalcomListActivity extends ListActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (MalcomApplicationHelper.PRE_ICS) MalcomMainLifecycleDispatcher.get().onActivityCreated(this, savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (MalcomApplicationHelper.PRE_ICS) MalcomMainLifecycleDispatcher.get().onActivityStarted(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (MalcomApplicationHelper.PRE_ICS) MalcomMainLifecycleDispatcher.get().onActivityResumed(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (MalcomApplicationHelper.PRE_ICS) MalcomMainLifecycleDispatcher.get().onActivityPaused(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (MalcomApplicationHelper.PRE_ICS) MalcomMainLifecycleDispatcher.get().onActivityStopped(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (MalcomApplicationHelper.PRE_ICS) MalcomMainLifecycleDispatcher.get().onActivitySaveInstanceState(this, outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (MalcomApplicationHelper.PRE_ICS) MalcomMainLifecycleDispatcher.get().onActivityDestroyed(this);
    }
}
