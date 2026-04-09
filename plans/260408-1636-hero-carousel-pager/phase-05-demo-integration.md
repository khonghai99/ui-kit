# Phase 05 — Demo Integration & Verify

**Priority:** Medium | **Status:** ✅ Completed

## Goal

Update GalleryScreen to showcase all new carousel features. Serve as living documentation.

## Files to Modify

- `app/src/main/kotlin/com/apero/uikit/ui/screen/GalleryScreen.kt`

## Implementation Steps

### 1. Update GalleryScreen

Show 3 demo sections:
1. **Hero Carousel with DotIndicator** — current trending banner
2. **Hero Carousel with LineIndicator** — same data, different indicator
3. **Grid** — existing grid below

### 2. Showcase carousel variants

```kotlin
// Variant 1: Dots
HeroCenterCarousel(items = trending, indicator = { state, count ->
    DotIndicator(state, count)
}) { image ->
    CarouselBannerItem(image)
}

// Variant 2: Lines
HeroCenterCarousel(items = trending, indicator = { state, count ->
    LineIndicator(state, count)
}) { image ->
    CarouselBannerItem(image)
}
```

### 3. Build & run verification

- `./gradlew :app:compileDebugKotlin` — compile check
- `./gradlew :app:assembleDebug` — full build
- Manual: verify auto-scroll, indicators, hover pause

## Todo

- [x] Update GalleryScreen with carousel variants
- [x] Compile check
- [x] Full assembleDebug
- [x] Visual verify on emulator

## Success Criteria

- App runs with both carousel variants visible
- Auto-advance works: 3s interval, pauses on touch, wraps around
- Indicators animate smoothly with scroll
- No crashes, no frame drops
