# Cosmonaut

Modernized multi-platform libGDX game build of **Cosmonaut** with optimized runtime behavior for Android, desktop, HTML5/WebGL, and a packaged Windows executable.

## Targets

- Android APK (`:android:assembleDebug`)
- Desktop JVM (`:desktop:run`, `:desktop:build`)
- HTML5/WebGL (`:html:dist`)
- Windows bundle with native launcher (`:desktop:windowsBundle` -> `Cosmonaut.exe`)

## What Was Upgraded

- Build stack moved to modern Gradle + modern Android Gradle Plugin + Java 17 toolchain.
- libGDX stack updated to current major line (1.14.x).
- Android launcher modernized and legacy in-app billing helper code removed.
- Desktop launcher migrated to LWJGL3 backend.
- HTML launcher rebuilt with responsive centered landscape layout.
- Web controls split by client type:
  - Web mobile -> Android-style touch controls.
  - Web desktop -> desktop keyboard controls.
- Web startup input on mobile hardened by removing aggressive DOM-level touch/mouse preventDefault handlers that could swallow game input on some browsers.

## Performance and Stability Improvements

- Removed per-frame UI action allocations in hot loops.
- Prevented repeated transition/action registration spam.
- Fixed fragile class/string identity checks to robust type/value checks.
- Tightened lifecycle/disposal paths for renderer and map resources.
- Reduced unnecessary per-frame input mode churn.
- Added safer level persistence parsing and write helpers.
- Normalized delta usage in stage/render update flow.

## Controls Matrix

- Android: Android controls mode.
- Desktop native: keyboard controls (QWERTY/AZERTY by locale).
- HTML on desktop browser: desktop keyboard controls.
- HTML on mobile browser: Android controls.

## Prerequisites

- JDK 17+
- Android SDK (for Android target)
- Internet access for Gradle dependency resolution

`local.properties` should contain the Android SDK path, for example:

```properties
sdk.dir=/home/vdlmrc/ApprenticeSoft/android-sdk
```

## Build Commands

From repository root:

```bash
./gradlew :core:compileJava
./gradlew :android:assembleDebug
./gradlew :desktop:build
./gradlew :html:dist
```

## Windows Executable

Build a Windows package containing a native launcher and fat jar:

```bash
./gradlew :desktop:windowsBundle
```

Output archive:

- `desktop/build/distributions/cosmonaut-windows-2.0.0.zip`

Archive contents:

- `Cosmonaut/Cosmonaut.exe`
- `Cosmonaut/cosmonaut-desktop-2.0.0.jar`

Notes:

- The `.exe` launcher is produced with Launch4j.
- The local `desktop/build/windows-dist/Cosmonaut.exe` **does require Java 17** on the target machine.

## Self-Contained Windows Build (No Java Install Required)

A GitHub Actions workflow now builds a self-contained Windows package using `jpackage`:

- Workflow: `.github/workflows/windows-jpackage.yml`
- Main artifact: `cosmonaut-windows-self-contained` (zip)
- Optional artifact: `cosmonaut-windows-installer` (`.exe`, generated when WiX packaging succeeds)

`jpackage` artifacts embed a runtime, so players do **not** need to install Java 17 manually.

Use this for no-Java delivery:

- download artifact `cosmonaut-windows-self-contained` from the workflow run.
- do not use the Launch4j `windows-dist/Cosmonaut.exe` if Java is not installed.

## HTML Dist Output

Web files are generated at:

- `html/build/dist/`

This folder is deploy-ready static content.

## Raspberry Pi Deployment (Cosmonaut)

Target domain:

- `https://cosmonaut.marcvidal.ca`

Suggested deployment pattern:

1. Copy `html/build/dist/*` to a web root directory on the Pi.
2. Serve that directory from a local backend (for example `busybox httpd`).
3. Reverse-proxy the domain from Apache HTTPS vhost to the local backend.
4. Validate with host-header `curl` checks from the Pi.

## Verification Commands

```bash
./gradlew :core:compileJava :android:assembleDebug :desktop:build :desktop:windowsBundle :html:dist
```

## Web Smoke Testing (Desktop + Mobile + Pi)

Automated browser smoke suite is provided at `qa/web_smoke.js`.

Run:

```bash
cd qa
npm install
npx playwright install chromium
node web_smoke.js
```

The script validates local and deployed flows (desktop/mobile emulation) including:

- load -> home -> main menu
- options open/back
- upgrades open/back
- level selection
- level start + input action

Output summary:

- `qa/reports/summary.json`

## Project Tracking

Detailed change log, deployment state, and interruption-safe resume notes are maintained in:

- `docs/WORKLOG.md`
