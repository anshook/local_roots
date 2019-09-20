<h2>Udacity Android Developer Nanodegree Program - Capstone project</h2>
Application for convenient access to local farmer markets. Search for farmer markets near device location or zip code, view market locations on map, add markets to favorites. Learn more about the market, its location, operating times and product offerings.

Disclaimer:
This product uses [USDA’s National Farmers Market API](https://search.ams.usda.gov/farmersmarkets/v1/svcdesc.html) but is not endorsed or certified by USDA.

<h5>Rubric</h5>
<h6>Common Project Requirements</h6>

  - App conforms to common standards found in the Android Nanodegree General Project Guidelines.
  - App is written solely in the Java Programming Language.
  - App utilizes stable release versions of all libraries, Gradle, and Android Studio.
  
<h6>Core Platform Development</h6>

  - App integrates a third-party library.
  - App validates all input from servers and users. If data does not exist or is in the wrong format, the app logs this fact    and does not crash.
  - App includes support for accessibility. That includes content descriptions, navigation using a D-pad, and, if applicable, non-audio versions of audio cues.
  - App keeps all strings in a strings.xml file and enables RTL layout switching on all layouts.
  - App provides a widget to provide relevant information to the user on the home screen.
  
<h6>Google Play Services</h6>

  - App integrates two or more Google services. Google service integrations can be a part of Google Play Services or Firebase.
  - Each service imported in the build.gradle is used in the app.
  - If Location is used, the app customizes the user’s experience by using the device's location.
  - If Maps is used, the map provides relevant information to the user.
  
<h6>Material Design</h6>

  - App theme extends AppCompat.
  - App uses an app bar and associated toolbars.
  - App uses standard and simple transitions between activities.

<h6>Building</h6>

  - App builds from a clean repository checkout with no additional configuration.
  - App builds and deploys using the installRelease Gradle task.
  - App is equipped with a signing configuration, and the keystore and passwords are included in the repository. Keystore is referred to by a relative path.
  - All app dependencies are managed by Gradle.
  
<h6>Data Persistence</h6>

  - App stores data locally using Room OR Firebase Realtime Database. No third party frameworks may be used.
  - App stores data locally either by implementing a ContentProvider OR using Firebase Realtime Database. No third party frameworks nor Room Persistence Library may be used.
  - If it regularly pulls or sends data to/from a web service or API, app updates data in its cache at regular intervals using a SyncAdapter or JobDispacter.
  - If Room is used then LiveData and ViewModel are used when required and no unnecessary calls to the database are made.
