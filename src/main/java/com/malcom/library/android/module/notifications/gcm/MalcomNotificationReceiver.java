package com.malcom.library.android.module.notifications.gcm;

import static com.malcom.library.android.module.notifications.MCMNotificationModule.applicationCode;
import static com.malcom.library.android.module.notifications.MCMNotificationModule.applicationSecretkey;
import static com.malcom.library.android.module.notifications.MCMNotificationModule.environmentType;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.malcom.library.android.module.notifications.MCMNotificationModule;
import com.malcom.library.android.module.notifications.NotificationHandler;


/**
 * Handles the notification, which is carried in the intent extras, using the provided handler
 * and also does other actions like decoding the message and sending an ack to the server.
 */
public class MalcomNotificationReceiver {

    private final Context context;
    private final Intent intent;
    private final NotificationHandler handler;

    public MalcomNotificationReceiver(Context context, Intent intent, NotificationHandler handler) {
        this.context = context;
        this.intent = intent;
        this.handler = handler;
    }

	public void handleNotification() {
		
        Log.d(MCMNotificationModule.TAG, "MalcomNotificationReceiver. A notification is going to be handled.");

        sendAck();

        String message = intent.getExtras().getString(MCMNotificationModule.ANDROID_MESSAGE_KEY);

        try {
            message = URLDecoder.decode(message, "UTF8");
        } catch (UnsupportedEncodingException e) {
            Log.w(MCMNotificationModule.TAG, "This message could not be decoded: " + message, e);
        }

        String richMediaUrl = intent.getExtras().getString(MCMNotificationModule.ANDROID_MESSAGE_RICHMEDIA_KEY);

        handler.handleNotification(message, richMediaUrl, intent.getExtras());
    }

    /** Sends ACK reponse to Malcom server */
    private void sendAck() {

        Log.i(MCMNotificationModule.TAG, "ACK.");

        final String notId = intent.getExtras().getString(MCMNotificationModule.ANDROID_MESSAGE_EFFICACY_KEY);
        final String segmentId = intent.getExtras().getString(MCMNotificationModule.ANDROID_MESSAGE_SEGMENT_KEY);
        final Context aContext = context;

        new Thread(new Runnable() {
            public void run() {
                try {
                    MalcomServerUtilities.doAck(aContext, notId, segmentId, environmentType.name(), applicationCode, applicationSecretkey);
                } catch (Exception e) {
                    Log.e(MCMNotificationModule.TAG, "Could to send ACK to Malcom for notification with ID: " + notId, e);
                }
            }
        }).start();
    }
}
