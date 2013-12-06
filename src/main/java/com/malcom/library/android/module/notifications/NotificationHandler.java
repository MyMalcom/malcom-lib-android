package com.malcom.library.android.module.notifications;

import android.os.Bundle;

/**
 * Decides what to do with the notification that has been opened.
 */
public interface NotificationHandler {

    /**
     * Handles the notification.
     *
     * You can use the return value if you want to handle the notification in one activity, then
     * go to another activity and handle it again.
     *
     * @param message notification message
     * @param url     notification url (might be null if the notification doesn't have)
     * @param extras  extras carry custom parameters if applicable
     *
     * @return true if the notification has been handled (so it is not handled again)
     */
    boolean handleNotification(String message, String url, Bundle extras);
}
