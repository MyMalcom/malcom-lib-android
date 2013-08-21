Malcom Lib Android
==============

Integration
------------


###Requirements


- Malcom Android library requires a minimum version 2.2, Froyo (API Level 8).
- The device needs at least a configured account. In the simulator the AVD needs the Google APIs and a configured account.


###Malcom Library integration

* Maven is the recommended way to add MalcomLib to your project
 
 You have to configure the Malcom repository in your `pom.xml` to use it.

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
		<version>1.0.3</version>
	</dependency>
 </dependencies>
```

* Also you can add the library manually to your project. The Malcom Library for Android is composed by 3 .jar files.
 - ["malcom-android-library-&lt;version&gt;.jar"](http://maven-repo.mobivery.com.s3.amazonaws.com/release/com/malcom/library/android/malcom-android-library/1.0.3/malcom-android-library-1.0.3.jar)
 - The source code ["malcom-android-library-&lt;version&gt;-sources.jar"](http://maven-repo.mobivery.com.s3.amazonaws.com/release/com/malcom/library/android/malcom-android-library/1.0.3/malcom-android-library-1.0.3-sources.jar
) (allows debbuging)
 - The javadoc ["malcom-android-library-&lt;version&gt;-javadoc.jar"](http://maven-repo.mobivery.com.s3.amazonaws.com/release/com/malcom/library/android/malcom-android-library/1.0.3/malcom-android-library-1.0.3-javadoc.jar)

 In order to integrate the library you have two options:

 a) Add the "malcom-android-library-<version>.jar" in "libs" folder.
 
 b) Add the 3 files described above.


 Option a) is better if you don't want to debug the library, option b) allows you to do so. With option b) you'll need to setup the "malcom-android-library-<version>-sources.jar" into the build path in the project properties.



#####Android Manifest 

In your Manifest.xml you'll need to add the following permissions:
 ```xml
        <uses-permission android:name="android.permission.INTERNET" />
        <uses-permission android:name="android.permission.GET_ACCOUNTS" />
        <uses-permission android:name="android.permission.READ_PHONE_STATE" />
        <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
        <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
        <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
        <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
        <uses-permission android:name="android.permission.VIBRATE" />
        
        <!-- ..... -->
        <application>
        <!-- ..... -->
                <service android:name="com.malcom.library.android.module.stats.services.PendingBeaconsDeliveryService" />
        <!-- ..... -->
        </application>
```


Using the Modules
------------

###Initializing

Firstly, you'll need to init Malcom in your Application extended class, make this call inside the onCreate() method because is the entry point for each activity of your application:

        MCMCoreAdapter.getInstance().initMalcom(this, UUID_MALCOM, SECRET_KEY);
        
where the UUID_MALCOM is the MalcomAppId and SECRET_KEY is the MalcomAppSecretKey both provided by the Malcom Web.

Secondly you'll need to set the MalcomActivityLifecycleCallbacks to allow Malcom to know when your app is going to background or foreground. You'll need add this after the initMalcom call:

        MalcomApplicationHelper.registerActivityLifecycleCallbacks(this, new MalcomActivityLifecycleCallbacks());

Thirdly, if your application is targeted for Android version before 4 (API 14), you'll need all your activities extending `MalcomActivity.java`.
We have developed some Activity base classes to allow the use of `FragmentActivity`, `ListActivity` or `PreferenceActivity` but if you want to use other libraries (such ActionbarSherlock) that requires your activities extend too,
this is an example about how do that:

[Example for the extend of Activity (ActionBarSherlock library)](https://github.com/MyMalcom/malcom-lib-android/wiki/ActivityExtendExample)


Now you can use these modules:

* [Configuration](https://github.com/MyMalcom/malcom-lib-android/wiki/Configuration)
* [Notifications](https://github.com/MyMalcom/malcom-lib-android/wiki/Notifications)
* [Stats](https://github.com/MyMalcom/malcom-lib-android/wiki/Stats)	
* [Ads](https://github.com/MyMalcom/malcom-lib-android/wiki/Ads)
* [Campaigns](https://github.com/MyMalcom/malcom-lib-android/wiki/Campaign)



License
-------

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

