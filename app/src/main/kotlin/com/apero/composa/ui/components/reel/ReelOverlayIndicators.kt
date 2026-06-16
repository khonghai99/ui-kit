package com.apero.composa.ui.components.reel

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 * Play/pause bubble overlay — appears on tap, auto-hides after [autoHideMs].
 *
 * Matches Flow's visual: circle with scale-in entrance, scale-out exit.
 *
 * @param visible Whether to show the indicator
 * @param isPlaying True = show play icon, false = show pause icon
 * @param onHide Called when auto-hide timer fires — consumer should set visible = false
 * @param autoHideMs Auto-hide delay (0 to disable)
 */
@Composable
fun ReelPlayPauseIndicator(
    visible: Boolean,
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    autoHideMs: Long = 600L,
    bubbleSize: Dp = 72.dp,
    bubbleColor: Color = Color.Black.copy(alpha = 0.45f),
    onHide: () -> Unit = {},
) {
    if (autoHideMs > 0) {
        LaunchedEffect(visible) {
            if (visible) {
                delay(autoHideMs)
                onHide()
            }
        }
    }

    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = scaleIn(initialScale = 0.6f, animationSpec = tween(150)) + fadeIn(tween(100)),
        exit = scaleOut(targetScale = 1.2f, animationSpec = tween(300)) + fadeOut(tween(300)),
    ) {
        Box(
            modifier = Modifier
                .size(bubbleSize)
                .background(bubbleColor, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = if (isPlaying) "\u25B6" else "\u23F8",
                color = Color.White,
                fontSize = 28.sp,
            )
        }
    }
}

/**
 * Buffering spinner overlay — shows during video load.
 *
 * @param visible Whether buffering is in progress
 */
@Composable
fun ReelBufferingIndicator(
    visible: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    color: Color = MaterialTheme.colorScheme.primary,
    strokeWidth: Dp = 3.dp,
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn(tween(200)),
        exit = fadeOut(tween(200)),
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(size),
            color = color,
            strokeWidth = strokeWidth,
        )
    }
}

/**
 * Speed indicator pill — shows during long-press (e.g., "2x").
 *
 * Slides down from top with fade, matching Flow's visual.
 *
 * @param visible Whether speed mode is active
 * @param speedText Text to display (e.g., "2x", "3x")
 */
@Composable
fun ReelSpeedIndicator(
    visible: Boolean,
    speedText: String = "2x",
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = slideInVertically { -it } + fadeIn(),
        exit = slideOutVertically { -it } + fadeOut(),
    ) {
        Box(
            modifier = Modifier
                .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(20.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("\u23E9", color = Color.White, fontSize = 16.sp)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = speedText,
                    color = Color.White,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}
