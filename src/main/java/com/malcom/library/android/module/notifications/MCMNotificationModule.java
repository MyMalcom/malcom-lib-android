package com.malcom.library.android.module.notifications;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
    public static final String ANDROID_NOTIFICATION_SOUND_KEY = "sound";
	
    /** Custom intent used to show the alert in the UI about a received push. */
    public static final String SHOW_NOTIFICATION_ACTION = "com.malcom.library.android.gcm.DISPLAY_MESSAGE";

    /** Notification parameters */
    public static final String GCM_SENDER_ID ="GCM_SENDER_ID";
    public static final String GCM_CLASS ="GCM_CLASS";
    public static final String GCM_TITLE_NOTIFICATION ="GCM_TITLE_NOTIFICATION";

    /** Intent extra that indicates if the intent carries a notification to be handled  */
    public static final String HAS_NOTIFICATION_TO_HANDLE = "hasNotificationToHandle";


    /**
     * Google API project id registered to use GCM.
     */
    public static String senderId = "";
    public static String applicationCode = null;
    public static String applicationSecretkey = null;
    public static EnvironmentType environmentType = null;

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
	public void gcmRegisterDevice(Context context, String title, Class<?> clazz) {
		
		EnvironmentType environment = EnvironmentType.PRODUCTION;
		if(ToolBox.application_isAppInDebugMode(context)){
			environment = EnvironmentType.SANDBOX;
		}
		
		gcmRegisterDevice(context, environment, title, clazz);
	}
	
	/**
	 * Registers the device with Google Cloud Messaging system (GCM)
	 * 
	 * @param 	context		Context.
	 * @param 	environment	Allows to set the environment type. 
	 * @param	title		Title for the notification
	 * @param 	clazz		Class to call when clicking in the notification
	 */
	public void gcmRegisterDevice(Context context, final EnvironmentType environment, String title, Class<?> clazz) {

		//Initializes the required variables
        senderId = MCMCoreAdapter.getInstance().coreGetProperty(MCMCoreAdapter.PROPERTIES_MALCOM_GCM_SENDERID);

        final SharedPreferences.Editor prefsEditor = context.getSharedPreferences( "GCM_SETTINGS", 0).edit();
		
		if (senderId != null) {
            prefsEditor.putString(GCM_SENDER_ID, senderId);
		}
		
		if (clazz != null) {
			String className = clazz.getCanonicalName();
            prefsEditor.putString(GCM_CLASS, className);
		}
		
		if (title != null) {
            prefsEditor.putString(GCM_TITLE_NOTIFICATION, title);
		}

        prefsEditor.commit();

        environmentType = environment;

		applicationCode = MCMCoreAdapter.getInstance().coreGetProperty(MCMCoreAdapter.PROPERTIES_MALCOM_APPID);
		applicationSecretkey = MCMCoreAdapter.getInstance().coreGetProperty(MCMCoreAdapter.PROPERTIES_MALCOM_APPSECRETKEY);
		
		// Verifies that the device supports GCM and throws an exception if it does not 
		//(for instance, if it is an emulator that does not contain the Google APIs)
        GCMRegistrar.checkDevice(context);
        // Makes sure the manifest was properly set       
        GCMRegistrar.checkManifest(context);

        // Force the register in the GCMService to avoid MissmatchedSenderId when the registerId is updated
        GCMRegistrar.register(context, senderId);

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

            if (applicationCode == null)
                applicationCode = MCMCoreAdapter.getInstance().coreGetProperty(MCMCoreAdapter.PROPERTIES_MALCOM_APPID);
            if (applicationSecretkey == null)
                applicationSecretkey = MCMCoreAdapter.getInstance().coreGetProperty(MCMCoreAdapter.PROPERTIES_MALCOM_APPSECRETKEY);
			
			//Set the unregistration URL for later usage.
			String serverUrl = MCMCoreAdapter.getInstance().coreGetProperty(MCMCoreAdapter.PROPERTIES_MALCOM_BASEURL) + MCMNotificationModule.notification_deregister;
			serverUrl=serverUrl.replaceAll(MCMNotificationModule.notification_deregister_param_appCode, applicationCode);
            serverUrl=serverUrl.replaceAll(MCMNotificationModule.notification_deregister_param_udid, ToolBox.device_getId(context));

			//Un-register the device from GCM.
			GCMRegistrar.unregister(context);
		}
	}

	/**
	 * If the activity intent comes from a notification a dialog is shown with the notification message.
	 */
	public void gcmCheckForNewNotification(Activity activity, NotificationHandler handler)
	{
		Intent intent = activity.getIntent();

		if ( hasNotificationToHandle(intent) )
		{
			Log.d(MCMNotificationModule.TAG,"Notification received. Displaying dialog.");

			// So the notification is not handled again
			intent.putExtra(HAS_NOTIFICATION_TO_HANDLE, false);

			// Trick to reuse receiver code until we properly refactor
			new MalcomNotificationReceiver(activity, intent, handler).handleNotification();
		}
	}

    private boolean hasNotificationToHandle(Intent intent) {
        return intent.getExtras() != null && intent.getExtras().getBoolean(HAS_NOTIFICATION_TO_HANDLE, false);
    }

}
