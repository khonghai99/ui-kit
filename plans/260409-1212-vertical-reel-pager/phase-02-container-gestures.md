# Phase 2 — Page Container + Gestures

## Context Links
- [CarouselItemContainer.kt](../../app/src/main/kotlin/com/apero/uikit/ui/components/carousel/CarouselItemContainer.kt) — reference for slot-based container with gradient
- [Flow ShortVideoPlayer.kt] — source: gesture detection (tap/doubleTap/longPress) + overlay structure
- [Phase 1](./phase-01-core-pager.md) — **blocker**: must complete first

## Overview
- **Priority**: P1
- **Status**: Pending
- **Effort**: 1h
- **Blocked by**: Phase 1

Two composables: `ReelPageContainer` (fullscreen slot-based page layout with gradient overlays) and `ReelGestureDetector` (configurable gesture layer wrapping content).

## Key Insights from Source Analysis

**CarouselItemContainer pattern** (to replicate):
- `@Immutable` data class for defaults
- `Defaults` object with configurable colors/shapes
- `imageContent: @Composable BoxScope.() -> Unit` slot
- `overlayContent: @Composable BoxScope.() -> Unit` slot
- Gradient overlay between image and content layers

**Flow ShortVideoPlayer gesture pattern** (to genericize):
- `pointerInput(Unit) { detectTapGestures(...) }` on fullscreen Box
- `onTap` = toggle play/pause
- `onDoubleTap` = like animation
- `onLongPress` = 2x speed
- `onPress { awaitRelease() }` for cleanup on release
- All callbacks are Flow-specific; we expose generic lambdas

**Flow overlay structure** (to port as slots):
- Top gradient (black 50% -> transparent)
- Bottom gradient (transparent -> black 70%)
- Bottom-left: channel info + title (overlayContent slot)
- Bottom-right: action buttons column (actionsContent slot)

## Requirements

### Functional
- `ReelPageContainer` provides fullscreen box with top/bottom gradient overlays and two content slots
- `ReelGestureDetector` wraps content with configurable `onTap`, `onDoubleTap`, `onLongPress` callbacks
- Both composables work independently (not coupled to VerticalReelPager)

### Non-Functional
- `ReelPageContainer.kt` < 120 lines
- `ReelGestureDetector.kt` < 80 lines

## Architecture

### ReelPageContainer.kt

```kotlin
object ReelPageDefaults {
    val TopGradientColors: List<Color>
    val BottomGradientColors: List<Color>
    val TopGradientHeight: Dp = 120.dp
    val BottomGradientHeight: Dp = 250.dp
}

@Composable
fun ReelPageContainer(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.Black,
    topGradientColors: List<Color> = ReelPageDefaults.TopGradientColors,
    bottomGradientColors: List<Color> = ReelPageDefaults.BottomGradientColors,
    topGradientHeight: Dp = ReelPageDefaults.TopGradientHeight,
    bottomGradientHeight: Dp = ReelPageDefaults.BottomGradientHeight,
    mediaContent: @Composable BoxScope.() -> Unit,
    overlayContent: @Composable BoxScope.() -> Unit = {},
)
```

Layer stack (bottom to top):
1. `mediaContent` — fullscreen (video player, image, etc.)
2. Top gradient box
3. Bottom gradient box
4. `overlayContent` — full box scope for consumer to place text, buttons, etc.

### ReelGestureDetector.kt

```kotlin
@Composable
fun ReelGestureDetector(
    onTap: (() -> Unit)? = null,
    onDoubleTap: (() -> Unit)? = null,
    onLongPress: (() -> Unit)? = null,
    onLongPressRelease: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
)
```

Implementation:
- Wraps content in `Box` with `pointerInput` modifier
- `detectTapGestures(onTap, onDoubleTap, onLongPress)`
- `onPress { try { awaitRelease() } finally { onLongPressRelease?.invoke() } }` pattern from Flow
- All callbacks nullable — no gesture handling if all null

## Related Code Files

### Create
- `app/src/main/kotlin/com/apero/uikit/ui/components/reel/ReelPageContainer.kt`
- `app/src/main/kotlin/com/apero/uikit/ui/components/reel/ReelGestureDetector.kt`

### Read (reference only)
- `app/src/main/kotlin/com/apero/uikit/ui/components/carousel/CarouselItemContainer.kt`

## Implementation Steps

1. Create `ReelPageContainer.kt`:
   - `ReelPageDefaults` object with gradient colors matching Flow's overlay style
   - `@Composable fun ReelPageContainer(...)` — fullscreen Box with:
     - `mediaContent` layer (fillMaxSize)
     - Top gradient Box (fillMaxWidth, height = topGradientHeight, Alignment.TopCenter)
     - Bottom gradient Box (fillMaxWidth, height = bottomGradientHeight, Alignment.BottomCenter)
     - `overlayContent` layer (fillMaxSize, on top of gradients)
2. Create `ReelGestureDetector.kt`:
   - `@Composable fun ReelGestureDetector(...)` — Box wrapping content
   - Apply `Modifier.pointerInput(onTap, onDoubleTap, onLongPress)` with `detectTapGestures`
   - Handle long-press release in `onPress` block
   - Skip `pointerInput` entirely if all callbacks are null (performance)
3. Run `./gradlew :app:compileDebugKotlin` to verify

## Todo List

- [ ] Implement `ReelPageContainer.kt` with gradient overlays and media/overlay slots
- [ ] Implement `ReelGestureDetector.kt` with configurable gesture callbacks
- [ ] Verify both files < 200 lines
- [ ] Compile check passes

## Success Criteria

- `ReelPageContainer` renders fullscreen with visible gradient overlays
- `ReelGestureDetector` fires `onTap` on single tap without conflicting with pager scroll
- `onDoubleTap` fires on double-tap
- `onLongPress` fires on hold, `onLongPressRelease` fires on release
- No coupling to specific media player or action types

## Risk Assessment

| Risk | Mitigation |
|------|-----------|
| Gesture conflicts with VerticalPager vertical scroll | `detectTapGestures` does not consume vertical drag; pager handles that separately |
| Gradient colors look different across themes | Defaults use semi-transparent black; consumer can override via params |
