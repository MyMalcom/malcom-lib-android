#Malcom Android library

Here we will explain how to easily integrate the [Malcom](http://www.mymalcom.com) library in your Android app.


##Requirements

- Malcom Android library requires a `minSdkVersion` of 8 or greater (Android 2.2, Froyo).
- The device needs at least a configured account. In the simulator the AVD needs the Google APIs and a configured account.


##Dependencies

###With Maven

Add the Malcom repository and the dependency in your `pom.xml`.

In the repositories section:

```xml
<repositories>
	<repository>
		<id>Malcom Repository</id>
		<url>http://maven-repo.mobivery.com.s3.amazonaws.com/release</url>
	</repository>
</repositories>
```

In dependencies section:

```xml
<dependencies>
 	<dependency>
		<groupId>com.malcom.library.android</groupId>
		<artifactId>malcom-android-library</artifactId>
		<version>2.0.8</version>
	</dependency>
</dependencies>
```

###Without Maven

Manually download the library and add it to your project:

- [malcom-android-library-2.0.8.jar](http://maven-repo.mobivery.com.s3.amazonaws.com/release/com/malcom/library/android/malcom-android-library/2.0.8/malcom-android-library-2.0.8.jar)

You can also download the sources and javadoc:

- [malcom-android-library-2.0.8-sources.jar](http://maven-repo.mobivery.com.s3.amazonaws.com/release/com/malcom/library/android/malcom-android-library/2.0.8/malcom-android-library-2.0.8-sources.jar)

- [malcom-android-library-2.0.8-javadoc.jar](http://maven-repo.mobivery.com.s3.amazonaws.com/release/com/malcom/library/android/malcom-android-library/2.0.8/malcom-android-library-2.0.8-javadoc.jar)


##Android Manifest

You will need the following permissions to be able to use all the Malcom features:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.GET_ACCOUNTS" />
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.VIBRATE" />
```

Also add this service in your `<application>`:

```xml
<application ...>
    ...
    <service android:name="com.malcom.library.android.module.stats.services.PendingBeaconsDeliveryService"/>
    ...
</application>
```

##Get the UUID and secret key for your application

If you haven't done it yet, create an Application in Malcom web. Login to http://malcom.mymalcom.com/ (register if you haven't) and click "Create application".

After creating your application, click on it and go to "Administration" on the left menu. There you will see two values: "UUID" and "Secret key". You will need them for the next step.


##Init Malcom in your Application

Add these two lines in the `onCreate` method of your Application class:

```java
import com.malcom.library.android.MalcomLib;

public class YourApplication extends Application
{
  @Override
  public void onCreate()
  {
    MalcomLib.init(this, <UUID>, <SECRET_KEY>);

    ...
  }

  ...
}
```

##Extend "Malcom activities" (if you target pre-ICS)

If your application is targeted for Android version before ICS (API Level < 14) your activities must extend [MalcomActivity](src/main/java/android/util/activitylifecyclecallbackscompat/app/MalcomActivity.java), [MalcomListActivity](src/main/java/android/util/activitylifecyclecallbackscompat/app/MalcomListActivity.java) or [MalcomPreferenceActivity](src/main/java/android/util/activitylifecyclecallbackscompat/app/MalcomPreferenceActivity.java) instead of `Activity`, `ListActivity` or `PreferenceActivity`. For example:

```java
import android.util.activitylifecyclecallbackscompat.app.MalcomActivity;

public class MyActivity extends MalcomActivity {
    ...
}
```

If some of your activities should extend other classes like `FragmentActivity` or `SherlockFragmentActivity` you can easily define Malcom activities for those by copying [MalcomActivity](src/main/java/android/util/activitylifecyclecallbackscompat/app/MalcomActivity.java) with another name (e.g. `MalcomFragmentActivity`) and extend the class you need (e.g. `FragmentActivity`):

```java
public class MalcomFragmentActivity extends FragmentActivity
{
  // Same code as MalcomActivity
}
```

Then use that class in your activities:

```java
public class SomeFragmentActivity extends MalcomFragmentActivity
{
  ...
}
```

##Congratulations!

Now that you have integrated Malcom in your app you are using the basic features of Malcom.

To check that everything is OK do the following:

* Use your app on one or more mobile phones (enter, do something, exit)
* Go to http://malcom.mymalcom.com, click on your app and then on "Usage" section to check that your app is sending statistics to Malcom server (unique users, sessions, etc).

##Discover other Malcom features

To make the most of Malcom check out the following links. They explain more interesting features:

* [Configuration](doc/Configuration.md)
* [Notifications](doc/Notifications.md)
* [Stats](doc/Stats.md)
* [Campaigns](doc/Campaigns.md)

##Contact

If you have any question don't hesitate to contact us at [malcom@mymalcom.com](mailto:malcom@mymalcom.com).


##License

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
