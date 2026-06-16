@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
@file:OptIn(ExperimentalMaterial3Api::class)

package com.apero.composa.ui.components.carousel

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.carousel.CarouselDefaults
import androidx.compose.material3.carousel.CarouselItemScope
import androidx.compose.material3.carousel.CarouselState
import androidx.compose.material3.carousel.HorizontalCenteredHeroCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/** Multiplier for virtual page count in infinite scroll mode. */
private const val INFINITE_SCROLL_PAGES = 10000

/**
 * Hero-center carousel using Material3 [HorizontalCenteredHeroCarousel].
 *
 * Follows animeko's TrendingSubjectsCarousel pattern:
 * - M3 Carousel handles hero-center layout and item masking natively
 * - multiBrowse fling behavior with medium-stiffness snap
 * - Auto-advance with hover-to-pause
 *
 * @param items Static list of items to display
 * @param autoAdvance Enable auto-scroll (default true)
 * @param maxItemWidth Maximum width for carousel items (default 300dp)
 * @param itemSpacing Spacing between items (default 8dp)
 * @param content Composable to render each item (receives CarouselItemScope)
 */
@Composable
fun <T> HeroCenterCarousel(
    items: List<T>,
    modifier: Modifier = Modifier,
    infiniteScroll: Boolean = false,
    autoAdvance: Boolean = true,
    autoAdvancePeriod: Duration = 3.seconds,
    maxItemWidth: Dp = 300.dp,
    itemSpacing: Dp = 8.dp,
    content: @Composable CarouselItemScope.(item: T) -> Unit,
) {
    if (items.isEmpty()) return

    val actualSize = items.size
    val shouldLoop = infiniteScroll && actualSize >= 2
    val virtualCount = if (shouldLoop) {
        actualSize.toLong().times(INFINITE_SCROLL_PAGES).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
    } else actualSize
    val startIndex = if (shouldLoop) (INFINITE_SCROLL_PAGES / 2) * actualSize else 0

    val carouselState = rememberCarouselState(initialItem = startIndex) { virtualCount }
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    // Re-center virtual position when drifting too far from center (prevents edge snap-back)
    if (shouldLoop) {
        LaunchedEffect(carouselState) {
            snapshotFlow { carouselState.pagerState.isScrollInProgress }
                .collect { scrolling ->
                    if (!scrolling) {
                        val current = carouselState.pagerState.currentPage
                        val center = (INFINITE_SCROLL_PAGES / 2) * actualSize
                        if (kotlin.math.abs(current - center) > virtualCount / 4) {
                            val newPage = center + (current % actualSize)
                            carouselState.pagerState.scrollToPage(newPage)
                        }
                    }
                }
        }
    }

    Box(modifier.hoverable(interactionSource)) {
        HorizontalCenteredHeroCarousel(
            state = carouselState,
            modifier = Modifier.fillMaxWidth(),
            maxItemWidth = maxItemWidth,
            itemSpacing = itemSpacing,
            flingBehavior = CarouselDefaults.multiBrowseFlingBehavior(
                state = carouselState,
                snapAnimationSpec = spring(stiffness = Spring.StiffnessMedium),
            ),
        ) { virtualIndex ->
            content(items[if (shouldLoop) virtualIndex % actualSize else virtualIndex])
        }

        CarouselAutoAdvanceEffect(
            enabled = autoAdvance && !isHovered,
            carouselState = carouselState,
            period = autoAdvancePeriod,
        )
    }
}

/**
 * Hero-center carousel for Paging3 data sources.
 *
 * Shows [placeholderCount] shimmer items during initial load.
 * Otherwise behaves identically to the List overload.
 *
 * Note: [infiniteScroll] is intentionally absent — paging data is unbounded by nature.
 */
@Composable
fun <T : Any> HeroCenterCarousel(
    items: LazyPagingItems<T>,
    modifier: Modifier = Modifier,
    autoAdvance: Boolean = true,
    autoAdvancePeriod: Duration = 3.seconds,
    maxItemWidth: Dp = 300.dp,
    itemSpacing: Dp = 8.dp,
    placeholderCount: Int = 5,
    errorContent: (@Composable (retry: () -> Unit) -> Unit)? = null,
    placeholder: @Composable CarouselItemScope.() -> Unit = {},
    content: @Composable CarouselItemScope.(item: T) -> Unit,
) {
    val isLoading = items.loadState.refresh is LoadState.Loading
    val pageCount = if (isLoading) placeholderCount else items.itemCount
    if (pageCount == 0) return

    val carouselState = rememberCarouselState(initialItem = 0) { pageCount }
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val size = CarouselItemDefaults.responsiveItemSize()

    Box(modifier.hoverable(interactionSource)) {
        HorizontalCenteredHeroCarousel(
            state = carouselState,
            modifier = Modifier.fillMaxWidth(),
            maxItemWidth = maxItemWidth,
            itemSpacing = itemSpacing,
            flingBehavior = CarouselDefaults.multiBrowseFlingBehavior(
                state = carouselState,
                snapAnimationSpec = spring(stiffness = Spring.StiffnessMedium),
            ),
        ) { index ->
            if (isLoading) {
                placeholder()
            } else {
                items[index]?.let { content(it) } ?: placeholder()
            }
        }

        // Error overlay with retry
        if (errorContent != null && items.loadState.hasError) {
            Box(
                Modifier.height(size.imageHeight).fillMaxWidth(),
                contentAlignment = Alignment.TopCenter,
            ) {
                errorContent { items.refresh() }
            }
        }

        CarouselAutoAdvanceEffect(
            enabled = autoAdvance && !isHovered && !isLoading,
            carouselState = carouselState,
            period = autoAdvancePeriod,
        )
    }
}
