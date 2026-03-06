# Cosmonaut Worklog

This file is maintained as an interruption-safe engineering log so work can resume quickly.

## Scope

- Repository: `Cosmonaut`
- Branch: `feature/full-upgrade-optimization-html-pi`
- Platforms covered: Android, Desktop, HTML5/WebGL, Windows executable packaging
- Deployment target: Raspberry Pi (`cosmonaut.marcvidal.ca`)

## Completed Engineering Work

### 1. Build/Toolchain modernization

- Migrated Gradle wrapper to 8.10.2.
- Updated root/module Gradle scripts to modern plugin/dependency configuration.
- Standardized Java toolchain to Java 17 across modules.
- Updated Android build config to AGP 8+ style and SDK 35 target settings.

### 2. Android modernization

- Reworked `AndroidLauncher` ad flow to modern Google Mobile Ads integration patterns.
- Removed obsolete v3 in-app billing helper stack (`IabHelper` and related legacy files).
- Removed obsolete AIDL billing service source.
- Kept interstitial-centric behavior through resolver abstraction.

### 3. Desktop modernization

- Migrated desktop backend to LWJGL3 launcher.
- Updated desktop resolver implementation and build tasks.

### 4. HTML/WebGL modernization

- Added/fixed GWT module wiring and super-source handling.
- Introduced responsive centered portrait behavior in `HtmlLauncher` + CSS.
- Added web-specific loading path avoiding unsupported freetype runtime in GWT.
- Added supersource compatibility shim for `com.badlogic.gdx.utils.StringBuilder` used by transitive libs under GWT.

### 5. Input behavior (required split)

- Web mobile clients now use Android-style controls.
- Web desktop clients now use desktop keyboard controls (QWERTY/AZERTY locale-aware).

### 6. Performance and stability optimization

- Removed per-frame `addAction(...)` spam from multiple UI/control paths.
- Added one-time guards for transition/event registration.
- Replaced fragile class-name string checks with robust type/value checks.
- Improved map/render lifecycle handling and disposal safety.
- Normalized `delta` usage and stage act/update flow.
- Hardened level-data load/save handling.

### 7. Windows executable packaging

- Added Launch4j-based packaging flow.
- Added `:desktop:windowsBundle` output containing:
  - `Cosmonaut.exe`
  - fat desktop jar

## Build Verification Status

Last full verification command:

```bash
./gradlew :core:compileJava :android:assembleDebug :desktop:build :desktop:windowsBundle :html:dist
```

Result: **SUCCESS**

Generated key artifacts:

- Android debug APK: `android/build/outputs/apk/debug/`
- HTML dist: `html/build/dist/`
- Windows package zip: `desktop/build/distributions/cosmonaut-windows-2.0.0.zip`

Latest branch commit:

- `2905842` (`feature/full-upgrade-optimization-html-pi`)

## Raspberry Pi Deployment Tracking

Planned deploy model:

- Static files under `/var/www/cosmonaut`
- Local backend service via `busybox httpd` on `127.0.0.1:18083`
- Apache reverse proxy vhosts:
  - `cosmonaut.marcvidal.ca.conf`
  - `cosmonaut.marcvidal.ca-ssl.conf`

### Deployment execution log

- [x] Synced `html/build/dist` to Pi web root `/var/www/cosmonaut`.
- [x] Created and enabled `cosmonaut-static.service`.
- [x] Created and enabled Apache vhosts for `cosmonaut.marcvidal.ca`.
- [x] Reloaded Apache and verified routing/content.
- [x] Verified HTTP -> HTTPS redirect and HTTPS host-header response.

Exact validation results:

- Initial rollout conflict: `127.0.0.1:18082` was already used by Bubble backend (`/var/www/bubble`), causing `cosmonaut-static.service` restart failures.
- Resolution: moved Cosmonaut backend to `127.0.0.1:18083` and updated SSL ProxyPass target accordingly.
- Final service state: `systemctl is-active cosmonaut-static.service` -> `active`.
- HTTP check:
  - `curl -I -H 'Host: cosmonaut.marcvidal.ca' http://127.0.0.1/` -> `301 Moved Permanently` to HTTPS.
- HTTPS check (SNI-correct):
  - `curl -kI --resolve cosmonaut.marcvidal.ca:443:127.0.0.1 https://cosmonaut.marcvidal.ca/` -> `200 OK`.
- External domain check:
  - `curl -kI https://cosmonaut.marcvidal.ca` -> `200 OK`.
- Asset checks:
  - `/html/html.nocache.js` -> `200 OK`
  - `/assets/assets.txt` -> `200 OK`

TLS note:

- Existing cert in use (`nuage.marcvidal.ca`) does not include `cosmonaut.marcvidal.ca`, so strict TLS validation fails for that host.
- Attempted fix with Certbot certificate expansion failed at ACME HTTP-01 validation:
  - `Connection refused` on `http://cosmonaut.marcvidal.ca/.well-known/acme-challenge/...`
- Required infra fix to complete trusted TLS:
  - Ensure public TCP/80 for `cosmonaut.marcvidal.ca` reaches this Apache host.

## Known Risks / Notes

- Launch4j-generated `.exe` is unsigned (expected warning). Code-signing can be added in release hardening.
- Current Windows package requires Java 17+ present on target unless bundling a runtime in a future pass.
- HTML build is heavy due GWT permutation compile time; expected long step.

## Fast Resume Checklist

1. `git checkout feature/full-upgrade-optimization-html-pi`
2. `./gradlew :core:compileJava :android:assembleDebug :desktop:build :desktop:windowsBundle :html:dist`
3. Deploy `html/build/dist/` to Pi and run host-header `curl` checks.
4. Review this worklog section "Deployment execution log" for the next pending item.
