#!/usr/bin/env bash
# Validate F-Droid/Fastlane metadata scaffold (AGENT gate; APK hashes remain ADB).
# Usage: scripts/verify-fdroid-metadata.sh
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

FASTLANE="$ROOT/fastlane/metadata/android/en-US"
LEGACY="$ROOT/examples/android/metadata/en-US"
GRADLE="$ROOT/examples/android/app/build.gradle.kts"
STUB="Golden Path FOSS Android stub"
ERRORS=0

fail() {
  echo "FAIL: $1"
  ERRORS=$((ERRORS + 1))
}

ok() {
  echo "OK   $1"
}

if [ -d "$FASTLANE" ]; then
  META="$FASTLANE"
  ok "root fastlane metadata present"
elif [ -d "$LEGACY" ]; then
  META="$LEGACY"
  ok "legacy examples/android/metadata present"
else
  META=""
  fail "missing fastlane/metadata/android/en-US"
fi

if [ -n "$META" ]; then
  for f in title.txt short_description.txt full_description.txt; do
    if [ ! -s "$META/$f" ]; then
      fail "missing or empty $META/$f"
    else
      ok "$f present"
    fi
  done

  if [ -s "$META/short_description.txt" ]; then
    SHORT="$(tr -d '\n' <"$META/short_description.txt")"
    if echo "$SHORT" | grep -q "$STUB"; then
      fail "placeholder short_description still present"
    fi
    SHORT_LEN="${#SHORT}"
    if [ "$SHORT_LEN" -gt 80 ]; then
      fail "short_description is ${SHORT_LEN} chars (F-Droid max 80)"
    else
      ok "short_description length ${SHORT_LEN}"
    fi
  fi

  if [ -f "$META/images/icon.png" ]; then
    ok "icon.png present"
  else
    fail "missing $META/images/icon.png"
  fi

  SHOT_DIR="$META/images/phoneScreenshots"
  SHOTS=0
  if [ -d "$SHOT_DIR" ]; then
    SHOTS="$(find "$SHOT_DIR" -maxdepth 1 -type f -name '*.png' | wc -l | tr -d ' ')"
  fi
  if [ "${SHOTS:-0}" -lt 1 ]; then
    fail "need at least one PNG in $SHOT_DIR"
  else
    ok "${SHOTS} phone screenshot(s)"
  fi
fi

if [ ! -f "$GRADLE" ]; then
  fail "missing $GRADLE"
else
  VERSION_CODE="$(grep -E 'versionCode\s*=' "$GRADLE" | head -1 | sed -E 's/.*=\s*([0-9]+).*/\1/')"
  if [ -z "${VERSION_CODE:-}" ]; then
    fail "could not parse versionCode from build.gradle.kts"
  elif [ -n "$META" ] && [ ! -s "$META/changelogs/${VERSION_CODE}.txt" ]; then
    fail "missing changelog $META/changelogs/${VERSION_CODE}.txt"
  else
    ok "changelog for versionCode ${VERSION_CODE}"
  fi
  if grep -q 'maybeCreate("release")' "$GRADLE" && grep -q 'findByName("release")' "$GRADLE"; then
    ok "release signingConfig is F-Droid-strip tolerant"
  else
    fail "signingConfigs must use maybeCreate/findByName(\"release\")"
  fi
  if grep -q 'getByName("release")' "$GRADLE"; then
    fail "signingConfigs.getByName(\"release\") breaks F-Droid signing strip"
  fi
fi

if [ -f "$ROOT/LICENSE" ]; then
  ok "root LICENSE present"
else
  fail "missing root LICENSE"
fi

echo ""
echo "SKIP [ADB] reproducible APK hash verification — run on device/emulator per modules/android/MODULE.md"

# Dummy/placeholder screenshots fail even when metadata text is present.
if ! bash "$ROOT/scripts/check-fdroid-screenshots.sh"; then
  fail "dummy F-Droid screenshots are not allowed"
fi

if [ "$ERRORS" -gt 0 ]; then
  echo "${ERRORS} F-Droid metadata check(s) failed"
  exit 1
fi

echo "F-Droid metadata scaffold verified"
