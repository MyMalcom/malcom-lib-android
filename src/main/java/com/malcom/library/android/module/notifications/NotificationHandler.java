package com.malcom.library.android.module.notifications;

import android.os.Bundle;

/**
 * Decides what to do with the notification that has been opened.
 */
public interface NotificationHandler {

    /**
     * Handles the notification
     *
     * @param message notification message
     * @param url     notification url (might be null if the notification doesn't have)
     * @param extras  extras carry custom parameters if applicable
     */
    void handleNotification(String message, String url, Bundle extras);
}
