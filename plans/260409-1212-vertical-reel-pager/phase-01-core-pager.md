# Phase 1 — Core Pager + Lifecycle

## Context Links
- [HeroCenterCarousel.kt](../../app/src/main/kotlin/com/apero/uikit/ui/components/carousel/HeroCenterCarousel.kt) — reference for dual-overload pager pattern
- [AutoAdvanceEffect.kt](../../app/src/main/kotlin/com/apero/uikit/ui/components/carousel/AutoAdvanceEffect.kt) — reference for lifecycle-aware side-effects
- [Flow ShortsScreen.kt](../../../Flow/app/src/main/java/io/github/aedev/flow/ui/screens/shorts/ShortsScreen.kt) — source: VerticalPager + load-more + settled page tracking

## Overview
- **Priority**: P1 (blocks all other phases)
- **Status**: Pending
- **Effort**: 1.5h

Create the main `VerticalReelPager` composable and `ReelPageLifecycleEffect` side-effect. These are the foundation all other reel components build on.

## Key Insights from Source Analysis

**HeroCenterCarousel pattern** (to replicate):
- Generic `<T>` type parameter
- Two overloads: `List<T>` (simple) and `LazyPagingItems<T>` (paging)
- `PagerState` exposed via `indicator` slot lambda
- Early return on empty items
- `key` parameter for stable item identity
- Paging overload handles `LoadState.Loading` with placeholder shimmer
- `LaunchedEffect(pageCount)` guards out-of-bounds currentPage

**Flow ShortsScreen patterns** (to port as generic callbacks):
- `beyondViewportPageCount = 1` — preloads adjacent pages
- `LaunchedEffect(pagerState.currentPage)` — load more when `currentPage >= size - 3`
- `LaunchedEffect(pagerState.settledPage)` — activate current, preload next/prev
- `isActive = page == pagerState.currentPage` — passed to each page composable

## Requirements

### Functional
- `VerticalReelPager(items: List<T>, ...)` with fullscreen vertical paging
- `VerticalReelPager(items: LazyPagingItems<T>, ...)` with load-more + placeholder
- `beyondViewportPageCount` param (default 1)
- `loadMoreThreshold` param (default 3) — triggers `onLoadMore` callback
- Page content lambda receives `(item: T, isActive: Boolean)`
- `ReelPageLifecycleEffect` composable for settled-page side-effects

### Non-Functional
- File: `VerticalReelPager.kt` < 200 lines
- File: `ReelPageLifecycleEffect.kt` < 80 lines
- No new dependencies

## Architecture

### VerticalReelPager.kt

```kotlin
// List<T> overload
@Composable
fun <T> VerticalReelPager(
    items: List<T>,
    modifier: Modifier = Modifier,
    beyondViewportPageCount: Int = 1,
    snapAnimationSpec: AnimationSpec<Float> = spring(stiffness = Spring.StiffnessMedium),
    key: ((index: Int) -> Any)? = null,
    indicator: (@Composable (pagerState: PagerState, pageCount: Int) -> Unit)? = null,
    content: @Composable (item: T, isActive: Boolean) -> Unit,
)

// LazyPagingItems<T> overload
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
)
```

Both overloads:
- Use `VerticalPager` with `beyondViewportPageCount`
- Pass `isActive = (page == pagerState.currentPage)` to content lambda
- Indicator slot placed as overlay (unlike carousel which places below)

Paging overload additionally:
- Triggers `items.get()` prefetch when `currentPage >= itemCount - loadMoreThreshold`
- Shows placeholder during `LoadState.Loading`
- Shows `errorContent` on error with retry

### ReelPageLifecycleEffect.kt

```kotlin
@Composable
fun ReelPageLifecycleEffect(
    pagerState: PagerState,
    pageCount: Int,
    onPageSettled: (settledPage: Int) -> Unit = {},
    onPageChanged: (currentPage: Int) -> Unit = {},
    onApproachingEnd: (currentPage: Int) -> Unit = {},
    endThreshold: Int = 3,
)
```

Uses `LaunchedEffect(pagerState.settledPage)` and `LaunchedEffect(pagerState.currentPage)` internally, lifecycle-aware via `repeatOnLifecycle`.

## Related Code Files

### Create
- `app/src/main/kotlin/com/apero/uikit/ui/components/reel/VerticalReelPager.kt`
- `app/src/main/kotlin/com/apero/uikit/ui/components/reel/ReelPageLifecycleEffect.kt`

### Read (reference only)
- `app/src/main/kotlin/com/apero/uikit/ui/components/carousel/HeroCenterCarousel.kt`
- `app/src/main/kotlin/com/apero/uikit/ui/components/carousel/AutoAdvanceEffect.kt`

## Implementation Steps

1. Create directory `app/src/main/kotlin/com/apero/uikit/ui/components/reel/`
2. Create `ReelPageLifecycleEffect.kt`:
   - Package declaration + imports
   - `@Composable fun ReelPageLifecycleEffect(...)` with `LaunchedEffect` on `settledPage` and `currentPage`
   - Lifecycle-aware via `LocalLifecycleOwner` + `repeatOnLifecycle(RESUMED)`
   - Call `onApproachingEnd` when `currentPage >= pageCount - endThreshold`
   - Call `onPageSettled` when `settledPage` changes
   - Call `onPageChanged` when `currentPage` changes
3. Create `VerticalReelPager.kt`:
   - Package declaration + imports
   - List<T> overload:
     - Early return on empty list
     - `rememberPagerState { items.size }`
     - `VerticalPager` with `beyondViewportPageCount`, `snapAnimationSpec`, `key`
     - Content renders `content(items[page], page == pagerState.currentPage)`
     - Optional indicator overlay at bottom
   - LazyPagingItems<T> overload:
     - Compute `pageCount` from loading state vs itemCount
     - `rememberPagerState { pageCount }`
     - `LaunchedEffect(pageCount)` guard for out-of-bounds currentPage (same as carousel)
     - `LaunchedEffect(pagerState.currentPage)` — prefetch via `items[currentPage + threshold]`
     - VerticalPager content: placeholder during loading, item otherwise
     - Error overlay with retry
     - Optional indicator overlay
4. Run `./gradlew :app:compileDebugKotlin` to verify

## Todo List

- [ ] Create `reel/` directory
- [ ] Implement `ReelPageLifecycleEffect.kt` (~60 lines)
- [ ] Implement `VerticalReelPager.kt` List<T> overload (~80 lines)
- [ ] Implement `VerticalReelPager.kt` LazyPagingItems<T> overload (~90 lines)
- [ ] Verify both files < 200 lines
- [ ] Compile check passes

## Success Criteria

- `VerticalReelPager(items = listOf(...)) { item, isActive -> }` compiles and renders
- `isActive` is `true` only for the currently visible page
- Paging overload triggers prefetch when approaching end
- No new dependencies added to `build.gradle.kts`

## Risk Assessment

| Risk | Mitigation |
|------|-----------|
| VerticalPager `settledPage` API might differ from `currentPage` timing | Test both; `settledPage` for activation, `currentPage` for visual state |
| File exceeds 200 lines with two overloads | Shared internal logic extracted to private composable if needed |
