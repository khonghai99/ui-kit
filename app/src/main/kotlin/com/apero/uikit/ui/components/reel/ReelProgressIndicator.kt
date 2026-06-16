package com.apero.uikit.ui.components.reel

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Default values for [ReelProgressIndicator] appearance. */
object ReelProgressDefaults {
    val Height: Dp = 2.dp
    val ScrubTouchTargetHeight: Dp = 20.dp
    val TrackColor: @Composable () -> Color = {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
    }
    val ProgressColor: @Composable () -> Color = {
        MaterialTheme.colorScheme.primary
    }
}

/**
 * Linear progress bar for reel pages.
 *
 * Tracks playback progress (0..1), NOT pager position. Does not require PagerState.
 *
 * When [onSeek] is null: simple non-interactive progress bar.
 * When [onSeek] is provided: enables horizontal drag-to-seek with enlarged touch target.
 *
 * @param progress Current progress value (0f..1f)
 * @param progressColor Color of the filled portion
 * @param trackColor Color of the unfilled track
 * @param height Visual height of the progress bar
 * @param onSeek Callback with sought position (0f..1f) on drag end. Null disables scrubbing.
 */
@Composable
fun ReelProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier,
    progressColor: Color = ReelProgressDefaults.ProgressColor(),
    trackColor: Color = ReelProgressDefaults.TrackColor(),
    height: Dp = ReelProgressDefaults.Height,
    onSeek: ((Float) -> Unit)? = null,
) {
    if (onSeek == null) {
        // Non-interactive: simple progress bar
        LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = modifier.fillMaxWidth().height(height),
            color = progressColor,
            trackColor = trackColor,
            strokeCap = StrokeCap.Round,
        )
    } else {
        // Interactive: drag-to-seek with enlarged touch target
        var isDragging by remember { mutableStateOf(false) }
        var dragProgress by remember { mutableFloatStateOf(progress) }

        BoxWithConstraints(
            modifier = modifier
                .fillMaxWidth()
                .height(ReelProgressDefaults.ScrubTouchTargetHeight)
                .pointerInput(onSeek) {
                    detectHorizontalDragGestures(
                        onDragStart = { offset ->
                            isDragging = true
                            dragProgress = (offset.x / size.width).coerceIn(0f, 1f)
                        },
                        onDragEnd = {
                            isDragging = false
                            onSeek(dragProgress)
                        },
                        onDragCancel = {
                            isDragging = false
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            dragProgress = (dragProgress + dragAmount / size.width)
                                .coerceIn(0f, 1f)
                        },
                    )
                },
            contentAlignment = Alignment.Center,
        ) {
            LinearProgressIndicator(
                progress = { if (isDragging) dragProgress else progress.coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(height),
                color = progressColor,
                trackColor = trackColor,
                strokeCap = StrokeCap.Round,
            )
        }
    }
}
