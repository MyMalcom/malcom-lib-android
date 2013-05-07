package com.malcom.library.android.module.notifications;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;
import com.malcom.library.android.module.core.MCMCoreAdapter;
import com.malcom.library.android.module.notifications.gcm.MalcomNotificationReceiver;
import com.malcom.library.android.utils.ToolBox;


/**
 * Malcom Notification module.
 * 
 * - Registration. 
 * 			1.- Register the device for push with GCM.
 * 			2.- Register with Malcom.  
 * - Un-registration.  
 * - Notification receival.
 * - Notification ACKs
 * 			
 * 
 * @author Malcom Ventures, S.L.
 * @since  2012
 *
 */
public class MCMNotificationModule {

	public static final String TAG = "MCMNotification";
	
	private static MCMNotificationModule instance = null;
	
	public static final String CACHED_ACK_FILE_PREFIX = "ack_";
	
	public static final String notification = "v3/notification";
	public static final String notification_push = notification + "/push/";
	public static final String notification_ack = notification+ "/ack/";
	public static final String notification_registry = notification + "/registry/application";
	public static final String notification_deregister = notification + "/registry/application/appCode/device/udid";
	public static final String notification_deregister_param_appCode = "appCode";
	public static final String notification_deregister_param_udid = "udid";
	
	
	/** The key where the message of the notification is */
	public static final String ANDROID_MESSAGE_EFFICACY_KEY = "notificationId";
	public static final String ANDROID_MESSAGE_SEGMENT_KEY = "segmentId";
	public static final String ANDROID_MESSAGE_KEY = "msg";
	public static final String ANDROID_MESSAGE_RICHMEDIA_KEY = "web_url";
	
    /** Custom intent used to show the alert in the UI about a received push. */
    public static final String SHOW_NOTIFICATION_ACTION = "com.malcom.library.android.gcm.DISPLAY_MESSAGE";
	
	
	private AsyncTask<Void, Void, Void> mRegisterTask;

		
	//This will hold the C2DM device registration token.
	public String deviceToken =null;
	
	/**
     * Google API project id registered to use GCM.
     */
    public static String SENDER_ID = "";
    
    public static String APPLICATION_CODE = null;
    public static String APPLICATION_SECRETKEY = null;
    public static EnvironmentType ENVIRONMENT_TYPE = null;    
    
    public static String NOTIFICATION_TITLE = null;
    public static Class<?> NOTIFICATION_ACTIVITY_TO_CALL = null;
    
    public static String SERVER_UNREG_URL = null;
    
    public static Boolean SHOW_ALERT = true;
    
	
	protected MCMNotificationModule() {
		// Exists only to defeat instantiation.
	}	
	
	// Singleton
	public static MCMNotificationModule getInstance() {

		if (instance == null) {
			instance = new MCMNotificationModule();
		}
		return instance;
	}
	
	

	// NOTIFICATIONS
	
	//GCM	
	
	/**
	 * Registers the device with Google Cloud Messaging system (GCM).
	 * 
	 * NOTE: 
	 * The environment is set by looking for the application debug mode,
	 * if is set to TRUE, the environment will be SANDBOX, otherwise PRODUCTION.
	 * 
	 * @param 	context		Context.  
	 * @param	title		Title for the notification
	 * @param 	clazz		Class to call when clicking in the notification
	 */
	public void gcmRegisterDevice(Context context, String title, Boolean showAlert, Class<?> clazz){
		
		EnvironmentType environment = EnvironmentType.PRODUCTION;
		if(ToolBox.application_isAppInDebugMode(context)){
			environment = EnvironmentType.SANDBOX;
		}
		
		gcmRegisterDevice(context, environment, title, showAlert, clazz);
	}
	
