package com.malcom.library.android.module.notifications.gcm;

import static com.malcom.library.android.module.notifications.MCMNotificationModule.applicationCode;
import static com.malcom.library.android.module.notifications.MCMNotificationModule.applicationSecretkey;
import static com.malcom.library.android.module.notifications.MCMNotificationModule.environmentType;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.malcom.library.android.module.notifications.MCMNotificationModule;


/**
 * Used to show the notification in the application UI.
 * 
 * @author Malcom Ventures, S.L.
 * @since  2012
 *
 */
public class MalcomNotificationReceiver extends BroadcastReceiver {

	public static final String NOTIFICATION_SHOW_ALERT_KEY = "openDialog";
	
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		if(intent.getAction().equals(MCMNotificationModule.SHOW_NOTIFICATION_ACTION)){
			Log.d(MCMNotificationModule.TAG,"MalcomNotificationReceiver. A received notification is going to be shown.");
			String message = intent.getExtras().getString(MCMNotificationModule.ANDROID_MESSAGE_KEY);
			
			try {
				message = URLDecoder.decode(message, "UTF8");
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
	        String richMediaUrl = intent.getExtras().getString(MCMNotificationModule.ANDROID_MESSAGE_RICHMEDIA_KEY);
			
	        if (MCMNotificationModule.showAlert) {

                try {
                    createAlertDialog(context, message, richMediaUrl).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
	        
	        }
	        else {
	        	
	        	if(richMediaUrl!=null){
		               //Open a browser.
		        	   Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(richMediaUrl));
		        	   context.startActivity(browserIntent);
		        	   richMediaUrl = null;
	        	}
	        	
	        }
	        
	        //Thread to make the ACK in Malcom.
	        Log.i(MCMNotificationModule.TAG, "ACK.");
	        final String notId = intent.getExtras().getString(MCMNotificationModule.ANDROID_MESSAGE_EFFICACY_KEY);
	        final String segmentId = intent.getExtras().getString(MCMNotificationModule.ANDROID_MESSAGE_SEGMENT_KEY);
	        final Context aContext = context;
	        Thread t = new Thread(new Runnable() {				
				public void run() {
                    try {
                        MalcomServerUtilities.doAck(aContext, notId, segmentId, environmentType.name(), applicationCode, applicationSecretkey);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
					
				}
			});
	        t.start();
		}
	}

	
	/*
	 * Creates the alert dialog with the notification data.
	 * 
	 * @param context
	 * @param message
	 * @param richMediaUrl
	 * @return
	 */
	private static AlertDialog createAlertDialog(final Context context, final String message, final String richMediaUrl){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(message).setCancelable(false);
		
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		          public void onClick(DialogInterface dialog, int id) {
		        	  dialog.cancel();	
		        	  if(richMediaUrl!=null){
			               //Open a browser
			        	   Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(richMediaUrl));
			        	   context.startActivity(browserIntent);

                          //Dismiss the dialog
                          dialog.dismiss();
		        	  }
		        	  
		           }
		       });
		      
		       
		return builder.create();
	}
	
}
