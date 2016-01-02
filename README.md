# SettingsManager
A library that interfaces with SharedPreferences and makes it easy to store data across devices including Android Wear syncing

I've found that, for saving simple data, the SharedPreferences class is great. This library wraps SharedPreferences to easily 
integrate that with Android Wear through automatic syncing, Google Drive, and Google Play Games.

## Gradle
This will be available on Gradle soon.

## SettingsManager

### Constructor
`new SettingsManager(Context)`

### Methods
`setInt(int resId, int value)` or `setInt(String key, int value)`

You can set a value by passing the resource id of your key or the key directly, along with the value you want saved.

`getInt(int resId)` or `getInt(String key)`

You can get an integer value by passing the resource id of your key or the key directly.

These methods also work for `Boolean`, `String`, and `Long`.

### Google Play Games - Cloud Saves
Cloud saves require you to write and read byte arrays. It's easy to turn your SharedPreferences into a JSONObject and then to a byte 
array by calling:

`writeSnapshotToBytes()` 

And you can read them by calling:

`readSnapshotAsBytes(byte[] array)`

You should read the documentation on cloud saves to learn more about their implementation. This library just makes it easy to store 
and read data. 

### Android Wear
To use Android Wear, use the `WearSettingsManager` class by including the following library:

`TBA`

The `WearSettingsManager` is an extension of the `SettingsManager` that, when a value is changed, syncs all of the data. This works
both ways, if data is changed on the phone or on the watch. 

You will need to add a receiver in both apps that will run when data is received on receiving device:

`TBA`

And your manifest will need to be updated:

`TBA`

You can manually reset data to another device by calling:

`pushData()`
