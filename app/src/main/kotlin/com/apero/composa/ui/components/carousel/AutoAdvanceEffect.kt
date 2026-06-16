@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
@file:OptIn(ExperimentalMaterial3Api::class)

package com.apero.composa.ui.components.carousel

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.material3.carousel.CarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Lifecycle-aware auto-advance for Material3 Carousel.
 *
 * Follows animeko's CarouselAutoAdvanceEffect pattern:
 * - Pauses when app backgrounded (lifecycle-aware)
 * - Pauses when user is scrolling manually
 * - External enable/disable toggle (e.g., hover-to-pause)
 * - Smart wrap-around: scrolls to first item when last page is ≥75% visible
 *
 * @param enabled External toggle — set false to pause (e.g., when hovered)
 * @param carouselState The carousel state to control
 * @param period Delay between auto-advances
 * @param animationSpec Animation for auto-scroll (default: 1000ms tween, per M3 motion spec)
 */
@Composable
fun CarouselAutoAdvanceEffect(
    enabled: Boolean,
    carouselState: CarouselState,
    period: Duration = 3.seconds,
    animationSpec: FiniteAnimationSpec<Float> = tween(durationMillis = 1000),
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(carouselState, lifecycle, animationSpec) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            snapshotFlow { !enabled || carouselState.isScrollInProgress }
                .collectLatest { skip ->
                    if (skip) return@collectLatest

                    while (currentCoroutineContext().isActive) {
                        delay(period)
                        launch(start = CoroutineStart.UNDISPATCHED) {
                            val pageCount = carouselState.pagerState.pageCount
                            if (pageCount <= 1) return@launch

                            val targetPage = (carouselState.pagerState.currentPage + 1) % pageCount
                            val pager = carouselState.pagerState
                            val layoutInfo = pager.layoutInfo
                            val lastItem = layoutInfo.visiblePagesInfo.lastOrNull() ?: return@launch

                            // Last page visible — check if ≥75% shown, then wrap to first
                            if (lastItem.index == pager.pageCount - 1) {
                                if (layoutInfo.viewportEndOffset - lastItem.offset >= layoutInfo.pageSize * 0.75f) {
                                    carouselState.animateScrollToItem(0, animationSpec)
                                } else {
                                    val scrollOffset =
                                        layoutInfo.pageSize - (layoutInfo.viewportEndOffset - lastItem.offset)
                                    carouselState.animateScrollBy(scrollOffset.toFloat(), animationSpec)
                                }
                            } else {
                                if (targetPage < 0 || targetPage >= pager.pageCount) return@launch
                                carouselState.animateScrollToItem(targetPage, animationSpec)
                            }
                        }
                    }
                }
        }
    }
}
