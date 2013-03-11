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




