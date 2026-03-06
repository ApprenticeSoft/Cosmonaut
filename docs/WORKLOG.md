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
- Added GitHub Actions Windows `jpackage` workflow for self-contained runtime builds:
  - `.github/workflows/windows-jpackage.yml`
  - artifact `cosmonaut-windows-self-contained` (no Java install required)

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

- Use `git rev-parse --short HEAD` on `feature/full-upgrade-optimization-html-pi` to get the current head.

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

## 2026-03-06 Web Black-Screen Incident

Symptom reported:

- Web loading bar reached 100% then screen stayed black on desktop and mobile.

Root causes found:

- `LevelHandler` used `Gdx.files.local(...)`, which is unsupported on GWT/WebGL and caused runtime failure.
- Multiple screen/control branches were tied only to `ApplicationType.Desktop/Android`, leaving WebGL paths partially uninitialized.

Fixes applied:

- `LevelHandler` now uses browser `Preferences` storage on WebGL and local file storage on native targets.
- Web-safe control-path updates:
  - `HomeScreen` prompt selection based on actual control mode.
  - `HUD` pause/escape prompt selection based on control mode.
  - `OptionScreen` control-screen routing based on selected controls (not app type).
  - `IntroScreen` and `TutorialScreen` touch-control asset logic based on control mode.

QA automation added:

- `qa/web_smoke.js` Playwright smoke runner for:
  - local desktop + local mobile emulation
  - deployed Pi desktop + deployed Pi mobile emulation
- Flow coverage per scenario:
  - load -> home -> main menu
  - options open/back
  - upgrades open/back
  - level selection open
  - level start + gameplay input action
- Runtime failure checks:
  - browser `pageerror`
  - significant console errors

Latest QA result:

- All four smoke scenarios passed.
- Summary artifact (local workspace): `qa/reports/summary.json`

## 2026-03-06 Mobile Web Orientation/Touch Follow-up

New user-reported symptoms:

- On mobile web after loading: black screen with `Touchez l'Écran`, touching did not progress.
- Live site appeared in portrait-like layout for a game that should run in landscape.
- Windows note: local `windows-dist/Cosmonaut.exe` requested Java 17.

Root causes identified:

- Live Pi deployment was still serving an older HTML build using portrait ratio behavior.
- DOM event prevention in `html/webapp/index.html` was overly aggressive (`mousedown`, `mouseup`, `touchstart`, `touchmove`), which could interfere with touch handling on some mobile browsers.

Fixes applied:

- `HtmlLauncher` ratio logic switched to centered landscape (`16:9`) viewport sizing.
- Removed aggressive pointer/touch `preventDefault` listeners; kept only context-menu suppression.
- README clarified Windows packaging expectations:
  - `desktop/build/windows-dist/Cosmonaut.exe` requires Java 17.
  - No-Java delivery must use the GitHub Actions `jpackage` artifact (`cosmonaut-windows-self-contained`).

Deployment and verification:

- Rebuilt web: `./gradlew :core:compileJava :html:dist` -> SUCCESS.
- Deployed fresh `html/build/dist` to Pi web root `/var/www/cosmonaut`.
- Restarted backend: `cosmonaut-static.service` -> `active`.
- Landscape verification (mobile emulation):
  - before deploy on live domain: canvas `320x658` (portrait full-height behavior),
  - after deploy on live domain: canvas `320x180` (centered landscape behavior).
- Full smoke suite rerun passed:
  - local desktop PASS
  - local mobile PASS
  - rpi desktop PASS
  - rpi mobile PASS

## 2026-03-06 Mobile Startup Regression + Local Windows Self-Contained Request

New user-reported issues:

- Mobile web stayed on black startup screen with `Touchez l'Écran` after tapping.
- User needs a Windows package that is self-contained and shareable from Google Drive, without requiring GitHub artifacts.
- User requested "YouTube-like" immersive mobile behavior (hide browser UI when possible).

Findings:

- Startup input transition in `HomeScreen` used `Gdx.input.isTouched()`, which can miss short taps on some mobile browsers.
- Existing smoke script passed false positives because it checked non-black pixels, not real screen transitions.
- Existing local `windows-dist/Cosmonaut.exe` is Launch4j wrapper requiring external Java.

Fixes implemented:

- `HomeScreen` startup transition now uses `Gdx.input.justTouched()` for reliable tap detection.
- Web shell (`index.html`) now requests immersive fullscreen and landscape lock on first user interaction (best-effort, browser-permission dependent).
- Added local no-GitHub Windows self-contained packaging path in desktop Gradle:
  - `:desktop:windowsPortableBundle`
  - downloads Windows JRE 17 runtime zip (Adoptium API by default),
  - embeds runtime in bundle (`runtime/`),
  - builds `Cosmonaut.exe` configured to use bundled runtime path.

Verification follow-up:

