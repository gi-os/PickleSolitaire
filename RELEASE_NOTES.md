# v1.8.1

- Corner labels now sized to their own quadrant and centered inside it,
  instead of anchored right at the corner — fixes the odd spacing that
  pushed them close to the Select chip. Select chip widened (72dp →
  96dp) for more breathing room around the text.

# v1.8.0

- Corrected a standing assumption: the Touch Cruiser toggle is a separate
  physical "III" key, not KEYCODE_F3. F3 (bottom-left) is free, so it's
  now wired to the same activate action as D-pad center -- labeled "Play".

# v1.7.1

- Undo moved from bottom-left to top-right (that's where F2 actually is);
  bottom-left is blank now instead. Select pill now fills the bar's full
  height instead of floating in a padded box. Whole bar shrunk to about
  3/4 its previous height.

# v1.7.0

- Corrected the softkey bar layout again: it's not a single row, it's a
  cross layout matching the real physical keys — F1 (New) top-left, F2
  (Undo) bottom-left, F3 blank top-right, F4 (Draw) bottom-right, with
  Select centered over the crossing divider lines.

# v1.6.1

- The v1.6.0 compass guess was wrong — a real screenshot of the native
  bar showed the actual layout: one flat row, three segments split by
  thin full-height divider lines, center segment a shade lighter as the
  "main" one. Rebuilt to match: Undo | New (highlighted) | Draw.

# v1.6.0

- Restyled the bottom bar as a compass instead of a plain row, to match
  the native grey bar's own layout on this device: a center "Select"
  pill (D-pad center/OK) with New (top), Undo (left), Draw (right), and
  Back (bottom) arranged around it.

# v1.5.0

- Best-guess fix for the grey system softkey bar: forced immersive/
  fullscreen mode (`SYSTEM_UI_FLAG_HIDE_NAVIGATION` + `FULLSCREEN` +
  `IMMERSIVE_STICKY`, reasserted on every focus change). If that bar is
  Kyocera's OEM navigation bar rather than something we can relabel, this
  should hide it outright and let our own app-drawn bar take over the
  full bottom strip instead of sitting above it. Unverified on hardware
  until this build.

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
