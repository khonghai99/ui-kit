package com.apero.composa.ui.components.reel

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems

/**
 * Fullscreen vertical reel pager for static lists.
 *
 * Follows the same generic `<T>` + slot-based API as [com.apero.composa.ui.components.carousel.HeroCenterCarousel].
 *
 * @param items Static list to page through
 * @param beyondViewportPageCount Pages to preload beyond the visible viewport (default 1)
 * @param snapAnimationSpec Animation for snap-to-page fling
 * @param key Stable key factory for item identity
 * @param indicator Optional overlay indicator (receives PagerState + pageCount)
 * @param content Page content — receives the item and whether this page is currently active
 */
@Composable
fun <T> VerticalReelPager(
    items: List<T>,
    modifier: Modifier = Modifier,
    beyondViewportPageCount: Int = 1,
    snapAnimationSpec: AnimationSpec<Float> = spring(stiffness = Spring.StiffnessMedium),
    key: ((index: Int) -> Any)? = null,
    indicator: (@Composable (pagerState: PagerState, pageCount: Int) -> Unit)? = null,
    content: @Composable (item: T, isActive: Boolean) -> Unit,
) {
    if (items.isEmpty()) return

    val pagerState = rememberPagerState { items.size }

    Box(modifier = modifier.fillMaxSize()) {
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            beyondViewportPageCount = beyondViewportPageCount,
            key = key,
            flingBehavior = PagerDefaults.flingBehavior(
                state = pagerState,
                snapAnimationSpec = snapAnimationSpec,
            ),
        ) { page ->
            content(items[page], page == pagerState.currentPage)
        }

        indicator?.let { slot ->
            Box(Modifier.align(Alignment.BottomCenter)) {
                slot(pagerState, items.size)
            }
        }
    }
}

/**
 * Fullscreen vertical reel pager for Paging3 data sources.
 *
 * Prefetches items near the end via [loadMoreThreshold] and shows [placeholder] during loading.
 *
 * @param items Paging items from `Flow<PagingData<T>>.collectAsLazyPagingItems()`
 * @param beyondViewportPageCount Pages to preload beyond the visible viewport
 * @param loadMoreThreshold Triggers paging prefetch when currentPage >= itemCount - threshold
 * @param placeholderCount Number of shimmer pages shown during initial load
 * @param key Stable key factory for item identity
 * @param indicator Optional overlay indicator
 * @param errorContent Error overlay with retry action
 * @param placeholder Composable shown for loading pages
 * @param content Page content — receives the item and whether this page is currently active
 */
@Composable
fun <T : Any> VerticalReelPager(
    items: LazyPagingItems<T>,
    modifier: Modifier = Modifier,
    beyondViewportPageCount: Int = 1,
    loadMoreThreshold: Int = 3,
    snapAnimationSpec: AnimationSpec<Float> = spring(stiffness = Spring.StiffnessMedium),
    placeholderCount: Int = 3,
    key: ((index: Int) -> Any)? = null,
    indicator: (@Composable (pagerState: PagerState, pageCount: Int) -> Unit)? = null,
    errorContent: (@Composable (retry: () -> Unit) -> Unit)? = null,
    placeholder: @Composable () -> Unit = {},
    content: @Composable (item: T, isActive: Boolean) -> Unit,
) {
    val isLoading = items.loadState.refresh is LoadState.Loading
    val pageCount = if (isLoading) placeholderCount else items.itemCount
    if (pageCount == 0) return

    val pagerState = rememberPagerState { pageCount }

    // Guard out-of-bounds currentPage when page count shrinks (same pattern as HeroCenterCarousel)
    LaunchedEffect(pageCount) {
        if (pagerState.currentPage >= pageCount && pageCount > 0) {
            pagerState.scrollToPage(pageCount - 1)
        }
    }

    // Prefetch: trigger paging load when approaching end
    LaunchedEffect(pagerState.currentPage, items.itemCount) {
        if (!isLoading && items.itemCount > 0 && pagerState.currentPage >= items.itemCount - loadMoreThreshold) {
            // Access an item near the end to trigger Paging3 prefetch
            items.get(items.itemCount - 1)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            beyondViewportPageCount = beyondViewportPageCount,
            key = key,
            flingBehavior = PagerDefaults.flingBehavior(
                state = pagerState,
                snapAnimationSpec = snapAnimationSpec,
            ),
        ) { page ->
            if (isLoading) {
                placeholder()
            } else {
                items[page]?.let { item ->
                    content(item, page == pagerState.currentPage)
                } ?: placeholder()
            }
        }

        // Error overlay with retry
        if (errorContent != null && items.loadState.hasError) {
            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                errorContent { items.refresh() }
            }
        }

        indicator?.let { slot ->
            Box(Modifier.align(Alignment.BottomCenter)) {
                slot(pagerState, pageCount)
            }
        }
    }
}
