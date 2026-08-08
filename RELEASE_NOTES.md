# v1.4.0

- Reverted the v1.3.0 Options Menu experiment. Confirmed on hardware: it
  doesn't map New/Undo/Draw onto the system's grey softkey bar the way
  native apps do it — it just collapses everything into one generic
  "Submenu" button at the top-left. No public API on this device does
  what that bar does; it's OEM-specific. Our own app-drawn bar from
  v1.2.0 is the real solution and stays as-is.

# v1.3.0

- The v1.2.0 bar we drew ourselves turned out to be redundant: the real
  softkey labels live in a separate system-owned grey bar below our app's
  window (confirmed on hardware — it survives a Touch Cruiser toggle, so
  it's not a Touch Cruiser overlay, it's genuine OS chrome). Wired up
  `onCreateOptionsMenu`/`onOptionsItemSelected` — the old pre-touchscreen
  Android convention for populating exactly this kind of bar — with New /
  Undo / Draw. Unverified on hardware until this build; our own black bar
  is left in place for now as a fallback in case this doesn't take.

# v1.2.0

- Added a softkey label bar at the bottom of the screen, same convention
  Kyocera's own KY-42C guide describes for the built-in menus ("when text
  shows on the screen's bottom line, the key below it does that"): New /
  Undo / (blank) / Draw, lined up with the F1/F2/F3/F4 keys underneath. The
  F1/F2/F4 shortcuts from v1.1.0 were working but invisible — this is what
  they were missing.

# v1.1.0

- Face-down cards now have a faint border, so a stacked column reads as a
  tower of cards instead of one solid green blob.
- The four soft keys under the screen (`KEYCODE_F1`/`F2`/`F4`, confirmed via
  KeyProbe) are now direct shortcuts: **F1** New, **F2** Undo, **F4** Draw —
  no need to steer the cursor over to those tiles first. **F3** is
  deliberately left alone since it's the device's own permanent Touch
  Cruiser toggle key.

# v1.0.0

First playable build for the Kyocera DIGNO KY-42C. See README for the full
control scheme and how the D-pad cursor model works.
