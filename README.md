# MusicPlayer (Echo)

Simple music player with an audio visualizer. Kotlin, support-library UI, `MediaStore` songs, shake-to-change-song.

## About this branch (`rebuild`)

The original upload held only the Kotlin sources and the manifest: no Gradle files and no `res/` folder, so it could not be built. This branch makes it buildable again.

- **Added:** the Gradle project (AGP 7.4.2, Gradle 7.6.4, Kotlin 1.7.22), and a `res/` folder recreated from what the code needs (11 layouts, 4 menus, strings, theme, colors, 17 vector drawables), checked against `demo/echo-original-demo.mp4`. The layouts are a re-creation, so they will not match the original pixel for pixel.
- **Moved:** sources into `app/src/main/java/com/example/yashwanth/echo/`, the manifest into `app/src/main/`, the demo video into `demo/`.
- **Changed in the code (14 lines):** nullability fixes the current Kotlin compiler requires (`?.isPlaying == true`, `?: 0`, `!!` on `Context` arguments, `Parcel` in `writeToParcel`). Behavior is unchanged.
- **Manifest:** `tools:replace="android:label"` (the visualizer library declares its own label) and a stray `>` removed.
- **Libraries:** `com.android.support:*:26.1.0` (the era the code was written for, so `android.useAndroidX=false`) and `com.cleveroad:audiovisualization:0.9.4`.
- **targetSdk 28** on purpose: the code creates `PendingIntent`s with flags `0`, which throws on targetSdk 31+. Newer phones still run it.

## Build and install

```sh
export JAVA_HOME=/path/to/jdk17 ANDROID_HOME=$HOME/Library/Android/sdk
./gradlew assembleDebug                 # app/build/outputs/apk/debug/app-debug.apk
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## Known limits

- Android 8+ ignores the "Now playing" notification: it is posted without a notification channel.
- Permission prompts follow the original code (storage, phone state, outgoing calls, microphone for the visualizer).
