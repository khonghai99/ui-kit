package com.apero.uikit.ui.components.reel

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import kotlinx.coroutines.delay

/**
 * Double-tap like animation — spring-bounce heart that auto-dismisses.
 *
 * Matches Flow's visual: scale-in with medium-bouncy spring, scale-out + fade exit.
 *
 * @param visible Trigger by setting true; auto-resets via [onAnimationEnd]
 * @param durationMs How long the animation stays visible before auto-dismiss
 * @param onAnimationEnd Called after [durationMs] — consumer should set visible = false
 * @param content Custom content slot; defaults to a large red heart
 */
@Composable
fun ReelLikeAnimation(
    visible: Boolean,
    modifier: Modifier = Modifier,
    durationMs: Long = 800L,
    iconSize: Dp = 120.dp,
    iconColor: Color = Color.Red,
    onAnimationEnd: () -> Unit = {},
    content: (@Composable () -> Unit)? = null,
) {
    LaunchedEffect(visible) {
        if (visible) {
            delay(durationMs)
            onAnimationEnd()
        }
    }

    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = scaleIn(
            initialScale = 0.3f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        ) + fadeIn(),
        exit = scaleOut(
            targetScale = 1.4f,
            animationSpec = tween(400),
        ) + fadeOut(tween(400)),
    ) {
        if (content != null) {
            content()
        } else {
            val fontSize = with(LocalDensity.current) { (iconSize * 0.6f).toSp() }
            Text(
                text = "\u2764\uFE0F",
                fontSize = fontSize,
                color = iconColor,
            )
        }
    }
}
