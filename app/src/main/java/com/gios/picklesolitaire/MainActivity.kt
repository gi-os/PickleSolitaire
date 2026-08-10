package com.gios.picklesolitaire

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.remember

/**
 * D-pad-only Klondike for the Kyocera DIGNO KY-42C. No touch anywhere in this
 * app on purpose — every action reachable from a D-pad direction + center,
 * confirmed against real hardware with a separate probe app before writing any
 * of this (see KeyProbe in the same working folder). Requires Touch Cruiser
 * turned off (long-press the physical "III" key once, it's a permanent
 * device setting) or D-pad presses arrive as cursor motion instead of
 * KeyEvents and nothing here will receive them. That "III" key is a
 * separate physical button from KEYCODE_F3 -- confirmed on hardware, F3 is
 * the bottom-left softkey and is free for this app to use.
 */
class MainActivity : ComponentActivity() {

    private lateinit var state: GameState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        state = GameState(applicationContext)
        hideSystemBars()

        setContent {
            MaterialTheme {
                BoardScreen(state = remember { state })
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        // Immersive-sticky gets dismissed by the system on focus changes
        // (an incoming call, the lock screen, etc.) -- reassert it so the
        // grey system softkey bar stays hidden and our own bar keeps the
        // full bottom strip to itself.
        if (hasFocus) hideSystemBars()
    }

    @Suppress("DEPRECATION")
    private fun hideSystemBars() {
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            )
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            when (event.keyCode) {
                KeyEvent.KEYCODE_DPAD_LEFT -> { state.moveFocus(-1, 0); return true }
                KeyEvent.KEYCODE_DPAD_RIGHT -> { state.moveFocus(1, 0); return true }
                KeyEvent.KEYCODE_DPAD_UP -> { state.moveFocus(0, -1); return true }
                KeyEvent.KEYCODE_DPAD_DOWN -> { state.moveFocus(0, 1); return true }
                KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> { state.activate(); return true }

                // The four soft keys under the screen — confirmed as plain
                // KEYCODE_F1-F4 via getevent on the actual KY-42C hardware.
                // Physical layout: F1 top-left, F2 top-right, F3 bottom-left,
                // F4 bottom-right (NOT F2-bottom-left/F3-top-right as
                // originally assumed — corrected Aug 2026 via getevent).
                KeyEvent.KEYCODE_F1 -> { state.newGame(); return true }
                KeyEvent.KEYCODE_F2 -> { state.undo(); return true }
                KeyEvent.KEYCODE_F3 -> { state.activate(); return true }
                KeyEvent.KEYCODE_F4 -> { state.drawFromStock(); return true }
                // Back exits the app via the normal Android back behavior —
                // no confirmation dialog, nothing to lose since every move autosaves.
            }
        }
        return super.dispatchKeyEvent(event)
    }

    // Tried onCreateOptionsMenu/onOptionsItemSelected here in v1.3.0 to see if
    // it would populate the system's own grey softkey bar (the one below our
    // app-drawn black bar, stuck on "Select"). It doesn't map items directly
    // to labeled keys the way native apps do — it just collapses into a
    // single generic "Submenu" button. Reverted in v1.4.0; the grey bar is
    // doing something OEM-specific with no public hook, so our own in-app bar
    // (BoardScreen's SoftkeyLabelBar) is the real solution here.
}
