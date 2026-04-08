package com.apero.smoothgallery.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.apero.smoothgallery.model.GalleryImage
import kotlin.math.absoluteValue

/** Hero-center banner with auto-advance and gradient overlays. */
@Composable
fun BannerCarousel(
    items: List<GalleryImage>,
    modifier: Modifier = Modifier,
) {
    if (items.isEmpty()) return

    val pagerState = rememberPagerState { items.size }

    Box(modifier.fillMaxWidth()) {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 32.dp),
            pageSpacing = 12.dp,
        ) { page ->
            // Hero-center effect: side pages shrink slightly
            val pageOffset = ((pagerState.currentPage - page)
                + pagerState.currentPageOffsetFraction).absoluteValue
            val scale = lerp(1f, 0.9f, pageOffset.coerceIn(0f, 1f))

            CarouselBannerItem(
                image = items[page],
                modifier = Modifier.graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                },
            )
        }

        AutoAdvanceEffect(
            pagerState = pagerState,
            pageCount = items.size,
        )
    }
}
