#Notifications Module

This module allows your app to receive notifications sent from Malcom via GCM (Google Cloud Messaging).

##How to use the module

###Add the required permisions in the manifest

In AndroidManifest.xml, add the following (replace `<PACKAGE>` with your application package):

```xml
<permission android:name="<PACKAGE>.permission.C2D_MESSAGE"
  android:protectionLevel="signature" />
<uses-permission android:name="<PACKAGE>.permission.C2D_MESSAGE" />
<uses-permission android:name="com.google.android.c2dm.permission.RECEIVE" />
<uses-permission android:name="android.permission.WAKE_LOCK" />

<receiver android:name="com.malcom.library.android.module.notifications.gcm.MalcomGCMBroadcastReceiver"
    android:permission="com.google.android.c2dm.permission.SEND" >
    <intent-filter>
        <action android:name="com.google.android.c2dm.intent.RECEIVE" />
        <action android:name="com.google.android.c2dm.intent.REGISTRATION" />
        <category android:name="<PACKAGE>" />
    </intent-filter>
</receiver>

<service android:name="com.malcom.library.android.module.notifications.gcm.GCMIntentService" />
<service android:name="com.malcom.library.android.module.notifications.services.PendingAcksDeliveryService" />
```

###Set the sender ID

In `onCreate` of your `Application` class, specify the ID that GCM has assigned to your app:

```java
MalcomLib.setSenderId(<SENDER_ID>);
```

###Register for notifications

In `onResume` of your main activity, call this method:

```java
MalcomLib.notificationsRegister(this, <Title>, <MainActivity>.class);
```

That call will register the device in Malcom so it can receive notifications.

- The `<Title>` tag will be the title text that is going to appear in the Android tabbar once the notification is received.
- `<MainActivity>` is the main activity of your application (usually the same class where you put that call), which is going to be launched once the notification is opened.

###Check for notifications

Just after the previous register call, also in the `onResume` of your activity, add this call to check for new notifications:

```java
MalcomLib.checkForNewNotifications(this);
```

That method will check whether the app was opened because the user clicked on a notification. If so, the message of the notification will be displayed.

###Other necessary calls

Override the `onNewIntent` method:

```java
@Override
protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    setIntent(intent);
}
```

Override the `onDestroy` method:

```java
@Override
protected void onDestroy() {
    super.onDestroy();
    GCMRegistrar.onDestroy(getApplicationContext());
}
```

###Unregistering the device (optional)

In case you want to unregister the device so it doesn't receive notifications you can use the following method:

```java
MCMCoreAdapter.getInstance().moduleNotificationsUnregister(getApplicationContext());
```
