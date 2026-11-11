# BekoHome

A small, **unofficial** Android TV / Google TV home-screen application designed for TV remotes and low-memory devices.

[🇹🇷 Türkçe rehber](README-TR.md) · [Changelog](CHANGELOG.md) · [v0.2.0 test plan](docs/TEST-PLAN-v0.2.0.md)

> **v0.2.0 beta prototype:** Builds in GitHub Actions and is ready for testing. **v0.2.0 has not yet been verified on physical TV.** The preceding v0.1.1 was tested successfully on a Beko Google UHD TV.
>
> BekoHome is not affiliated with, sponsored by, or endorsed by Beko or Google.

## Features

- Minimal, dark, distraction-free design with live date and time
- Responsive rows built for TV D-pad, OK, Back, and Home
- Auto-discovery of launchable TV applications (without native libraries)
- **Categories:** Favorites, Recently Opened, Film & TV, Music, Games, Tools, Other, All
- **Move apps between categories** using TV remote
- **Reorder apps** left or right within a category
- **Favorites** with a subtle star indicator
- All category assignments, favorites, and custom orders saved locally using `SharedPreferences`
- No ads, analytics, internet permission or tracking
- Does **not** disable the system Google TV launcher or register as a HOME handler

## Remote controls

| Action | Remote |
| --- | --- |
| Browse apps or categories | D-pad arrows |
| Launch an app | OK |
| Edit selected app | **Long-press OK** on app card, or focus **Düzenle** and press OK |
| Favorite/unfavorite | Edit menu → favorite |
| Move to another category | Edit menu → category |
| Rearrange app order | Edit menu → move left/right |
| Return to original Google TV screen | Back / Home |

In the app editor, category changes immediately display the destination category so you can see where the app went. **Recently Opened** reflects apps launched via BekoHome, not apps launched independently from Google TV.

## Hardware used for testing

Beko Google UHD TV · Android TV OS 14 / API 34 · ARM 32-bit / `armeabi-v7a` · approximately 1.74 GiB RAM · 4K display with 1080p system UI.

The app is written in Java, with no NDK/native code. Other Android TV models may behave differently.

## Build

JDK 17, Android SDK 34, Android Studio, and an internet connection for first-time Gradle setup are required.

```powershell
git clone https://github.com/erdinakgz/BekoHome.git
cd BekoHome
powershell -ExecutionPolicy Bypass -File .\SETUP-WINDOWS.ps1
.\gradlew.bat assembleDebug
```

Output: `app\build\outputs\apk\debug\app-debug.apk`

Or use **Actions → Android debug build → successful run → Artifacts → BekoHome-debug-apk**.

## Installation and rollback (wireless ADB)

Connect using the **current** Android TV Wireless debugging IP/connection port, not the pairing port:

```powershell
cd C:\platform-tools
.\adb.exe devices -l
.\adb.exe install -r "C:\BekoHome\app\build\outputs\apk\debug\app-debug.apk"
.\adb.exe shell am start -n com.erdinc.bekohome/.MainActivity
```

If you switch from local debug signing to GitHub Actions debug signing, you may see `INSTALL_FAILED_UPDATE_INCOMPATIBLE`. Uninstall **only** this app, then install the new APK:

```powershell
.\adb.exe uninstall com.erdinc.bekohome
.\adb.exe install "C:\BekoHome\app\build\outputs\apk\debug\app-debug.apk"
```

Uninstalling clears BekoHome's **locally saved favorites/categories/order**. It does not remove the original Google TV interface.

## Release process

- Every change gets a git commit and a [CHANGELOG](CHANGELOG.md) entry.
- Debug APKs are built with GitHub Actions; a green workflow is a **build check**, not proof of runtime correctness.
- Only tag and publish stable APKs after testing on real hardware.
- Public repository is currently **without a redistribution license**. Select an explicit license (e.g. MIT or Apache-2.0) before inviting community contributions.

## Roadmap

- **v0.2.0:** categories, app moves, favorites, local persistence, minimal redesign
- v0.2.x: remote-focus polish, layout edge cases, TV compatibility
- v0.3: optional backgrounds, appearance preferences
- v0.4: source/input shortcuts where supported by each model
- v1.0: fully tested stable release
