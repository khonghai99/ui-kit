package com.apero.uikit.ui.components.reel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Default values for [ReelPageContainer] gradient overlays. */
object ReelPageDefaults {
    val TopGradientColors: List<Color> = listOf(
        Color.Black.copy(alpha = 0.5f),
        Color.Black.copy(alpha = 0.2f),
        Color.Transparent,
    )
    val BottomGradientColors: List<Color> = listOf(
        Color.Transparent,
        Color.Black.copy(alpha = 0.3f),
        Color.Black.copy(alpha = 0.7f),
    )
    val TopGradientHeight: Dp = 120.dp
    val BottomGradientHeight: Dp = 250.dp
}

/**
 * Fullscreen page container with top/bottom gradient overlays and two content slots.
 *
 * Layer stack (bottom to top):
 * 1. [mediaContent] — fullscreen (video player, image, etc.)
 * 2. Top gradient
 * 3. Bottom gradient
 * 4. [overlayContent] — full BoxScope for text, buttons, etc.
 *
 * Works independently — not coupled to [VerticalReelPager].
 *
 * @param backgroundColor Background color behind media (visible during image load)
 * @param topGradientColors Gradient colors for top overlay (top-to-bottom)
 * @param bottomGradientColors Gradient colors for bottom overlay (top-to-bottom)
 * @param mediaContent Slot for background media (image, video, etc.)
 * @param overlayContent Slot for foreground UI (text, action buttons, etc.)
 */
@Composable
fun ReelPageContainer(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.Black,
    topGradientColors: List<Color> = ReelPageDefaults.TopGradientColors,
    bottomGradientColors: List<Color> = ReelPageDefaults.BottomGradientColors,
    topGradientHeight: Dp = ReelPageDefaults.TopGradientHeight,
    bottomGradientHeight: Dp = ReelPageDefaults.BottomGradientHeight,
    mediaContent: @Composable BoxScope.() -> Unit,
    overlayContent: @Composable BoxScope.() -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor),
    ) {
        // Layer 1: Media content (fullscreen)
        mediaContent()

        // Layer 2: Top gradient (skip if fewer than 2 colors)
        if (topGradientColors.size >= 2) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(topGradientHeight)
                    .align(Alignment.TopCenter)
                    .background(Brush.verticalGradient(topGradientColors)),
            )
        }

        // Layer 3: Bottom gradient (skip if fewer than 2 colors)
        if (bottomGradientColors.size >= 2) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(bottomGradientHeight)
                    .align(Alignment.BottomCenter)
                    .background(Brush.verticalGradient(bottomGradientColors)),
            )
        }

        // Layer 4: Overlay content (text, buttons, etc.)
        overlayContent()
    }
}
