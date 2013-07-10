package com.malcom.library.android.module.notifications.gcm;

import android.content.Context;

import com.google.android.gcm.GCMBroadcastReceiver;

/**
 * We create this class to be able to not use the default GCMIntentService name. 
 * 
 * By default this Intent-Service should be called "GCMIntentService" and located
 * in the user main application package. We do not want this so we override the
 * method taht return the name :).
 * 
 * @author Malcom Ventures, S.L.
 * @since  2012
 *
 */
public class MalcomGCMBroadcastReceiver extends GCMBroadcastReceiver {

	@Override
	protected String getGCMIntentServiceClassName(Context context) {
		
		return "com.malcom.library.android.module.notifications.gcm.GCMIntentService";
	}

}
