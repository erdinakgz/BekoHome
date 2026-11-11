# BekoHome

**A lightweight, remote-friendly Android TV / Google TV home-screen prototype.**

[🇹🇷 Türkçe kurulum ve kullanım rehberi](README-TR.md) · [Changelog](CHANGELOG.md)

> **Status:** v0.1.1 alpha — v0.1.0 was launched on a physical Beko TV and remote D-pad/OK/Back/Home checks passed. **v0.1.1 still needs on-device verification.**
>
> **Unofficial project.** Not affiliated with or endorsed by Beko, Google or any streaming-service provider.

## Highlights

- Designed for Android TV OS 14 (API 34) and low-memory TV devices
- TV-remote D-pad / OK navigation
- Lists launchable installed apps and opens them
- Live Turkish-language clock and date
- Minimal dark layout, without ads, analytics, or network permissions
- v0.1.1 compact height-responsive UI layout and app labels up to two lines
- Safe to test: **does not register as the system HOME launcher** and does not disable the stock Google TV launcher

## Compatibility / development

The prototype targets an **Android TV OS 14 Beko Google UHD TV**, ARM 32-bit (`armeabi-v7a`), with a 1080p UI on a 4K panel. The application uses Java only (no native libraries) and should also be adaptable to other Android TV devices.

Build requirements: Android Studio, JDK 17, Android SDK 34, Gradle 8.9 / Android Gradle Plugin 8.7.3.

### Windows

```powershell
git clone https://github.com/erdinakgz/BekoHome.git
cd BekoHome
powershell -ExecutionPolicy Bypass -File .\SETUP-WINDOWS.ps1
.\gradlew.bat assembleDebug
```

APK path: `app\build\outputs\apk\debug\app-debug.apk`

Alternatively open the repository directory in Android Studio and choose **Build → Build APK(s)**.

### Install / test (wireless ADB)

```powershell
.\adb.exe devices -l
.\adb.exe install -r "PATH_TO\app-debug.apk"
.\adb.exe shell am start -n com.erdinc.bekohome/.MainActivity
```

Use the actual ADB connection address/port shown on the TV; the pairing port is not the connection port. Never publicly share TV pairing codes.

### Remove

```powershell
.\adb.exe uninstall com.erdinc.bekohome
```

This leaves the original Google TV home screen intact.

**Note:** If the new debug APK is signed with a different key (e.g. local build versus CI), `adb install -r` can fail with `INSTALL_FAILED_UPDATE_INCOMPATIBLE`. Uninstall only `com.erdinc.bekohome` and reinstall; this clears the app’s data.

## CI / APK

A GitHub Actions workflow builds a debug APK on pushes and pull requests. After a successful run, open **Actions → Android debug build → Artifacts** for the APK. The workflow result is **not** proof of a successful physical-TV test.

## Roadmap

- v0.1.x: basic app launching, remote input, stability and packaging
- v0.2: favorites, app order, modernized UI
- v0.3: backgrounds and optional widgets
- v0.4: TV-source shortcuts where supported by the device
- v1.0: stable release after real-device testing

## Licensing

A redistribution license has not yet been selected by the repository owner. Being publicly visible on GitHub does **not** by itself grant permission to reuse or redistribute code. Choose a license (e.g. MIT or Apache-2.0) before inviting open-source contributions.

