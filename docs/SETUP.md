# TorNest setup and technical guide

Installation instructions, device requirements, playback troubleshooting, and build commands live here. For an introduction to the app, read the [TorNest README](../README.md). For catalogue authoring and project internals, see the [developer guide](DEVELOPMENT.md).

## Install TorNest

### Download the Android APK

Open the [TorNest repository releases](https://github.com/DavidSamir/TorNest/releases), read the release notes, and download the APK attached to the release you want to install. Existing release filenames still use the earlier Torfilx name.

The Android release workflow currently publishes `torfilx-release.apk`. Use the APK attached to a release, rather than a source-code ZIP. Check the release notes for version-specific compatibility and upgrade instructions.

### Install on Fire TV, Android TV, or Google TV

1. Confirm that your device runs Android or Android-based Fire OS and matches the [compatibility requirements](#device-compatibility).
2. Transfer the APK to the TV using your preferred sideloading method. Enable installation from the installer you use when your device asks for it.
3. Open the APK on the device and complete installation.
4. Launch TorNest. Review the sharing prompt before enabling peer-to-peer playback and catalogue updates.

For installation from a computer with Android Platform Tools and device debugging enabled:

```bash
adb connect <tv-ip-address>:5555
adb install -r torfilx-release.apk
```

Your TV may require a different debugging connection or pairing flow; use the address and instructions shown by the device. If Android refuses an update, check the release's signing and migration notes before uninstalling, because uninstalling removes local app data.

## Device compatibility

TorNest targets TVs and streaming devices that can run Android APKs. The current release configuration requires **Android 5.1 / API 22 or newer** and includes **32-bit ARM (`armeabi-v7a`) and 64-bit ARM (`arm64-v8a`)** native libraries.

| Device or platform | Compatibility scope |
| --- | --- |
| Android-based Amazon Fire TV / Fire TV Stick | Primary target. Check the device's actual Fire OS and Android version. |
| Android TV and Google TV | Intended targets when the device meets the Android version and ARM requirements. Individual models still need playback testing. |
| Android-based TV boxes without Google Play Services | Google Play Services is not required; device installation permissions and codec support still apply. |
| x86 / x86_64 Android emulators | Supported by the debug build; these ABIs are excluded from the release APK. |
| TVs without Android APK support | The Android APK cannot run directly on Roku OS, Samsung Tizen, LG webOS, Apple tvOS, or Amazon Vega OS. |

Minimum requirements do not guarantee playback on every device. Performance also depends on available peers, network conditions, video codecs, hardware decoding, and free storage. When reporting compatibility, include the exact device model, OS version, and TorNest release.

## How TorNest works

1. **Browse the built-in catalogue.** A new installation includes movie and show metadata, so browsing does not depend on first downloading a catalogue update.
2. **Choose a title or episode.** TorNest uses its catalogue's magnet sources to find peers and obtain torrent metadata.
3. **Play as pieces arrive.** The torrent engine prioritizes the selected video, and a local HTTP bridge supplies it to the player. Seeking can require more buffering.
4. **Keep your place.** Watch progress and saved titles remain on the device. Settings includes manual watch-data backup and restore.
5. **Update the catalogue.** With sharing enabled, TorNest can discover releases through the peer network and verify them before replacing the catalogue.

The catalogue contains metadata and source references, not bundled video files. A signed catalogue verifies its publisher and integrity; it does not guarantee that a source is reachable or that its content may be redistributed.

## Frequently asked questions

### Does TorNest need a media server?

No. TorNest streams from BitTorrent peers directly on the TV device. You do not need to run a separate media server on a computer or NAS.

### Can TorNest stream before a torrent finishes downloading?

Yes. TorNest prioritizes pieces of the selected video for playback while downloading continues. Startup time, seeking, and buffering depend on the source and reachable peers.

### Does TorNest work offline?

The bundled catalogue lets you browse metadata without fetching a catalogue update. Streaming video and receiving new catalogue releases require network access. TorNest is not designed as an offline video library.

### Why does a title appear but fail to play?

A catalogue entry can exist even when its source has no reachable peers, lacks a usable magnet, or contains video your device cannot decode. Check the connection and try another available source. Report persistent failures with the device model, Android or Fire OS version, app version, and exported logs.

### Does BitTorrent streaming upload data or expose my IP address?

Yes. While participating in a swarm, your device can upload pieces to other peers, and those peers can see its network IP address. TorNest asks for sharing consent before peer-to-peer operation. Review the sharing and storage controls in Settings.

### Can I open any magnet link in TorNest?

TorNest's viewing flow uses sources in its catalogue. It is not presented as a general-purpose magnet-link opener. Catalogue authors can add episode sources, season packs, and show packs using the [catalogue format in the developer guide](DEVELOPMENT.md#catalogue-format).

### How do I update TorNest and keep my watch progress?

Install a compatible release signed with the same key over the existing app. Follow the release notes and use the watch-data backup option before changing installations. Catalogue updates and APK updates are separate: a catalogue update does not update the app itself.

## Build from source

Use **JDK 21** (Android Studio's bundled JBR works), an Android SDK with **platform 36**, and Android Platform Tools. The Gradle wrapper manages the project's Gradle version.

Create `local.properties` in the repository root with your SDK path, for example:

```properties
sdk.dir=/path/to/Android/sdk
```

Build and install a debug APK:

```bash
./gradlew :app:assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.torfilx.tv.debug/com.torfilx.tv.MainActivity
```

On Windows, use `.\gradlew.bat` in place of `./gradlew`. The package identifiers retain the earlier project name, which is why the launch command contains `com.torfilx`.

Run unit tests separately:

```bash
./gradlew test
```

For catalogue authoring, module layout, and native torrent builds, read the [developer guide](DEVELOPMENT.md). Distribution builds require a persistent signing key so later APKs can update existing installations.

## Reporting a problem

Include your device model, OS version, TorNest version, affected title or episode, and what happened in a [GitHub issue](https://github.com/DavidSamir/TorNest/issues). Settings can export diagnostic logs; remove private details before posting them publicly.

## Storage and catalogue notes

The configurable cache budget removes older cached titles while protecting the video currently playing. Watch progress and saved titles are local; Settings includes manual backup and restore.

Use a persistent signing key for distribution builds so later APKs can update existing installations. Catalogue signatures verify the publisher and integrity, not source availability or redistribution rights. Catalogue publishers are responsible for checking the rights to the sources they distribute.
