# AI Android Agent

A native Kotlin + Jetpack Compose Android agent foundation for voice-driven, permission-gated device assistance. It uses a closed loop: observe, plan, validate, execute, verify, and replan.

## Open and build

Open `android-agent` in Android Studio with a current Android Gradle Plugin toolchain. Run `./gradlew test`, `./gradlew lint`, and `./gradlew assembleDebug`; the debug APK is written to `app/build/outputs/apk/debug/app-debug.apk`. Release artifacts require your own signing configuration and must never store keystores or passwords in source.

## Required user setup

Enable AI Android Agent under Android Settings > Accessibility for UI inspection and gestures. Grant microphone permission for SpeechRecognizer. Screen vision must use the Android MediaProjection consent dialog; this project never captures silently. The default provider is an offline-safe provider; connect the app to your own authenticated HTTPS backend before adding a real Gemini implementation. Provider secrets must remain on that backend.

## Safety and limitations

Plans are typed JSON and unknown action types fail closed. Dangerous actions require confirmation, arbitrary shell/code execution is not supported, simulation mode previews actions without executing them, and the emergency stop cancels the coroutine loop. Accessibility behavior varies by app and Android version; third-party workflows must be discovered from visible semantics rather than hard-coded coordinates. This is a production-oriented foundation, not a bypass for Android security controls.

## GitHub Actions

The included workflow runs tests, lint, and assembles a debug APK as an artifact. It installs Gradle 8.9 and creates `gradlew` in CI, which makes this repository safe to upload from a phone even when wrapper binaries were not generated locally. Configure signing only through GitHub Secrets in a separate release workflow. See `backend/.env.example` for the future server contract.

## Build from an Android phone

1. Open GitHub in your phone browser and create a new repository.
2. Open the repository, choose **Add file > Upload files**, and upload the entire `android-agent` folder contents. Keep `.github/workflows/android-build.yml` in exactly that path.
3. Commit the files to the default branch. If GitHub cannot upload a folder, create folders in the web editor first, then upload each file while preserving its path.
4. Open the repository's **Actions** tab, choose **Android build**, and tap **Run workflow**. A push or pull request also starts it automatically.
5. Wait for the green check. Open the completed run, scroll to **Artifacts**, and download `app-debug`.
6. Extract the ZIP on your phone. The APK is `app-debug.apk`. Android may require enabling installation from your browser or file manager under **Install unknown apps**.

The workflow is the build authority: it installs JDK 17, Android platform 35, build tools 35.0.0, creates/validates the Gradle 8.9 wrapper, runs unit tests and lint, then uploads the APK. If it fails, open the failed step and copy the first error line; do not download an APK from a red run.

## APK types

- **Debug APK:** unsigned development build produced by `assembleDebug`; suitable for testing on a device.
- **Release APK:** optimized variant that still needs signing configuration.
- **Signed release APK:** installable distribution artifact signed with a keystore held in GitHub Secrets. Never commit a keystore, password, API key, or signing material.

## Platform setup after installation

Open the app, grant microphone permission, enable **AI Android Agent** in Android Settings > Accessibility, and approve the system MediaProjection dialog before using screen vision. These are explicit Android user-consent flows; GitHub Actions can compile the app but cannot grant them remotely.
