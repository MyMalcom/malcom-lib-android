package com.malcom.library.android.module.notifications;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

/**
 * Creates a dialog with the notification message and only an "Ok" button.
 * When the "Ok" button is pressed, the notification url will be opened in a browser
 * (if it's applicable).
 */
public class DefaultDialogNotificationHandler implements NotificationHandler {

    private final Context context;

    public DefaultDialogNotificationHandler(Context context) {
        this.context = context;
    }

    @Override
    public void handleNotification(String message, String url, Bundle extras) {

        try {
            createAlertDialog(message, url).show();

        } catch (Exception e) {
            Log.e(MCMNotificationModule.TAG,
                    "Could not show dialog for message '" + message + "' and url '" + url + "'", e);
        }
    }

    /**
     * Creates a dialog with the message and only an "Ok" button.
     * When the "Ok" button is pressed, the url will be opened on a browser (if it's not null).
     */
    private AlertDialog createAlertDialog(final String message, final String url) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message).setCancelable(false);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (url != null) {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                }
            }
        });

        return builder.create();
    }
}
