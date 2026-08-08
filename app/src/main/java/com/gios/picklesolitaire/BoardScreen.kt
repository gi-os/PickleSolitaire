package com.gios.picklesolitaire

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gios.picklesolitaire.game.Game

private val Felt = Color(0xFF0B3D0B)
private val TopBarChip = Color(0xFF14531A)
private val SoftkeyBar = Color(0xFF1A1A1A)
private val SoftkeyChipBg = Color(0xFF3A3A3A)
private val SoftkeySelectBg = Color(0xFF555555)

/**
 * The whole board. Measures from the real available size (BoxWithConstraints)
 * rather than a fixed dp, the same reasoning the LPIII original used — this
 * screen is 480x854px at a manufacturer-inflated density, nothing about it can
 * be assumed ahead of time.
 */
@Composable
fun BoardScreen(state: GameState) {
    val game = state.game
    val focus = state.focus

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Felt)
            .padding(4.dp),
    ) {
        val columnWidth = maxWidth / Game.COLUMNS
        val cardHeight = columnWidth * 1.4f

        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(4.dp)) {

            // Top row: New | Undo | move counter, then Stock/Waste/Foundations
            // squeezed to match tableau column width so everything lines up below.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                TopBarAction("New", focus is Focus.New, Modifier.width(columnWidth * 0.9f))
                TopBarAction("Undo", focus is Focus.Undo, Modifier.width(columnWidth * 0.9f))
                Box(modifier = Modifier.width(columnWidth * 0.4f)) // gap so foundations line up on the right
                Text(
                    text = "${game.moves}",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                val topWaste = game.waste.lastOrNull()
                CardView(
                    card = if (game.stock.isEmpty()) null else game.stock.last(),
                    faceUp = false,
                    focused = focus is Focus.Stock,
                    modifier = Modifier.width(columnWidth).height(cardHeight),
                    emptySlotGlyph = if (game.stock.isEmpty() && game.waste.isNotEmpty()) "↻" else null,
                )
                CardView(
                    card = topWaste,
                    faceUp = true,
                    focused = focus is Focus.Waste,
                    modifier = Modifier.width(columnWidth).height(cardHeight),
                )
                Box(modifier = Modifier.width(columnWidth * 0.3f))
                for (f in 0 until Game.FOUNDATIONS) {
                    CardView(
                        card = game.foundations[f].lastOrNull(),
                        faceUp = true,
                        focused = focus is Focus.Foundation && focus.index == f,
                        modifier = Modifier.width(columnWidth).height(cardHeight),
                    )
                }
            }

            // Tableau: 7 fanned columns. A focused card in the middle of a run
            // gets the amber ring, not just whatever sits on top.
            Row(modifier = Modifier.fillMaxWidth().weight(1f)) {
                for (col in 0 until Game.COLUMNS) {
                    val column = game.tableau[col]
                    val fanStep = (cardHeight * 0.32f)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize(),
                    ) {
                        if (column.isEmpty()) {
                            EmptyCardSlot(
                                focused = focus is Focus.TableauColumn && focus.index == col,
                                modifier = Modifier
                                    .width(columnWidth)
                                    .height(cardHeight),
                            )
                        }
                        column.forEachIndexed { i, tableauCard ->
                            val isFocused = focus is Focus.TableauColumn &&
                                focus.index == col &&
                                focus.cardIndex == i
                            CardView(
                                card = tableauCard.card,
                                faceUp = tableauCard.faceUp,
                                focused = isFocused,
                                modifier = Modifier
                                    .width(columnWidth)
                                    .height(cardHeight)
                                    .offset(y = fanStep * i),
                            )
                        }
                    }
                }
            }

            SoftkeyCompass()
        }

        if (state.justWon) {
            WinOverlay(moves = game.moves)
        }
    }
}

/**
 * Now that immersive mode hides the device's own grey softkey bar (v1.5.0),
 * this owns the full bottom strip. Styled after that native bar's own
 * layout on this device — a center "Select" pill (the D-pad center/OK key)
 * with the surrounding soft keys arranged around it rather than in a plain
 * row. F3 (right side) is left unlabeled on purpose; it's the device's
 * permanent Touch Cruiser toggle, not something this app should claim.
 */
@Composable
private fun SoftkeyCompass() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(84.dp)
            .background(SoftkeyBar),
    ) {
        SoftkeyChip("New", Modifier.align(Alignment.TopCenter).padding(top = 6.dp))
        SoftkeyChip("Undo", Modifier.align(Alignment.CenterStart).padding(start = 10.dp))
        SoftkeyChip("Draw", Modifier.align(Alignment.CenterEnd).padding(end = 10.dp))
        SoftkeyChip("Back", Modifier.align(Alignment.BottomCenter).padding(bottom = 6.dp))
        SoftkeySelectDot(modifier = Modifier.align(Alignment.Center))
    }
}

@Composable
private fun SoftkeyChip(label: String, modifier: Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(SoftkeyChipBg)
            .padding(horizontal = 10.dp, vertical = 4.dp),
    ) {
        Text(text = label, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SoftkeySelectDot(modifier: Modifier) {
    Box(
        modifier = modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(SoftkeySelectBg),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = "Select", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun TopBarAction(label: String, focused: Boolean, modifier: Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(TopBarChip)
            .then(if (focused) Modifier.border(2.dp, Color(0xFFFFC107), RoundedCornerShape(4.dp)) else Modifier)
            .padding(vertical = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = label, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun WinOverlay(moves: Int) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xCC000000)),
        contentAlignment = Alignment.Center,
    ) {
        Column {
            Text(
                text = "You win",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
            Text(
                text = "$moves moves — press center on New for another deal",
                color = Color(0xFFDDDDDD),
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 8.dp).align(Alignment.CenterHorizontally),
            )
        }
    }
}
