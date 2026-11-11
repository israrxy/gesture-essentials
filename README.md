# Gesture Essentials

Android shortcuts and actions triggered by gestures — shake, letter drawing,
screen events, charging, and more. Everything runs on-device; the app requests
**no internet permission**.

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
| M2.5 | In-app Settings: theme, shake sensitivity, haptics, cooldowns, permission status | ✅ |
| M3 | Gesture pad + letter recognition | ⏳ |
| M4 | Full action library, conditions, logs, polish | ⏳ |

### Runtime coverage

Working triggers: shake, screen on/off/unlock, power double-press heuristic,
charger, headset and app foreground.

Volume keys are deliberately **not** a trigger. The app never asks for key-event
filtering, so the volume keys stay purely system sound controls.

Working actions: flashlight, media play/pause-next-previous, lock screen,
screenshot (Android 11+), vibrate, Do Not Disturb, open URL, speak, wait.

Not wired yet: face-down, Wi-Fi network changes, drawn letters and manual entry
points (tile/bubble/notification). They are excluded from the custom-rule picker;
previously saved rules using them remain stored but cannot fire yet.

Branches: `main` holds the last user-verified, installable build. `dev` is where
the next milestone is built; it is promoted after user verification.

## Settings

Open **Android Settings → Accessibility → Downloaded apps → Gesture Essentials →
Settings** (the service-list name varies by phone). The app has no launcher entry.
Android's service page opens preferences through `android:settingsActivity`.

The page is a compact preference list:

1. **Common shortcuts** — separate saved switches for shake twice → flashlight
   on/off, shake three times → screenshot, and screen off → flashlight off.
   New presets start disabled.
2. **Custom rules** — saved rules with enable switches, Edit, and Add custom rule.
3. **Preferences** — shake sensitivity (default **25%, Low**) and haptic feedback.
4. **More settings** — theme, wallpaper colors, default cooldown, system access
   status and About.

Presets use stable rule IDs. A gesture has one owner: turning a shortcut on
switches off whichever rule used that gesture, and the page names what changed.
Custom rules can also be saved disabled. Multiple actions can be placed in one
custom rule. Existing custom rules and explicitly saved sensitivity values are
preserved.

A shake burst is dispatched after **450 ms of quiet**. A burst of N shakes
satisfies rules asking for N or fewer, so the engine runs only the closest
match: a triple shake never also runs the double-shake shortcut, and an
over-counted double shake still runs the double-shake shortcut when that is the
closest rule. The flashlight handler tracks changes from Android Quick Settings
as well as its own actions.

The system's Settings entry opens a dedicated preferences activity. Back returns
to the caller, and it works even when the accessibility service is disabled.
Android controls the service page itself; custom preferences are shown in the
app's settings activity.

Android does not allow an app to enable its own accessibility service or grant
itself DND/battery exemptions. Those rows are honest deep links, not fake
toggles.

The single CI workflow runs JVM tests for shake sequences, assignment conflicts,
concurrent preference writes and persistence across DataStore reopening before
publishing the APK. It also enforces the 500-line source-file limit.

## Known Android limits

- **Double-press of the power button cannot be intercepted** by a normal app.
  It ships as an off-by-default heuristic (screen off→on within ~1.2 s).
- Mobile data, airplane mode and hard Wi-Fi/Bluetooth toggles need Shizuku or
  root; they are planned for v2 rather than shipped as buttons that silently fail.
