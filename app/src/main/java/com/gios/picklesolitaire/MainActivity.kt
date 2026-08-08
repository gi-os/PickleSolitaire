package com.gios.picklesolitaire

import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.remember

/**
 * D-pad-only Klondike for the Kyocera DIGNO KY-42C. No touch anywhere in this
 * app on purpose — every action reachable from a D-pad direction + center,
 * confirmed against real hardware with a separate probe app before writing any
 * of this (see KeyProbe in the same working folder). Requires Touch Cruiser
 * turned off (long-press F3 once, it's a permanent device setting) or D-pad
 * presses arrive as cursor motion instead of KeyEvents and nothing here will
 * receive them.
 */
class MainActivity : ComponentActivity() {

    private lateinit var state: GameState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        state = GameState(applicationContext)

        setContent {
            MaterialTheme {
                BoardScreen(state = remember { state })
            }
        }
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
                // KEYCODE_F1-F4 via KeyProbe. Direct shortcuts, no cursor
                // movement needed. F3 is deliberately skipped: it's the
                // device's own permanent Touch Cruiser toggle key, and
                // doubling it up here risks confusing the two.
                KeyEvent.KEYCODE_F1 -> { state.newGame(); return true }
                KeyEvent.KEYCODE_F2 -> { state.undo(); return true }
                KeyEvent.KEYCODE_F4 -> { state.drawFromStock(); return true }
                // Back exits the app via the normal Android back behavior —
                // no confirmation dialog, nothing to lose since every move autosaves.
            }
        }
        return super.dispatchKeyEvent(event)
    }
}
