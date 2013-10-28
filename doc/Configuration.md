#Configuration Module

This module lets you configure dynamic values for your app so you can change
them wihtout having to re-publish to Google Play. You can define alert messages,
a splash screen image and custom properties.

We'll explain what you need to do in [Malcom web](http://malcom.mymalcom.com)
and in your app to use this module.

##Configure your settings in [Malcom web](http://malcom.mymalcom.com)

In the "Marketing" tab, go to "Configuration" section on the left menu.
There you will be able to configure your alerts, splash screen image
and custom properties (in "advanced").

##Use the configuration in your app

###Initialization

Add this initialization call in the `onCreate` method of the activities that
are going to use the Malcom configuration:

```java
MalcomLib.loadConfiguration(this);
```

###Accessing properties

You can access the properties you defined (in the "Advanced" section) using
the following method:

```java
MalcomLib.getConfiguredProperty(<PROPERTY NAME>, DEFAULT_VALUE);
```

###Secondary splash screen

Copy and paste the following layout to the layout file of your activity. You might have to tweak it a little depending on your needs:

```xml
<LinearLayout
    android:id="@+id/splash_layout"
    android:layout_width="fill_parent"
    android:layout_height="fill_parent"
    android:orientation="vertical"
    android:visibility="gone"
    android:background="@android:color/black"
    android:gravity="center_vertical">
    <ImageView android:id="@+id/image_view"
        android:layout_width="fill_parent"
        android:layout_height="fill_parent"
        android:visibility="gone"/>
    <LinearLayout
        android:id="@+id/splash_progresszone"
        android:layout_width="fill_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:gravity="center_horizontal|center_vertical"
        android:visibility="gone">
        <ProgressBar
            android:id="@+id/splash_progress_bar"
            style="?android:attr/progressBarStyleSmall"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:padding="2dip"/>
        <TextView android:layout_height="wrap_content"
            android:layout_width="wrap_content"
            android:textColor="@android:color/white"
            android:text=""
            android:paddingLeft="4dp"/>
    </LinearLayout>
</LinearLayout>
```

##Best practices

###Default configuration

If a user launches your app for the first time when the device is offline the configuration you defined in [Malcom web](http://malcom.mymalcom.com) will not be retrieved. That's why it is recommendable to have a default configuration bundled inside your app.

To get a default configuration file go to this URL (replace `UUID` by the UUID
of your app), where you will see a JSON file with values that represent what you
configured in [Malcom web](http://malcom.mymalcom.com):

http://api.mymalcom.com/v1/globalconf/UUID/device/config.json

Download that file, place it in your `assets` folder and rename it to `mcmconfiginfo.json`.

###Default splash image

We recommend you to copy a default image file to your `assets` folder and
name it `splash.img`. That image will be used in case the device is offline
so it cannot access the image you configured in
[Malcom web](http://malcom.mymalcom.com).
