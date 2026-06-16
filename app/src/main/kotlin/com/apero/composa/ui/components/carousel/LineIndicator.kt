package com.apero.composa.ui.components.carousel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp as colorLerp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import kotlin.math.absoluteValue

/**
 * Scroll-linked line indicator for HorizontalPager.
 *
 * Active line expands to 1.5x width and transitions color during scroll.
 * Uses [PagerState.currentPageOffsetFraction] for sub-page animation.
 */
@Composable
fun LineIndicator(
    pagerState: PagerState,
    pageCount: Int,
    modifier: Modifier = Modifier,
    activeColor: Color = MaterialTheme.colorScheme.primary,
    inactiveColor: Color = MaterialTheme.colorScheme.outlineVariant,
    lineWidth: Dp = 24.dp,
    lineHeight: Dp = 3.dp,
    spacing: Dp = 4.dp,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(pageCount) { index ->
            val distanceFromCurrent = (pagerState.currentPage
                + pagerState.currentPageOffsetFraction - index)
                .absoluteValue.coerceIn(0f, 1f)

            val widthMultiplier = lerp(1.5f, 1f, distanceFromCurrent)
            val color = colorLerp(activeColor, inactiveColor, distanceFromCurrent)

            Box(
                modifier = Modifier
                    .width(lineWidth * widthMultiplier)
                    .height(lineHeight)
                    .clip(RoundedCornerShape(lineHeight / 2))
                    .background(color),
            )
        }
    }
}
