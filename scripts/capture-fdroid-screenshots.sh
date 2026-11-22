#!/usr/bin/env bash
# Capture real QRaft PWA screenshots into root Fastlane (no Android emulator required).
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
WEB="$ROOT/examples/web"
OUT="$ROOT/fastlane/metadata/android/en-US/images"
cd "$WEB"
if [ ! -d node_modules ]; then
  npm ci
fi
npx playwright install chromium
npm run build
node "$WEB/scripts/capture-fdroid-screenshots.mjs"
echo "Wrote Fastlane images under $OUT"
