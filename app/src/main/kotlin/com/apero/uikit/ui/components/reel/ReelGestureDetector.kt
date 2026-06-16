package com.apero.uikit.ui.components.reel

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput

/**
 * Configurable gesture layer for reel pages.
 *
 * Wraps [content] in a Box with tap/doubleTap/longPress detection.
 * Does NOT consume vertical drag — [VerticalReelPager] handles scroll independently.
 *
 * All callbacks are optional. If all are null, no gesture detection is applied (performance).
 *
 * @param onTap Fired on single tap (e.g., toggle play/pause)
 * @param onDoubleTap Fired on double-tap (e.g., like animation)
 * @param onLongPress Fired when a long-press begins (e.g., 2x speed)
 * @param onLongPressRelease Fired when a long-press ends (finger lifted)
 * @param content The page content to wrap
 */
@Composable
fun ReelGestureDetector(
    onTap: (() -> Unit)? = null,
    onDoubleTap: (() -> Unit)? = null,
    onLongPress: (() -> Unit)? = null,
    onLongPressRelease: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val hasGestures = onTap != null || onDoubleTap != null || onLongPress != null

    val gestureModifier = if (hasGestures) {
        Modifier.pointerInput(onTap, onDoubleTap, onLongPress) {
            var longPressTriggered = false
            detectTapGestures(
                onTap = onTap?.let { handler -> { handler() } },
                onDoubleTap = onDoubleTap?.let { handler -> { handler() } },
                onLongPress = onLongPress?.let { handler ->
                    { longPressTriggered = true; handler() }
                },
                onPress = {
                    longPressTriggered = false
                    tryAwaitRelease()
                    if (longPressTriggered) onLongPressRelease?.invoke()
                },
            )
        }
    } else {
        Modifier
    }

    Box(modifier = modifier.then(gestureModifier)) {
        content()
    }
}
