package com.apero.composa.ui.components.carousel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Responsive dimensions for carousel items. */
@Immutable
data class CarouselItemSize(
    val preferredWidth: Dp,
    val imageHeight: Dp,
)

@Stable
private val carouselBrush = Brush.verticalGradient(
    listOf(
        Color.Transparent,
        Color.Transparent,
        Color.Black.copy(alpha = 0.612f),
    ),
)

/** Default values for carousel item appearance. */
object CarouselItemDefaults {
    val ImageHeight = 213.dp
    val PreferredWidth = 300.dp
    val Shape: Shape @Composable get() = MaterialTheme.shapes.extraLarge

    /** Returns responsive dimensions: 300dp for medium+ screens (≥600dp), 240dp for compact. */
    @Composable
    fun responsiveItemSize(): CarouselItemSize {
        val screenWidthDp = LocalConfiguration.current.screenWidthDp.dp
        val preferredWidth = if (screenWidthDp >= 600.dp) 300.dp else 240.dp
        return CarouselItemSize(preferredWidth = preferredWidth, imageHeight = ImageHeight)
    }
}

/**
 * Generic carousel item container with gradient overlay.
 *
 * Follows animeko's BasicCarouselItem pattern:
 * - Image layer clipped with [maskShape] (from CarouselItemScope.rememberMaskShape)
 * - Gradient overlay applied with same shape for consistent masking
 * - Content overlay on top (text, badges, etc.)
 *
 * @param maskShape Shape for masking — pass `rememberMaskShape(shape)` from CarouselItemScope
 * @param imageContent Slot for the background image (e.g., AsyncImage)
 * @param overlayContent Slot for content on top of the gradient (e.g., title text)
 */
@Composable
fun CarouselItemContainer(
    modifier: Modifier = Modifier,
    maskShape: Shape = RectangleShape,
    imageHeight: Dp = CarouselItemDefaults.ImageHeight,
    imageContent: @Composable BoxScope.() -> Unit,
    overlayContent: @Composable BoxScope.() -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(imageHeight),
    ) {
        // Image layer — clipped with maskShape
        Box(modifier = Modifier.fillMaxSize().clip(maskShape), content = imageContent)

        // Gradient overlay — same maskShape for consistent masking
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(carouselBrush, maskShape),
        )

        // Content overlay (text, badges, etc.)
        overlayContent()
    }
}
