# Phase 03 — HeroCenterCarousel Component

**Priority:** High | **Status:** ✅ Completed

## Context

- Current: [BannerCarousel.kt](/Users/haikhong/RepoHub/ui-kit/app/src/main/kotlin/com/apero/uikit/ui/components/BannerCarousel.kt) — hardcoded to `List<GalleryImage>`, basic scale effect
- Reference: [animeko TrendingSubjectsCarousel.kt](/Users/haikhong/RepoHub/animeko/app/shared/ui-exploration/src/commonMain/kotlin/ui/exploration/trends/TrendingSubjectsCarousel.kt)

## Goal

Generic hero-center carousel using HorizontalPager with:
- Slot-based content (not tied to any model)
- Support `List<T>` and `LazyPagingItems<T>` inputs
- Hero-center scale effect (peek side items, center item larger)
- Hover-to-pause integration
- Configurable: contentPadding, pageSpacing, animationSpec, auto-advance toggle

## Files

- Rename + upgrade: `BannerCarousel.kt` → `carousel/HeroCenterCarousel.kt`
- Keep `BannerCarousel.kt` as thin wrapper for backward compat (or delete if unused outside demo)

## Implementation Steps

### 1. Create HeroCenterCarousel.kt

```kotlin
@Composable
fun <T> HeroCenterCarousel(
    items: List<T>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 32.dp),
    pageSpacing: Dp = 12.dp,
    autoAdvance: Boolean = true,
    autoAdvancePeriod: Duration = 3.seconds,
    key: ((index: Int) -> Any)? = null,
    content: @Composable (item: T) -> Unit,
)
```

Core logic:
- `HorizontalPager` with `contentPadding` for peek effect
- Hero scale: `lerp(1f, 0.9f, pageOffset)` on side items (keep current logic)
- `AutoAdvanceEffect(enabled = autoAdvance && !isHovered, ...)`
- Hover detection via `MutableInteractionSource.collectIsHoveredAsState()`

### 2. LazyPagingItems overload

```kotlin
@Composable
fun <T : Any> HeroCenterCarousel(
    items: LazyPagingItems<T>,
    // same params
    placeholderCount: Int = 8,
    placeholder: @Composable () -> Unit = { ShimmerPlaceholder() },
    content: @Composable (item: T) -> Unit,
)
```

Handles:
- Show `placeholderCount` shimmer items during `isLoadingFirstPageOrRefreshing`
- Null-safe item access: `items[index]?.let { content(it) } ?: placeholder()`

### 3. Update BannerCarousel.kt

Thin wrapper:
```kotlin
@Composable
fun BannerCarousel(items: List<GalleryImage>, modifier: Modifier = Modifier) {
    HeroCenterCarousel(items = items, modifier = modifier) { image ->
        CarouselBannerItem(image = image)
    }
}
```

## Todo

- [x] Create `carousel/HeroCenterCarousel.kt` with generic List overload
- [x] Add LazyPagingItems overload
- [x] Add hover-to-pause integration
- [x] Update `BannerCarousel.kt` as thin wrapper
- [x] Verify hero scale effect works identically

## Success Criteria

- Generic carousel works with any data type
- Demo screen renders same as before
- Auto-advance pauses on hover (testable on emulator with mouse)
- Compile passes
