#Notifications Module

This module allows your app to receive notifications sent from Malcom via GCM (Google Cloud Messaging).

First, we will explain how to configure the module. Once it is configured, you can send notifications to the users of your app from the Malcom web.

##How to configure the module

###Create an application in the Google apis console

Go to of the [Google apis console](https://code.google.com/apis/console) and
create an application. In the "Services" section, activate the
"Google Cloud Messaging for Android". Then, in the "API Access" section,
create a new server key (leave the server IPs box empty).

Now you should have a key for server apps.
Check that it says "IPs: Any IP allowed". Copy the API key because you will
need to configure it in [Malcom web](http://malcom.mymalcom.com).

Login to [Malcom web](http://malcom.mymalcom.com), go to the "Administration"
section of your app and then go to "Push config". There you have to copy the
API key you generated before. Copy it in both text inputs (production and sandbox).

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

###Set the project number

In `onCreate` of your `Application` class, specify the project number that GCM has assigned to your app. You can find it in the "Overview" section of the [Google apis console](https://code.google.com/apis/console).

```java
MalcomLib.setSenderId(<PROJECT_NUMBER>);
```

###Register for notifications

In `onResume` of your main activity, call this method:

```java
MalcomLib.notificationsRegister(this, <Title>, <TargetActivity>.class);
```

That call will register the device in Malcom so it can receive notifications.

- `<Title>` is the title that will appear in all the notifications. You can set the name of your app or a generic message like "New message".
- `<TargetActivity>` is the activity that is going to be opened when the user clicks a notification. This will usually be the same main activity.

###Check notification

In `onResume` of your "TargetActivity" class (usually just after the previous register call), add this call to check for new notifications:

```java
MalcomLib.checkNotification(this);
```

That method will check whether the activity was opened because the user clicked on a notification. If so, the message of the notification will be displayed in an alert dialog.

If you want to handle the notification message in a custom way, use the following method:

```java
MalcomLib.checkNotification(this, handler);
```

###Other necessary calls

Override the `onNewIntent` method. This method is called when a notification is clicked and the user was already on the "TargetActivity". We set the intent so we don't lose the notification message that comes with it.

```java
@Override
protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    setIntent(intent);
}
```

Override the `onDestroy` method to free resources:

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

##How to send notifications

Login to [Malcom web](http://malcom.mymalcom.com), click on "Marketing" tab and then go to Campaigns - Push. There you can send notifications to your apps. Either to all users or to some of them using filters or segments.

You can also send notifications via API. Refer to the API documentation: https://github.com/MyMalcom/MalcomAPI