	/**
	 * Registers the device with Google Cloud Messaging system (GCM)
	 * 
	 * @param 	context		Context.
	 * @param 	environment	Allows to set the environment type. 
	 * @param	title		Title for the notification
	 * @param 	clazz		Class to call when clicking in the notification
	 */
	public void gcmRegisterDevice(Context context, final EnvironmentType environment,
								  String title, Boolean showAlert, Class<?> clazz){
		
		//Initializes the required variables
		SENDER_ID = MCMCoreAdapter.getInstance().coreGetProperty(MCMCoreAdapter.PROPERTIES_MALCOM_GCM_SENDERID);
		
		//	Lo guardamos en memoria para el caso en que sea nulo
		
		final SharedPreferences prefs = context.getSharedPreferences( "GCM_SETTINGS", 0);
		
		if (SENDER_ID != null) {
				        
	        prefs.edit().putString("GCM_SENDER_ID", SENDER_ID).commit();
			
		}
		
		if (clazz != null) {
			
			String className = clazz.getCanonicalName();
	        
	        prefs.edit().putString("GCM_CLASS", className).commit();
			
		}
		
		if (title != null) {
	        
	        prefs.edit().putString("GCM_TITLE_NOTIFICATION", title).commit();
			
		}
		
		ENVIRONMENT_TYPE = environment;
		APPLICATION_CODE = MCMCoreAdapter.getInstance().coreGetProperty(MCMCoreAdapter.PROPERTIES_MALCOM_APPID);
		APPLICATION_SECRETKEY = MCMCoreAdapter.getInstance().coreGetProperty(MCMCoreAdapter.PROPERTIES_MALCOM_APPSECRETKEY);
		
		NOTIFICATION_TITLE = title;
		NOTIFICATION_ACTIVITY_TO_CALL = clazz;
		SHOW_ALERT = showAlert;
		
		// Verifies that the device supports GCM and throws an exception if it does not 
		//(for instance, if it is an emulator that does not contain the Google APIs)
        GCMRegistrar.checkDevice(context);
        // Makes sure the manifest was properly set       
        GCMRegistrar.checkManifest(context);
        
        final String regId = GCMRegistrar.getRegistrationId(context);
        if (regId==null || (regId!=null && regId.length()==0)) {            
            GCMRegistrar.register(context, SENDER_ID);            
        }/*else{
	        if(!GCMRegistrar.isRegisteredOnServer(context)){	        
		        
	        	final Context oContext = context;
		        mRegisterTask = new AsyncTask<Void, Void, Void>() {
		
		            @Override
		            protected Void doInBackground(Void... params) {
		                boolean registered = MalcomServerUtilities.register(oContext, regId, environment);
		                // At this point all attempts to register with the app
		                // server failed, so we need to unregister the device
		                // from GCM - the app will try to register again when
		                // it is restarted. Note that GCM will send an
		                // unregistered callback upon completion, but
		                // GCMIntentService.onUnregistered() will ignore it.
		                if (!registered) {
		                    GCMRegistrar.unregister(oContext);
		                }
		                return null;
		            }
		
		            @Override
		            protected void onPostExecute(Void result) {
		                mRegisterTask = null;
		            }
		
		        };
		        mRegisterTask.execute(null, null, null);
	        }
        }*/
	}
	
	
	/**
	 * Returns the registration token from GCM.
	 * 
	 * @param context
	 * @return	The token or null if the device is not registered.
	 */
	public String gcmGetRegistrationToken(Context context){
		if(GCMRegistrar.isRegistered(context))
			return GCMRegistrar.getRegistrationId(context);
		else
			return null;
	}
	
	/**
	 * Returns the Android device unique id.
	 * 
	 * @param context
	 * @return	The android device unique id.
	 */
	public String gcmGetDeviceUdid(Context context){
		return ToolBox.device_getId(context);
	}	
	
	/**
	 * Unregister the device from Google Cloud Messaging system (GCM)
	 * 
	 * @param context
	 */
	public void gcmUnregisterDevice(Context context){
		if(GCMRegistrar.isRegistered(context)){
			
			//Set the unregistration URL for later usage.
			String serverUrl = MCMCoreAdapter.getInstance().coreGetProperty(MCMCoreAdapter.PROPERTIES_MALCOM_BASEURL) + MCMNotificationModule.notification_deregister;
			serverUrl=serverUrl.replaceAll(MCMNotificationModule.notification_deregister_param_appCode, APPLICATION_CODE);
            serverUrl=serverUrl.replaceAll(MCMNotificationModule.notification_deregister_param_udid, ToolBox.device_getId(context));
			SERVER_UNREG_URL = serverUrl;
			
			//Un-register the device from GCM.
			GCMRegistrar.unregister(context);
		}
			
			
		if (mRegisterTask != null) {
			mRegisterTask.cancel(true);
	    }
	}
	
	/**
	 * Makes the UI to show the alert for any received notification.
	 * 
	 * @param context
	 * @param intent
	 */
	public void gcmCheckForNewNotification(Context context, Intent intent){
		if(intent.getExtras()!=null && intent.getExtras().getBoolean(MalcomNotificationReceiver.NOTIFICATION_SHOW_ALERT_KEY,new Boolean(false))){
			Log.d(MCMNotificationModule.TAG,"Notification received. Sending show order to broadcast.");
			
			String efficacyKey = (String)intent.getExtras().get(MCMNotificationModule.ANDROID_MESSAGE_EFFICACY_KEY);
			String segmentId = null;
			if(intent.getExtras().get(MCMNotificationModule.ANDROID_MESSAGE_SEGMENT_KEY)!=null)
				segmentId = (String)intent.getExtras().get(MCMNotificationModule.ANDROID_MESSAGE_SEGMENT_KEY);
			String message = (String)intent.getExtras().get(MCMNotificationModule.ANDROID_MESSAGE_KEY);
			try {
				message = URLDecoder.decode(message, "UTF8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	String richMediaUrl = (String)intent.getExtras().get(MCMNotificationModule.ANDROID_MESSAGE_RICHMEDIA_KEY);
	    	
	    	//Tells to the UI to show the alert for the notification (using the BroadcastReceiver "MalcomNotificationReceiver".
	        Intent intentOpenAlert = new Intent(MCMNotificationModule.SHOW_NOTIFICATION_ACTION);
	        intentOpenAlert.putExtra(MCMNotificationModule.ANDROID_MESSAGE_EFFICACY_KEY, efficacyKey);
	        if(segmentId!=null)
	        	intentOpenAlert.putExtra(MCMNotificationModule.ANDROID_MESSAGE_SEGMENT_KEY, segmentId);
	        intentOpenAlert.putExtra(MCMNotificationModule.ANDROID_MESSAGE_KEY, message);
	        intentOpenAlert.putExtra(MCMNotificationModule.ANDROID_MESSAGE_RICHMEDIA_KEY, richMediaUrl);
	        
	        context.sendBroadcast(intentOpenAlert);
		}
	}

}
