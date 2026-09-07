# AI Android Agent

A native Kotlin + Jetpack Compose Android agent foundation for voice-driven, permission-gated device assistance. It uses a closed loop: observe, plan, validate, execute, verify, and replan.

## Open and build

Open `android-agent` in Android Studio with a current Android Gradle Plugin toolchain. Run `./gradlew test`, `./gradlew lint`, and `./gradlew assembleDebug`; the debug APK is written to `app/build/outputs/apk/debug/app-debug.apk`. Release artifacts require your own signing configuration and must never store keystores or passwords in source.

## Required user setup

Enable AI Android Agent under Android Settings > Accessibility for UI inspection and gestures. Grant microphone permission for SpeechRecognizer. Screen vision must use the Android MediaProjection consent dialog; this project never captures silently. The default provider is an offline-safe provider; connect the app to your own authenticated HTTPS backend before adding a real Gemini implementation. Provider secrets must remain on that backend.

## Safety and limitations

Plans are typed JSON and unknown action types fail closed. Dangerous actions require confirmation, arbitrary shell/code execution is not supported, simulation mode previews actions without executing them, and the emergency stop cancels the coroutine loop. Accessibility behavior varies by app and Android version; third-party workflows must be discovered from visible semantics rather than hard-coded coordinates. This is a production-oriented foundation, not a bypass for Android security controls.

## GitHub Actions

The included workflow runs tests, lint, and assembles a debug APK as an artifact. Configure signing only through GitHub Secrets in a separate release workflow. See `backend/.env.example` for the future server contract.
