package com.apero.composa.ui.components.reel

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Vertical action button column for reel pages (right side).
 *
 * Slot-based — consumer places [ReelActionButton] instances and any custom composables inside.
 * Matches Flow's Shorts action column layout.
 *
 * @param spacing Vertical spacing between items
 * @param content Column content — typically [ReelActionButton] instances
 */
@Composable
fun ReelActionButtonColumn(
    modifier: Modifier = Modifier,
    spacing: Dp = 16.dp,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(spacing),
        horizontalAlignment = Alignment.CenterHorizontally,
        content = content,
    )
}

/**
 * Single action button with icon slot + label text.
 *
 * No ripple indication (matching Flow's Shorts pattern). Consumer provides icon via slot.
 *
 * @param label Text below the icon
 * @param onClick Tap handler
 * @param tint Color for the label text
 * @param icon Composable icon slot (Text emoji, Icon, AsyncImage, etc.)
 */
@Composable
fun ReelActionButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = Color.White,
    iconSize: Dp = 28.dp,
    labelStyle: TextStyle = MaterialTheme.typography.labelSmall,
    icon: @Composable () -> Unit,
) {
    Column(
        modifier = modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick,
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        // Icon slot with constrained size
        Column(
            modifier = Modifier.size(iconSize),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            icon()
        }
        Text(
            text = label,
            style = labelStyle,
            color = tint,
        )
    }
}
