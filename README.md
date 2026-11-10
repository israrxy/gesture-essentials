# Gesture Essentials

Android shortcuts and actions triggered by gestures — shake, volume keys, letter
drawing, screen events, charging, and more. Everything runs on-device; the app
requests **no internet permission**.

## Build

Builds run **only** on GitHub Actions, never locally.

Trigger a build:

```bash
gh workflow run "Build Debug APK" -R israrxy/gesture-essentials
```

## Install

```bash
./scripts/install.sh
```

The script picks the latest successful run, downloads the `gesture-essentials-debug`
artifact, and installs it over `adb` (keeping app data, thanks to the stable
debug keystore checked into `keystore/`).

## Project rules

- MVVM: Compose screens are stateless, logic lives in ViewModels/use cases,
  services never touch the UI.
- No source file exceeds 500 lines — enforced by `scripts/check-size.sh` in CI.
- UI, domain logic, data, runtime services and action handlers live in separate
  packages/files.

## Status

| Milestone | Scope | State |
|---|---|---|
| M0 | Project skeleton, theme, CI pipeline, artifact install | ✅ |
| M1 | Rule model, persistence, rules list/editor | ✅ |
| M2 | Accessibility service, trigger bus, rule engine, core actions | ✅ |
| M3 | Gesture pad + letter recognition | ⏳ |
| M4 | Full action library, conditions, logs, polish | ⏳ |

### Runtime coverage

Working triggers: shake, volume keys (short and long press), screen on/off/unlock,
power double-press heuristic, charger, headset, app foreground, manual.

Working actions: flashlight, media play/pause-next-previous, lock screen,
screenshot (Android 11+), vibrate, Do Not Disturb, open URL, speak, wait.

Not wired yet: face-down, Wi-Fi network changes and drawn letters. Rules using
those triggers are saved but stay inert; they land in M3 rather than pretending
to work.

Branches: `main` holds the last verified, installable build. `dev` is where the
next milestone is built. Both are fast-forwarded together once a build passes
and is installed.

## Known Android limits

- **Double-press of the power button cannot be intercepted** by a normal app.
  It ships as an off-by-default heuristic (screen off→on within ~1.2 s).
- Mobile data, airplane mode and hard Wi-Fi/Bluetooth toggles need Shizuku or
  root; they are planned for v2 rather than shipped as buttons that silently fail.