- `:desktop:windowsPortableBundle` completed and validated in subsequent pass.
- Web smoke was tightened with screenshot-region checks and rerun after redeploy (all scenarios pass).

## 2026-03-06 Mobile HTML Stabilization + 1080p Desktop Base Resolution

User request in this pass:

- finish mobile HTML fixes,
- keep deployed Pi web build aligned with latest fixes,
- set desktop portable/base desktop resolution to 1080p.

Implemented and verified:

- Confirmed desktop launcher base resolution is now `1920x1080`:
  - `desktop/src/com/cosmonaut/desktop/DesktopLauncher.java`
- Rebuilt and redeployed HTML bundle to Pi:
  - build: `./gradlew :core:compileJava :html:dist`
  - deploy target: `/var/www/cosmonaut`
  - backend restart: `cosmonaut-static.service` -> `active`
- Hardened WebGL/mobile stability path already in branch was validated:
  - shared-batch `Stage` usage on WebGL screens,
  - startup transition via `justTouched()`,
  - immersive request retained as best-effort user-activation flow.
- Tightened web smoke QA script to avoid false positives:
  - handles first interaction consumed by immersive request,
  - validates menu/options/play transitions using region-brightness checks (not only coarse non-black checks),
  - updated flow to match current web menu layout (no Upgrades button in WebGL fallback).

Final validation results (after deploy):

- `node qa/web_smoke.js`
  - `local-desktop` PASS
  - `local-mobile` PASS
  - `rpi-desktop` PASS
  - `rpi-mobile` PASS
- `./gradlew :core:compileJava :desktop:build :android:assembleDebug :html:dist` -> **SUCCESS**

## 2026-03-06 HTML Asset/Scaling Pass (Desktop + Mobile)

User-reported regressions in this pass:

- Main menu/intro visuals missing or black on HTML.
- Desktop web button text appeared too small.
- Mobile web did not reliably enter fullscreen and landscape.

Changes implemented:

- Web texture safety + intro rendering path (already present in branch) was kept and validated:
  - screen backgrounds now consistently loaded via web-safe texture parameters,
  - intro shader path disabled on WebGL with default batch rendering fallback,
  - intro stage projection matrix is explicitly applied every frame on WebGL.
- Increased WebGL fallback UI font sizing in HTML supersource `LoadingScreen`:
  - added desktop/mobile-specific scale boosts for `fontTable`, `fontHUD`, `fontUpgrade`, `fontDialogue`, `fontOption`.
  - purpose: align button/label readability with portable desktop presentation.
- Hardened mobile immersive behavior in `html/webapp/index.html`:
  - fullscreen + landscape requests now retry on subsequent interactions/focus/resize/orientation events (not one-shot).
  - preserves browser-policy compatibility by keeping it user-gesture driven.
- Updated `html/webapp/styles.css` for stable full-viewport embedding:
  - fixed root container to viewport with `100vw/100vh` and `100dvh`,
  - ensured canvas remains centered and bounded within the viewport.

QA evidence captured:

- Manual desktop/mobile menu/options screenshots:
  - `qa/reports/manual-check5/desktop_02_menu.png`
  - `qa/reports/manual-check5/desktop_03_options.png`
  - `qa/reports/manual-check5/mobile_02_menu.png`
  - `qa/reports/manual-check5/mobile_03_options.png`
- Intro animation asset verification (desktop + mobile):
  - `qa/reports/intro_probe_verification/desktop_intro_t1.png`
  - `qa/reports/intro_probe_verification/desktop_intro_t2.png`
  - `qa/reports/intro_probe_verification/mobile_intro_t1.png`
  - `qa/reports/intro_probe_verification/mobile_intro_t2.png`
- Automated smoke (local + deployed Pi):
  - `node qa/web_smoke.js` -> `local-desktop PASS`, `local-mobile PASS`, `rpi-desktop PASS`, `rpi-mobile PASS`.

Deployment log:

- Built web dist: `./gradlew :core:compileJava :html:dist` (SUCCESS).
- Synced dist to Pi staging: `/home/marc/cosmonaut-deploy`.
- Deployed to live root: `/var/www/cosmonaut`.
- Restarted service: `sudo systemctl restart cosmonaut-static.service`.
- Verified service status: `active`.
- Verified HTTPS host-header response: `HTTP/1.1 200 OK`.

Known platform constraint:

- Mobile fullscreen/orientation lock remains best-effort due browser permission/policy rules; behavior now retries across user interactions and lifecycle events.

## 2026-03-06 Mobile Intro Handoff + True Viewport Fill (Follow-up)

User-reported issues in this pass:

- Mobile HTML reached intro end but did not reliably start level 1.
- Mobile HTML looked like "fake fullscreen" with capped bars/tiny play area.

Root causes addressed:

