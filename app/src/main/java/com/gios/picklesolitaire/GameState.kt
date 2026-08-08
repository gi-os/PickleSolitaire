package com.gios.picklesolitaire

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.gios.picklesolitaire.game.Game
import com.gios.picklesolitaire.game.Pile
import com.gios.picklesolitaire.game.SaveState
import com.gios.picklesolitaire.game.autoMove
import kotlin.random.Random

private const val PREFS = "pickle_solitaire"
private const val KEY_SAVE = "save"

/**
 * Everything the board screen needs: the current [Game], undo history, and
 * where the D-pad cursor is. No touch anywhere — [activate] is what a center
 * press does, [moveFocus] is what a D-pad direction does.
 */
class GameState(private val context: Context) {

    var game: Game by mutableStateOf(loadOrDeal())
        private set

    var focus: Focus by mutableStateOf(Focus.Stock)
        private set

    var justWon: Boolean by mutableStateOf(false)
        private set

    private val history = ArrayDeque<Game>()

    fun moveFocus(dx: Int, dy: Int) {
        focus = if (dy != 0) focus.moveVertical(game, dy) else focus.moveHorizontal(game, dx)
    }

    /** What a center/OK press does, wherever the cursor happens to be. */
    fun activate() {
        when (focus) {
            Focus.New -> newGame()
            Focus.Undo -> undo()
            else -> {
                val (pile, cardIndex) = focus.asPileAndIndex() ?: return
                performAutoMove(pile, cardIndex)
            }
        }
    }

    fun newGame() {
        history.clear()
        applyGame(Game.deal(Random.nextLong()))
    }

    fun undo() {
        val previous = history.removeLastOrNull() ?: return
        applyGame(previous, resetWin = true)
    }

    /** F4 shortcut — draws on the stock without moving the cursor there first. */
    fun drawFromStock() {
        performAutoMove(Pile.Stock, 0)
    }

    private fun performAutoMove(pile: Pile, cardIndex: Int) {
        val before = game
        val after = game.autoMove(pile, cardIndex) ?: return
        history.addLast(before)
        if (history.size > 120) history.removeFirst()
        applyGame(after)
    }

    private fun applyGame(next: Game, resetWin: Boolean = false) {
        game = next
        justWon = !resetWin && next.isWon
        save()
        // Re-land the cursor on a real stop — a column it was deep into may have
        // shrunk (or emptied) under it.
        focus = Focus.order(next).firstOrNull { it.sameSlotFor(focus) } ?: Focus.Stock
    }

    private fun Focus.sameSlotFor(previous: Focus): Boolean = when {
        this is Focus.TableauColumn && previous is Focus.TableauColumn -> index == previous.index
        else -> this::class == previous::class &&
            (this as? Focus.Foundation)?.index == (previous as? Focus.Foundation)?.index
    }

    private fun loadOrDeal(): Game {
        val saved = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY_SAVE, null)
        return SaveState.decode(saved) ?: Game.deal(Random.nextLong())
    }

    private fun save() {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_SAVE, SaveState.encode(game))
            .apply()
    }
}
