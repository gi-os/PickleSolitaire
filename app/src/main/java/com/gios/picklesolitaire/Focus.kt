package com.gios.picklesolitaire

import com.gios.picklesolitaire.game.Game
import com.gios.picklesolitaire.game.Pile

/**
 * Where the D-pad cursor currently sits. There is no touch and no drag on this
 * hardware, so every pile the player can reach needs a stop in a flat,
 * left-to-right order — that's [ORDER] below. DPAD_LEFT/RIGHT walk that list;
 * DPAD_UP/DOWN only do something when focus is on a tableau column, where they
 * walk up/down the face-up run so a multi-card move is reachable, not just the
 * top card.
 *
 * Center press always means the same thing regardless of focus: call
 * [Game.autoMove] with whatever [asPileAndIndex] resolves to. That is exactly
 * the original app's "tap" semantics — foundation first for a single card,
 * else the leftmost legal column, no-op on a foundation, turn over a face-down
 * card at the bottom of its column, draw on the stock. Nothing here decides
 * game rules; this file only decides what the cursor is pointing at.
 */
sealed interface Focus {
    data object New : Focus
    data object Undo : Focus
    data object Stock : Focus
    data object Waste : Focus
    data class Foundation(val index: Int) : Focus
    data class TableauColumn(val index: Int, val cardIndex: Int) : Focus

    companion object {
        /** Flat left-to-right stop order. [moveHorizontal] walks this list. */
        fun order(game: Game): List<Focus> = buildList {
            add(New)
            add(Undo)
            add(Stock)
            add(Waste)
            repeat(Game.FOUNDATIONS) { add(Foundation(it)) }
            repeat(Game.COLUMNS) { col -> add(defaultTableauFocus(game, col)) }
        }

        /** Landing on a column defaults to its top card — the common tap target. */
        fun defaultTableauFocus(game: Game, column: Int): TableauColumn {
            val cards = game.tableau[column]
            return TableauColumn(column, cards.lastIndex)
        }
    }
}

/** [source] pile + card index for [Game.autoMove], or null if this focus can't act (New/Undo/empty foundation). */
fun Focus.asPileAndIndex(): Pair<Pile, Int>? = when (this) {
    Focus.New, Focus.Undo -> null
    Focus.Stock -> Pile.Stock to 0
    Focus.Waste -> Pile.Waste to 0
    is Focus.Foundation -> Pile.Foundation(index) to 0
    is Focus.TableauColumn -> Pile.Tableau(index) to cardIndex
}

fun Focus.moveHorizontal(game: Game, delta: Int): Focus {
    val order = Focus.order(game)
    val currentIndex = order.indexOfFirst { it.sameSlotAs(this) }.let { if (it == -1) 0 else it }
    val next = (currentIndex + delta).mod(order.size)
    return order[next]
}

fun Focus.moveVertical(game: Game, delta: Int): Focus {
    val current = this as? Focus.TableauColumn ?: return this
    val cards = game.tableau[current.index]
    if (cards.isEmpty()) return this
    val firstFaceUp = cards.indexOfFirst { it.faceUp }.let { if (it == -1) cards.lastIndex else it }
    val newIndex = (current.cardIndex + delta).coerceIn(firstFaceUp, cards.lastIndex)
    return Focus.TableauColumn(current.index, newIndex)
}

/** Same pile, ignoring which card within a tableau column — used to re-find a stop after the board changes. */
private fun Focus.sameSlotAs(other: Focus): Boolean = when {
    this is Focus.TableauColumn && other is Focus.TableauColumn -> index == other.index
    else -> this == other
}
