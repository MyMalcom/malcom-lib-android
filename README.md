Malcom Lib Android
==============

Integration
------------


Requirements
------------

- Malcom Android library requires a minimum version 2.2, Froyo (API Level 8).
- The device needs at least a configured account. In the simulator the AVD needs the Google APIs and a configured account.


Malcom Library integration
------------
The Malcom Library for Android is composed by 3 .jar files and some associated assets to the modules.
- "malcom-android-library-<version>.jar"
- The source code that allows debbuging "malcom-android-library-<version>-sources.jar"
- The javadoc, "malcom-android-library-<version>-javadoc.jar"

In order to integrate the library you have two options:
a) Add the "malcom-android-library-<version>.jar" in "libs" folder.
b) Add the 3 files described above.

Option a) is better if you don't want to debug the library, option b) allows you to do so. With option b) you'll need to setup the "malcom-android-library-<version>-sources.jar" into the build path in the project properties.



###Android Manifest 

In your Manifest.xml you'll need to add the following permissions:

        <uses-permission android:name="android.permission.INTERNET" />        <uses-permission android:name="android.permission.GET_ACCOUNTS" />        <uses-permission android:name="android.permission.READ_PHONE_STATE" />        <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />        <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />        <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />        <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />        <uses-permission android:name="android.permission.VIBRATE" />


Using the Modules
------------

###Initializing

Firstly, you'll need to init Malcom with the  method.

        MCMCoreAdapter.getInstance().initMalcom(this, UUID_MALCOM, SECRET_KEY);
        
where the UUID_MALCOM is the MalcomAppId and SECRET_KEY is the MalcomAppSecretKey both provided by the Malcom Web.


We can use these modules:

* [Configuration](https://github.com/MyMalcom/malcom-lib-android/wiki/Configuration)
* [Notifications](https://github.com/MyMalcom/malcom-lib-android/wiki/Notifications)
* [Stats](https://github.com/MyMalcom/malcom-lib-android/wiki/Stats)	
* [Ads](https://github.com/MyMalcom/malcom-lib-ios/wiki/Ads)	
* [Campaings](https://github.com/MyMalcom/malcom-lib-android/wiki/Campaing)



