# Phase 3 — Progress Indicator + Player Pool Interface

## Context Links
- [DotIndicator.kt](../../app/src/main/kotlin/com/apero/uikit/ui/components/carousel/DotIndicator.kt) — reference for indicator composable style
- [LineIndicator.kt](../../app/src/main/kotlin/com/apero/uikit/ui/components/carousel/LineIndicator.kt) — reference for indicator composable style
- [Flow ShortsPlayerPool.kt](../../../Flow/app/src/main/java/io/github/aedev/flow/player/shorts/ShortsPlayerPool.kt) — source: 3-player pool with activate/prepare/release
- [Phase 1](./phase-01-core-pager.md) — **blocker**: must complete first

## Overview
- **Priority**: P1
- **Status**: Complete
- **Effort**: 1h
- **Blocked by**: Phase 1
- **Parallel with**: Phase 2

Two files: `ReelProgressIndicator` (linear progress bar with optional scrub) and `ReelPlayerPool` (generic interface abstracting the N-player pool pattern).

## Key Insights from Source Analysis

**Existing indicator pattern** (Dot/Line):
- Accept `PagerState` + `pageCount` + color params
- Use Material3 theme colors as defaults
- Pure composables, no side-effects
- `ReelProgressIndicator` differs: it tracks playback progress, NOT pager position. It does NOT need PagerState.

**Flow ShortsPlayerPool** (to abstract):
- 3 ExoPlayer instances, modulo-mapped to content indices
- Key operations: `prepare(index, id, url, autoPlay)`, `activatePlayer(index)`, `releaseUnusedPlayers(currentIndex)`, `release()`
- Singleton pattern (Flow-specific; generic interface won't mandate this)
- We extract the **contract**, not the implementation. Media3 is consumer's choice.

**Flow progress bar pattern** (from ShortVideoPlayer):
- `currentPosition / duration` as float progress
- `isDragging` + `dragProgress` for scrub state
- `BoxWithConstraints` for scrub hit area
- `LinearProgressIndicator` for the visual bar

## Requirements

### Functional
- `ReelProgressIndicator`: horizontal progress bar at bottom of page
  - Accepts `progress: Float` (0..1), NOT pager state
  - Optional `onSeek: (Float) -> Unit` callback for scrubbing
  - Configurable colors, height, track shape
- `ReelPlayerPool<P>`: generic interface for N-player pooling
  - Type param `P` = player type (ExoPlayer, MediaPlayer, any)
  - Methods: `getPlayer(index)`, `activate(index)`, `prepare(index, shouldPlay)`, `releaseDistant(currentIndex)`, `releaseAll()`

### Non-Functional
- `ReelProgressIndicator.kt` < 120 lines
- `ReelPlayerPool.kt` < 60 lines
- No new dependencies (especially NO Media3)

## Architecture

### ReelProgressIndicator.kt

```kotlin
object ReelProgressDefaults {
    val Height: Dp = 2.dp
    val ScrubHeight: Dp = 20.dp  // Touch target when scrubbable
    val TrackColor: @Composable () -> Color = { MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f) }
    val ProgressColor: @Composable () -> Color = { MaterialTheme.colorScheme.primary }
}

@Composable
fun ReelProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier,
    progressColor: Color = ReelProgressDefaults.ProgressColor(),
    trackColor: Color = ReelProgressDefaults.TrackColor(),
    height: Dp = ReelProgressDefaults.Height,
    onSeek: ((Float) -> Unit)? = null,
)
```

When `onSeek` is null: simple `LinearProgressIndicator` — no interaction.
When `onSeek` is provided: wraps in `BoxWithConstraints` with horizontal drag gesture. Drag position mapped to 0..1 float and passed to `onSeek`. Visual thumb appears during drag.

### ReelPlayerPool.kt

```kotlin
/**
 * Generic interface for an N-player pool used with VerticalReelPager.
 *
 * Consumers implement this for their specific player type (Media3, MediaPlayer, etc.).
 * The pool manages a fixed number of player instances and maps content indices to
 * player slots via modulo arithmetic.
 *
 * @param P The player type (e.g., ExoPlayer, MediaPlayer)
 */
interface ReelPlayerPool<P> {
    /** Get the player instance for the given content index. */
    fun getPlayer(index: Int): P?

    /** Activate the player at [index] (play) and pause all others. */
    fun activate(index: Int)

    /** Release players far from [currentIndex] to free resources. */
    fun releaseDistant(currentIndex: Int)

    /** Release all players. Call when leaving the reel screen. */
    fun releaseAll()
}
```

Deliberately minimal. Flow's `prepare(index, id, url, audioUrl, shouldPlay)` is too media-specific. Consumers call `getPlayer(index)` and configure it themselves. The interface only manages the lifecycle/activation pattern.

## Related Code Files

### Create
- `app/src/main/kotlin/com/apero/uikit/ui/components/reel/ReelProgressIndicator.kt`
- `app/src/main/kotlin/com/apero/uikit/ui/components/reel/ReelPlayerPool.kt`

### Read (reference only)
- `app/src/main/kotlin/com/apero/uikit/ui/components/carousel/DotIndicator.kt`
- `app/src/main/kotlin/com/apero/uikit/ui/components/carousel/LineIndicator.kt`

## Implementation Steps

1. Create `ReelPlayerPool.kt`:
   - Package declaration
   - KDoc explaining the pool concept and usage
   - `interface ReelPlayerPool<P>` with 4 methods
   - No implementation — pure contract
2. Create `ReelProgressIndicator.kt`:
   - `ReelProgressDefaults` object with default colors/dimensions
   - `@Composable fun ReelProgressIndicator(...)`:
     - When `onSeek == null`: `LinearProgressIndicator(progress, ...)` with `fillMaxWidth()` and `height`
     - When `onSeek != null`: `BoxWithConstraints` wrapping the indicator with:
       - `Modifier.height(ScrubHeight)` for touch target
       - `pointerInput` with `detectHorizontalDragGestures` mapping drag X to 0..1
       - Local `isDragging` + `dragProgress` state
       - Show `progress` normally, show `dragProgress` while dragging
       - Call `onSeek(dragProgress)` on drag end
3. Run `./gradlew :app:compileDebugKotlin` to verify

## Todo List

- [x] Implement `ReelPlayerPool.kt` interface (~40 lines)
- [x] Implement `ReelProgressIndicator.kt` with non-scrub and scrub modes (~110 lines)
- [x] Verify both files < 200 lines
- [x] Compile check passes

## Success Criteria

- `ReelPlayerPool<ExoPlayer>` compiles without importing Media3 in the interface file
- `ReelProgressIndicator(progress = 0.5f)` renders a half-filled bar
- `ReelProgressIndicator(progress = 0.5f, onSeek = { })` enables drag-to-seek
- No new dependencies in `build.gradle.kts`

## Risk Assessment

| Risk | Mitigation |
|------|-----------|
| ReelPlayerPool too thin to be useful | Intentional — YAGNI. Add methods only when a real consumer needs them. Interface exists to document the pattern. |
| Scrub gesture conflicts with VerticalPager | Progress bar is at bottom edge; drag is horizontal. Pager only consumes vertical drags. No conflict. |
| LinearProgressIndicator M3 API change | Using stable API (`progress` lambda variant from M3 1.2+). BOM pins version. |
