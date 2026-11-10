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
| M2 | Accessibility service, trigger bus, rule engine, core actions | ⏳ |
| M3 | Gesture pad + letter recognition | ⏳ |
| M4 | Full action library, conditions, logs, polish | ⏳ |

## Known Android limits

- **Double-press of the power button cannot be intercepted** by a normal app.
  It ships as an off-by-default heuristic (screen off→on within ~1.2 s).
- Mobile data, airplane mode and hard Wi-Fi/Bluetooth toggles need Shizuku or
  root; they are planned for v2 rather than shipped as buttons that silently fail.
