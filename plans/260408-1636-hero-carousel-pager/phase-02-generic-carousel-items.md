# Phase 02 — Generic Slot-Based Carousel Items

**Priority:** High | **Status:** ✅ Completed

## Context

- Current: [CarouselBannerItem.kt](/Users/haikhong/RepoHub/ui-kit/app/src/main/kotlin/com/apero/uikit/ui/components/CarouselBannerItem.kt) — hardcoded to `GalleryImage`
- Reference: [animeko CarouselItem.kt](/Users/haikhong/RepoHub/animeko/app/shared/ui-foundation/src/commonMain/kotlin/ui/foundation/layout/CarouselItem.kt)

## Goal

Make carousel item a generic container with:
- Configurable gradient overlay
- Slot for image content
- Slot for text overlay (title + subtitle)
- Configurable shape, colors, sizing
- Keep `CarouselBannerItem` as convenience wrapper using the generic component

## Files to Modify

- Create: `app/src/main/kotlin/com/apero/uikit/ui/components/carousel/CarouselItemContainer.kt`
- Refactor: `CarouselBannerItem.kt` → delegate to `CarouselItemContainer`

## Implementation Steps

### 1. Create CarouselItemContainer.kt

```kotlin
@Composable
fun CarouselItemContainer(
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.extraLarge,
    gradientColors: List<Color> = DefaultGradientColors,
    imageContent: @Composable BoxScope.() -> Unit,
    overlayContent: @Composable BoxScope.() -> Unit = {},
)
```

Key features:
- `imageContent` slot: caller provides AsyncImage, placeholder, etc.
- `overlayContent` slot: text, badges, play button, etc.
- Default gradient: `[Transparent, Transparent, Black.copy(0.612f)]` (animeko value)
- Shape clip on entire container

### 2. Create CarouselItemDefaults

```kotlin
object CarouselItemDefaults {
    val Shape @Composable get() = MaterialTheme.shapes.extraLarge
    val ImageHeight = 213.dp
    val PreferredWidth = 300.dp  // can be overridden per WindowSizeClass
    val GradientColors = listOf(
        Color.Transparent, Color.Transparent, Color.Black.copy(alpha = 0.612f)
    )
}
```

### 3. Refactor CarouselBannerItem

Keep as a convenience composable that uses `CarouselItemContainer`:
```kotlin
@Composable
fun CarouselBannerItem(image: GalleryImage, modifier: Modifier = Modifier) {
    CarouselItemContainer(
        modifier = modifier,
        imageContent = { OptimizedAsyncImage(...) },
        overlayContent = { Text(image.title, ...) },
    )
}
```

## Todo

- [x] Create `carousel/CarouselItemContainer.kt` with slots
- [x] Create `CarouselItemDefaults` object
- [x] Refactor `CarouselBannerItem.kt` to use `CarouselItemContainer`
- [x] Verify no visual regression

## Success Criteria

- `CarouselItemContainer` works with any image loader, any overlay
- `CarouselBannerItem` looks identical to current version
- Compile passes
