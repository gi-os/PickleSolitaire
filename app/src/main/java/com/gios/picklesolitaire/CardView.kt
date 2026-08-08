package com.gios.picklesolitaire

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
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
import com.gios.picklesolitaire.game.Card
import com.gios.picklesolitaire.game.Suit

private val CardBack = Color(0xFF1B5E20) // felt green, distinct from every suit color
private val CardBackBorder = Color(0x66FFFFFF) // faint edge so a stacked run reads as a tower, not a blob
private val CardFace = Color(0xFFFFFDF5)
private val FocusRing = Color(0xFFFFC107) // amber — the one color nothing else on the board uses
private val EmptySlotOutline = Color(0x33000000)

private fun Suit.symbol(): String = when (this) {
    Suit.SPADES -> "♠"
    Suit.HEARTS -> "♥"
    Suit.DIAMONDS -> "♦"
    Suit.CLUBS -> "♣"
}

private fun Suit.color(): Color = if (isRed) Color(0xFFC62828) else Color(0xFF212121)

/**
 * A single card, face up, face down, or an empty slot outline. [focused] draws
 * the amber cursor ring — the only way the player sees where they are, since
 * there's no touch highlight or pointer on this hardware.
 */
@Composable
fun CardView(
    card: Card?,
    faceUp: Boolean,
    focused: Boolean,
    modifier: Modifier = Modifier,
    emptySlotGlyph: String? = null,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .then(
                when {
                    card == null -> Modifier.border(1.dp, EmptySlotOutline, RoundedCornerShape(4.dp))
                    faceUp -> Modifier.background(CardFace)
                    else -> Modifier
                        .background(CardBack)
                        .border(1.dp, CardBackBorder, RoundedCornerShape(4.dp))
                }
            )
            .then(
                if (focused) Modifier.border(3.dp, FocusRing, RoundedCornerShape(4.dp)) else Modifier
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (card != null && faceUp) {
            Text(
                text = "${card.label}\n${card.suit.symbol()}",
                color = card.suit.color(),
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                lineHeight = 14.sp,
                modifier = Modifier.padding(2.dp),
            )
        } else if (card == null && emptySlotGlyph != null) {
            Text(text = emptySlotGlyph, color = EmptySlotOutline, fontSize = 16.sp)
        }
    }
}

@Composable
fun EmptyCardSlot(focused: Boolean, modifier: Modifier = Modifier, glyph: String? = null) {
    CardView(card = null, faceUp = false, focused = focused, modifier = modifier, emptySlotGlyph = glyph)
}
