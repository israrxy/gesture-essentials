#!/usr/bin/env bash
# Downloads the latest CI debug APK for this repo and installs it over adb.
# Usage: ./scripts/install.sh [run-id]
set -euo pipefail

REPO="israrxy/gesture-essentials"
ARTIFACT="gesture-essentials-debug"
RUN_ID="${1:-}"

if [ -z "$RUN_ID" ]; then
  echo "Looking up the latest successful build..."
  RUN_ID=$(gh run list -R "$REPO" -w "Build Debug APK" -s success -L 1 --json databaseId --jq '.[0].databaseId')
fi

if [ -z "$RUN_ID" ] || [ "$RUN_ID" = "null" ]; then
  echo "No successful build found. Trigger one with: gh workflow run 'Build Debug APK' -R $REPO"
  exit 1
fi

echo "Downloading artifact from run $RUN_ID..."
rm -rf .apk-download
gh run download "$RUN_ID" -R "$REPO" -n "$ARTIFACT" -D .apk-download

APK=".apk-download/app-debug.apk"
echo "Installing $APK (keeping app data)..."
adb install -r "$APK"

echo "Launching..."
adb shell am start -n com.israrxy.gestureessentials/.AccessibilitySettingsActivity
echo "Done."
