package com.apero.composa.ui.components.reel

import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * Lifecycle-aware side-effects for [VerticalReelPager] page state.
 *
 * Fires callbacks when the current page changes, settles, or approaches the end of the list.
 * All callbacks are lifecycle-aware — only fire while the host is in RESUMED state.
 *
 * @param pagerState The pager state to observe
 * @param pageCount Total number of pages (needed for end-detection)
 * @param onPageSettled Called when a page fully settles after a fling/scroll
 * @param onPageChanged Called when the currently-visible page index changes (includes mid-scroll)
 * @param onApproachingEnd Called when [pagerState.currentPage] >= [pageCount] - [endThreshold]
 * @param endThreshold How many pages before the end triggers [onApproachingEnd]
 */
@Composable
fun ReelPageLifecycleEffect(
    pagerState: PagerState,
    pageCount: Int,
    onPageSettled: (settledPage: Int) -> Unit = {},
    onPageChanged: (currentPage: Int) -> Unit = {},
    onApproachingEnd: (currentPage: Int) -> Unit = {},
    endThreshold: Int = 3,
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val currentOnPageSettled by rememberUpdatedState(onPageSettled)
    val currentOnPageChanged by rememberUpdatedState(onPageChanged)
    val currentOnApproachingEnd by rememberUpdatedState(onApproachingEnd)

    // Settled page — fires after fling animation completes
    LaunchedEffect(pagerState, lifecycle) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            snapshotFlow { pagerState.settledPage }
                .distinctUntilChanged()
                .collect { currentOnPageSettled(it) }
        }
    }

    // Current page — fires during scroll (visual tracking)
    LaunchedEffect(pagerState, lifecycle) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            snapshotFlow { pagerState.currentPage }
                .distinctUntilChanged()
                .collect { page ->
                    currentOnPageChanged(page)
                    if (pageCount > 0 && page >= pageCount - endThreshold) {
                        currentOnApproachingEnd(page)
                    }
                }
        }
    }
}
