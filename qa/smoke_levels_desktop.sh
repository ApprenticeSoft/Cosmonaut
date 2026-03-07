#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

LOG_DIR="$ROOT_DIR/qa/logs/desktop-level-smoke"
mkdir -p "$LOG_DIR"
ASSETS_DIR="$ROOT_DIR/android/assets"

LAUNCHER_UNIX="$ROOT_DIR/desktop/build/install/desktop/bin/desktop"
LAUNCHER_WIN="$ROOT_DIR/desktop/build/install/desktop/bin/desktop.bat"

echo "[level-smoke] Building desktop installDist..."
./gradlew :desktop:installDist >/dev/null

if [[ -x "$LAUNCHER_UNIX" ]]; then
  LAUNCHER="$LAUNCHER_UNIX"
elif [[ -f "$LAUNCHER_WIN" ]]; then
  LAUNCHER="$LAUNCHER_WIN"
else
  echo "[level-smoke] Launcher not found after installDist build."
  exit 1
fi

echo "[level-smoke] Launcher: $LAUNCHER"
if [[ ! -d "$ASSETS_DIR" ]]; then
  echo "[level-smoke] Assets directory not found: $ASSETS_DIR"
  exit 1
fi
echo "[level-smoke] Working directory: $ASSETS_DIR"
FAIL_COUNT=0

for LEVEL in $(seq 1 24); do
  LOG_FILE="$LOG_DIR/level-${LEVEL}.log"
  echo "[level-smoke] Launching level $LEVEL..."

  set +e
  (
    cd "$ASSETS_DIR"
    timeout 8s "$LAUNCHER" "--level=$LEVEL"
  ) >"$LOG_FILE" 2>&1
  STATUS=$?
  set -e

  if [[ $STATUS -eq 124 ]]; then
    echo "[level-smoke] Level $LEVEL launched (process kept running until timeout)."
  elif [[ $STATUS -eq 0 ]]; then
    echo "[level-smoke] Level $LEVEL launched and exited cleanly."
  else
    echo "[level-smoke] Level $LEVEL failed (exit=$STATUS). See $LOG_FILE"
    FAIL_COUNT=$((FAIL_COUNT + 1))
  fi
done

if [[ $FAIL_COUNT -gt 0 ]]; then
  echo "[level-smoke] FAIL: $FAIL_COUNT level(s) failed."
  exit 1
fi

echo "[level-smoke] PASS: all 24 levels launched without immediate crash."
