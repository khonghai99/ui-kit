---
phase: 1
title: "Add infinite scroll to HeroCenterCarousel"
status: pending
effort: 1h
---

# Phase 1: Add Infinite Scroll to HeroCenterCarousel

## Context

- [HeroCenterCarousel.kt](../../app/src/main/kotlin/com/apero/uikit/ui/components/carousel/HeroCenterCarousel.kt) — modify
- [AutoAdvanceEffect.kt](../../app/src/main/kotlin/com/apero/uikit/ui/components/carousel/AutoAdvanceEffect.kt) — no changes needed
- [GalleryScreen.kt](../../app/src/main/kotlin/com/apero/uikit/ui/screen/GalleryScreen.kt) — modify: enable infinite scroll in demo

## Overview

Add `infiniteScroll: Boolean = false` parameter to the `List<T>` overload of `HeroCenterCarousel`. When enabled, uses virtual page count pattern to create seamless looping in both directions.

## Implementation Steps

### 1. Add constant and parameter to HeroCenterCarousel.kt

Add at file level:
```kotlin
/** Multiplier for virtual page count in infinite scroll mode. */
private const val INFINITE_SCROLL_PAGES = 10000
```

Add parameter to `List<T>` overload:
```kotlin
fun <T> HeroCenterCarousel(
    items: List<T>,
    modifier: Modifier = Modifier,
    infiniteScroll: Boolean = false,  // NEW
    autoAdvance: Boolean = true,
    ...
)
```

### 2. Compute virtual page count and start index

Inside the `List<T>` overload, replace:
```kotlin
val carouselState = rememberCarouselState(initialItem = 0) { items.size }
```

With:
```kotlin
val actualSize = items.size
val shouldLoop = infiniteScroll && actualSize >= 2
val virtualCount = if (shouldLoop) actualSize * INFINITE_SCROLL_PAGES else actualSize
val startIndex = if (shouldLoop) (INFINITE_SCROLL_PAGES / 2) * actualSize else 0

val carouselState = rememberCarouselState(initialItem = startIndex) { virtualCount }
```

### 3. Map virtual index to actual item

Replace content lambda:
```kotlin
// Before:
) { index ->
    content(items[index])
}

// After:
) { virtualIndex ->
    content(items[if (shouldLoop) virtualIndex % actualSize else virtualIndex])
}
```

### 4. Enable in GalleryScreen demo

```kotlin
HeroCenterCarousel(
    items = trending,
    infiniteScroll = true,  // ADD
) { image ->
    CarouselBannerItem(image = image)
}
```

## Why AutoAdvanceEffect Needs No Changes

With virtual page count:
- `pageCount` = 50000 (for 5 items)
- `currentPage + 1` → next virtual page → maps to next actual item via `% actualSize`
- Last-page detection (`lastItem.index == pageCount - 1`) never fires — we're never near virtual edge
- Wrap-around code path is unreachable but harmless (no dead code removal needed)

For non-infinite carousels (LazyPagingItems), auto-advance works exactly as before.

## Files Modified

- `app/src/main/kotlin/com/apero/uikit/ui/components/carousel/HeroCenterCarousel.kt`
- `app/src/main/kotlin/com/apero/uikit/ui/screen/GalleryScreen.kt`

## Files Read (no changes)

- `app/src/main/kotlin/com/apero/uikit/ui/components/carousel/AutoAdvanceEffect.kt`

## Todo

- [ ] Add `INFINITE_SCROLL_PAGES` constant
- [ ] Add `infiniteScroll` parameter to List<T> overload
- [ ] Compute virtual count + start index
- [ ] Map virtual index → actual index in content lambda
- [ ] Enable `infiniteScroll = true` in GalleryScreen demo
- [ ] Compile and verify
- [ ] Manual test: swipe forward past last item → loops to first
- [ ] Manual test: swipe backward past first item → loops to last
- [ ] Manual test: auto-advance loops smoothly without jump

## Success Criteria

- Seamless forward loop (no visible snap-back)
- Seamless backward loop
- Auto-advance works indefinitely
- No regression when `infiniteScroll = false`
- Build passes
