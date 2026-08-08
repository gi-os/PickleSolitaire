# Pickle Solitaire

Klondike solitaire for **Kyocera "garaho" flip phones** — Android under the hood, but
operated entirely with a physical keypad and D-pad, no touchscreen. Built and tested
against a **Kyocera DIGNO KY-42C** (docomo). No touch anywhere in this app on purpose:
every action is reachable with the D-pad and the center/OK key.

This is a from-scratch, standalone rebuild, not a fork of a light-sdk tool — the
original [BrightSolitaire](https://github.com/gi-os/LightSolitaire) is built on the
official Light Phone III SDK (`LightActivity`, a bound SDK service, etc.), which only
exists on real LightOS and can't run here. The Klondike rules engine itself
(`Cards.kt`, `Klondike.kt`, `Moves.kt`, `SaveState.kt`) is lifted verbatim from that
project — it has zero Android dependencies — and everything around it (the UI, input,
save/restore) is new.

## Why a keitai needs its own build

Kyocera's garaho platform ships a system-level cursor emulator ("Touch Cruiser") that
turns D-pad presses into a synthetic mouse pointer by default, so unmodified touch
apps are *usable* but clumsy — steering a cursor around a 3.4" screen to tap a card is
slow. Turning that off (long-press the **F3** key for 1+ second — a one-time, permanent
device setting) makes the D-pad send real `KEYCODE_DPAD_*` events instead, and this app
is built entirely around that: a single amber-ringed cursor moves between piles with
the D-pad, and center/OK acts on whatever it's pointing at.

Confirmed on real KY-42C hardware: Android 10 (API 29), 32-bit only (`armeabi-v7a`, no
`arm64-v8a`) — not that it matters here, there's no native code in this app.

## Controls

| Key | What happens |
| --- | --- |
| D-pad left/right | Move the cursor between Stock, Waste, the four Foundations, and the seven Tableau columns |
| D-pad up/down | On a Tableau column, walk up/down the face-up run — lets you pick a card mid-run for a multi-card move, not just the top card |
| Center / OK | Same as a tap in the original: sends the card to a Foundation if it fits, otherwise the leftmost legal column. No-op on a Foundation (never unstacks one by accident). Draws on the Stock. Turns over a face-down card at the bottom of its column. On **New**/**Undo**, does exactly that. |
| Back | Exits — nothing to lose, every move autosaves |

There's no drag and no manual placement override in this version — every move goes
where `autoMove` would send a tap in the original. That covers the large majority of
real play; manual placement (picking a specific destination column instead of the
auto-picked leftmost one) is a possible follow-up, not in v1.

## Build

```bash
./gradlew assembleDebug
```

`minSdk` is pinned to 29 to match the KY-42C's real Android 10. `compileSdk`/`targetSdk`
34 for a modern toolchain; nothing in this app touches an API above 29.

## Install

Same path as any sideloaded APK on this device — the standard browser blocks `.apk`
downloads, so get it onto the phone via USB (`adb install`, once developer options +
USB debugging are on) or Bluetooth transfer if USB/adb isn't cooperating. See
[Kyocera's official ADB driver](https://www.kyocera.co.jp/prdct/telecom/consumer/support/ky-42c/usb.html)
if Windows/macOS doesn't see the device.

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.gios.picklesolitaire/.MainActivity
```

**Before playing:** if you haven't already, long-press **F3** once to turn off Touch
Cruiser system-wide. It's permanent — one long-press, ever, on this phone.

## Save format

Text encoding via `SaveState`, one line: version, stock, waste, four foundations, seven
tableau columns, move count. Stored in `SharedPreferences`. A save that doesn't decode
to a legal 52-card deal is discarded in favor of a fresh deal, same rule as the
original.

## Version

`1.0.0` — first playable build. No Hint/Solver yet (the original's DFS solver,
`Solver.kt`, wasn't ported in this pass), no win-screen card animation, no manual
placement override. All straightforward follow-ups on top of a working core loop.
