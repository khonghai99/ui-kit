package com.apero.smoothgallery.ui.components

import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/** Lifecycle-aware auto-advance — pauses on touch and when backgrounded. */
@Composable
fun AutoAdvanceEffect(
    pagerState: PagerState,
    pageCount: Int,
    period: Duration = 3.seconds,
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(pagerState, lifecycle, pageCount) {
        if (pageCount <= 1) return@LaunchedEffect
        lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            snapshotFlow { pagerState.isScrollInProgress }
                .collectLatest { scrolling ->
                    if (scrolling) return@collectLatest
                    while (currentCoroutineContext().isActive) {
                        delay(period)
                        val next = (pagerState.currentPage + 1) % pageCount
                        pagerState.animateScrollToPage(next)
                    }
                }
        }
    }
}
