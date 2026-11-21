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

## Guideflow guide (this branch)

The app embeds the [Guideflow](https://github.com/yashwanth-Thota/universal-walkthrough) Android adapter: a "Guide me" button that walks the user through the real screen, one control at a time (spotlight, tooltip, Skip and Next). It only highlights; the user taps.

- **Flows** are in `flows/*.md`, written with `guideflow generate flow` and built with `guideflow scan .` into `.guideflow/kb.json` (git-ignored). Four flows: play and pause, add a song to favourites, turn on shake to change song, shuffle.
- **Icon-only buttons** got content descriptions ("Add to favourites", "Play or pause", ...) so the guide can find them. TalkBack reads them too.
- **Server** (from the Guideflow repo, on the Mac): `GUIDEFLOW_HOST=127.0.0.1 GUIDEFLOW_OFFLINE=1 PORT=8812 GUIDEFLOW_SOURCE_ROOT=<this folder> GUIDEFLOW_INGEST_ON_START= npm run start -w packages/server`. Use `127.0.0.1`, not `localhost`: the emulator's `10.0.2.2` reaches IPv4 only.
- **Build:** debug builds ask `http://10.0.2.2:8812` (the Mac, from the emulator). For a phone: `./gradlew assembleDebug -PguideflowUrl=http://<mac-ip>:8812`. Release builds have no server URL, so the button is not added.
- The module is included from `../universal-walkthrough/packages/android`, so both repos must sit side by side. This is the default, for local development.
- **Use the published artifact instead:** `./gradlew :guideflow-android:publishToMavenLocal` writes `com.guideflow:guideflow-android:0.2.0` to `~/.m2` (local only, nothing is uploaded). Then `./gradlew assembleDebug -PguideflowArtifact=com.guideflow:guideflow-android:0.2.0` drops the source module and resolves that artifact from `mavenLocal()` (limited to the `com.guideflow` group). Run the publish step without the property.
- Rotation keeps a guide in progress, and Back closes the guide card before it reaches the app. Details in the library's [README](../universal-walkthrough/packages/android/README.md).
