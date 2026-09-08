# AI Android Agent Workspace

This repository contains the complete AI Android Agent project, featuring a dual-architecture setup:

1. **Interactive Web Workspace & Emulator (Web Preview)**: A rich React/Next.js and Material 3 interface that simulates the AI Android Agent companion application, permissions gates, and automated device actions (clicks, keyboard input, swiping, planning) inside a high-fidelity visual Android emulator frame in real time.
2. **Native Android Client App (`/android-agent`)**: A native Kotlin application built with Jetpack Compose, Room database, and Accessibility services ready to be compiled into an Android APK or opened in Android Studio.

---

## 🛠️ Project Structure

- `/app/page.tsx`: The main interactive dashboard of the **AI Android Agent Workspace** simulator.
- `/android-agent`: The complete native Kotlin codebase, consisting of:
  - `app/src/main/java/com/example/aiandroidagent/MainActivity.kt`: The main entry screen.
  - `accessibility/AIAccessibilityService.kt`: Interfacing with system accessibility APIs.
  - `agent/AgentOrchestrator.kt`: Implementing the agent planner state machine.
  - `security/ActionPolicy.kt`: Defining safety validations (Safe, Confirmation Required, Blocked).
  - `data/AppDatabase.kt`: Room database persistence.

---

## 🚀 Getting Started

### 1. Web Simulator Workspace (Local Development)

First, install dependencies and start the development server:

```bash
npm install
npm run dev
```

Open [http://localhost:3000](http://localhost:3000) to access the interactive web emulator dashboard where you can type commands like `"Open YouTube and play lo-fi"` and watch the agent orchestrator dynamically plan and automate simulated clicks and gestures on the screen.

### 2. Native Android App (`/android-agent`)

Open the `/android-agent` directory in **Android Studio** to run, debug, or build the actual native Android APK:

- Supported Android OS: SDK 26+ (Android 8.0+)
- Targeted SDK: SDK 35 (Android 15)
- UI Library: Jetpack Compose
- Database: Room with KSP Compiler