- Intro progression on WebGL could stall for long periods due touch-only line advancement and fragile mid-render transition/dispose sequence.
- Mobile launcher sizing used generic client dimensions; on some mobile browsers this can drift from the effective visual viewport.

Changes implemented:

- `html/src/com/cosmonaut/client/HtmlLauncher.java`
  - added mobile visual-viewport sizing (`visualViewport.width/height` fallback to `innerWidth/innerHeight`).
  - mobile sizing now tracks effective viewport area directly.
  - desktop keeps centered 16:9 behavior.
- `core/src/com/cosmonaut/Screens/IntroScreen.java`
  - enabled timed dialogue progression (`timeControl=true`) while keeping touch skip support.
  - added faster web dialogue timing for intro/alarm blocks.
  - added WebGL safety timeout (95s absolute intro cap) to force level handoff if intro flow stalls.
  - fixed transition sequence to avoid `game.getScreen().dispose()` mid-render; now builds next screen, disposes intro safely, then switches.
  - stopped disposing asset-managed sounds (`Alarm`, `Background`) in `dispose()`; now only stops them.

Validation performed:

- Build:
  - `./gradlew :core:compileJava :html:dist` -> SUCCESS
- Targeted mobile no-touch intro test (Play -> wait):
  - final screenshot shows active gameplay/HUD after intro:
    - `qa/reports/mobile_intro_autostart_wait_final.png`
- Full smoke (post-fix, pre/post deploy):
  - `node qa/web_smoke.js`
  - `local-desktop` PASS
  - `local-mobile` PASS
  - `rpi-desktop` PASS
  - `rpi-mobile` PASS

Deployment log:

- Rebuilt dist and packaged `/tmp/cosmonaut-dist.tar.gz`.
- Uploaded to Pi and deployed:
  - staging: `/home/marc/cosmonaut-deploy`
  - live root: `/var/www/cosmonaut`
- Restarted backend service:
  - `cosmonaut-static.service` -> `active`
- Pi-side host-header checks:
  - `/` -> `HTTP/1.1 200 OK`
- `/html/html.nocache.js` -> `HTTP/1.1 200 OK`
- `/assets/assets.txt` -> `HTTP/1.1 200 OK`

## 2026-03-06 Mobile Landscape Viewport Initialization Fix

User-reported issue in this pass:

- On phone, intro->level transition worked but gameplay viewport was tiny in landscape with large black side bars.
- Rotating back to portrait showed the full game, indicating the runtime had initialized using portrait dimensions.

Root cause:

- Mobile runtime size in `HtmlLauncher` used raw viewport dimensions at startup.
- If the game initialized while browser was still portrait, internal width/height-dependent calculations were portrait-based.
- Some mobile fullscreen/orientation transitions do not trigger reliable resize events, leaving stale runtime dimensions.

Changes implemented:

- `html/src/com/cosmonaut/client/HtmlLauncher.java`
  - Mobile target size is now always normalized to landscape coordinates:
    - `targetWidth = max(browserWidth, browserHeight)`
    - `targetHeight = min(browserWidth, browserHeight)`
  - Added resize watchdog timer (`250ms`) to re-apply responsive size on mobile browsers with unreliable resize events.
  - Added no-op guard to avoid repeated `setWindowedMode(...)` calls when dimensions are unchanged.
- `html/webapp/index.html`
  - After immersive/orientation request, dispatches a synthetic `resize` event to accelerate canvas reflow.

Validation performed:

- Build:
  - `./gradlew :core:compileJava :html:dist` -> SUCCESS
- Orientation-init probe (start portrait then switch to landscape):
  - `portrait_init`: `inner=320x658`, `canvas=320x155.6` (landscape aspect preserved at init)
  - `after_landscape_resize`: `inner=658x320`, `canvas=658x320` (full landscape fill)
  - screenshots:
    - `qa/reports/mobile_portrait_init_afterfix.png`
    - `qa/reports/mobile_landscape_afterfix.png`
- Full smoke suite:
  - `node qa/web_smoke.js`
  - `local-desktop` PASS
  - `local-mobile` PASS
  - `rpi-desktop` PASS
  - `rpi-mobile` PASS

Deployment log:

- Rebuilt web dist and deployed to Pi `/var/www/cosmonaut`.
- Restarted `cosmonaut-static.service` -> `active`.
- Verified Pi host-header endpoints:
  - `/` -> `HTTP/1.1 200 OK`
  - `/html/html.nocache.js` -> `HTTP/1.1 200 OK`

## Fast Resume Checklist

1. `git checkout feature/full-upgrade-optimization-html-pi`
2. `./gradlew :core:compileJava :android:assembleDebug :desktop:build :desktop:windowsBundle :html:dist`
3. Deploy `html/build/dist/` to Pi and run host-header `curl` checks.
4. Review this worklog section "Deployment execution log" for the next pending item.
