# Phase 4 — Demo Integration

## Context Links
- [GalleryScreen.kt](../../app/src/main/kotlin/com/apero/uikit/ui/screen/GalleryScreen.kt) — modify: add reel demo section
- [GalleryViewModel.kt](../../app/src/main/kotlin/com/apero/uikit/ui/screen/GalleryViewModel.kt) — modify: add reel data source
- [MockData.kt](../../app/src/main/kotlin/com/apero/uikit/data/MockData.kt) — modify: add reel mock items
- [GalleryImage.kt](../../app/src/main/kotlin/com/apero/uikit/model/GalleryImage.kt) — read: existing model pattern
- [Phase 1](./phase-01-core-pager.md), [Phase 2](./phase-02-container-gestures.md), [Phase 3](./phase-03-progress-player-pool.md) — **blockers**

## Overview
- **Priority**: P1
- **Status**: Complete
- **Effort**: 1h
- **Blocked by**: Phases 1, 2, 3

Wire all reel components into the demo app. Add a "Vertical Reel" section to GalleryScreen that showcases `VerticalReelPager` + `ReelPageContainer` + `ReelGestureDetector` + `ReelProgressIndicator` using static image data (no video).

## Key Insights

**Existing demo pattern** (GalleryScreen):
- `LazyVerticalGrid` with `GridItemSpan(maxLineSpan)` for full-width sections
- Each section: `Text` title + component
- ViewModel exposes `StateFlow<List<T>>` and `Flow<PagingData<T>>`
- Repository returns from `MockData`

**Demo strategy**: Use tall portrait images (9:16 ratio) from Picsum to simulate reel content. No video. This proves the component works without adding Media3 dependency.

**Navigation concern**: VerticalReelPager is fullscreen by nature. Embedding it in a LazyVerticalGrid item won't work well — the reel needs its own screen. Options:
- **Option A**: Fixed-height preview (e.g., 400dp) in the grid, tap to open fullscreen — complex, needs navigation
- **Option B**: Add a button in the grid that navigates to a dedicated `ReelDemoScreen` — clean separation
- **Chosen: Option B** — matches real-world usage, keeps GalleryScreen changes minimal

## Requirements

### Functional
- New `ReelDemoScreen` composable showing `VerticalReelPager` with mock images
- Each page uses `ReelPageContainer` with image as `mediaContent` and title/index overlay
- `ReelGestureDetector` wrapping each page with toast/log on tap/doubleTap
- `ReelProgressIndicator` at bottom of each page with simulated progress (auto-advancing)
- Button in `GalleryScreen` grid to navigate to `ReelDemoScreen`
- New `ReelItem` model or reuse `GalleryImage` with portrait URLs

### Non-Functional
- `ReelDemoScreen.kt` < 150 lines
- Changes to existing files are additive only (no deletions)
- No new dependencies

## Architecture

### Data Flow

```
MockData.reelImages (List<GalleryImage> with 9:16 portrait URLs)
    |
    v
GalleryViewModel.reelImages: StateFlow<List<GalleryImage>>
    |
    v
ReelDemoScreen
    |
    +---> VerticalReelPager(items = reelImages)
              |
              +---> ReelGestureDetector(onTap, onDoubleTap)
              |         |
              |         +---> ReelPageContainer
              |                   mediaContent: OptimizedAsyncImage
              |                   overlayContent: title + index text
              |
              +---> ReelProgressIndicator (simulated auto-progress)
```

### File Changes

**New files:**
- `app/src/main/kotlin/com/apero/uikit/ui/screen/ReelDemoScreen.kt`

**Modified files:**
- `MockData.kt` — add `reelImages` list (portrait Picsum URLs)
- `GalleryViewModel.kt` — expose `reelImages: StateFlow<List<GalleryImage>>`
- `GalleryScreen.kt` — add "Reel Demo" button with navigation callback
- `MainActivity.kt` — add simple navigation (or use a `var showReel` state toggle)

### Navigation Approach

Simplest approach: `MainActivity` holds a `var currentScreen by remember { mutableStateOf("gallery") }` and conditionally renders `GalleryScreen` or `ReelDemoScreen`. Pass `onNavigateToReel` and `onBack` lambdas. No Navigation library needed for a demo.

Check current `MainActivity.kt` to confirm:

```kotlin
// Current: just renders GalleryScreen
// After: conditional rendering based on state
```

## Implementation Steps

1. **MockData.kt** — add `reelImages`:
   ```kotlin
   val reelImages: List<GalleryImage> = listOf(
       GalleryImage("r1", "Waterfall", "https://picsum.photos/id/1015/1080/1920", "..."),
       GalleryImage("r2", "Forest Path", "https://picsum.photos/id/1018/1080/1920", "..."),
       // ... 8-10 items with 1080x1920 portrait aspect
   )
   ```

2. **GalleryViewModel.kt** — add:
   ```kotlin
   val reelImages: StateFlow<List<GalleryImage>> = repository.getReelImages()
       .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
   ```

3. **GalleryRepository.kt** — add:
   ```kotlin
   fun getReelImages(): Flow<List<GalleryImage>> = flowOf(MockData.reelImages)
   ```

4. **Create `ReelDemoScreen.kt`**:
   - Accept `reelImages: List<GalleryImage>` and `onBack: () -> Unit`
   - `Scaffold` with `TopAppBar` (back button + "Reel Demo" title)
   - `VerticalReelPager(items = reelImages)` filling remaining space
   - Each page:
     - `ReelGestureDetector(onTap = { /* toggle pause state */ }, onDoubleTap = { /* show heart */ })`
     - `ReelPageContainer(mediaContent = { AsyncImage(...) }, overlayContent = { title + page number })`
     - `ReelProgressIndicator(progress = animatedProgress)` at bottom
   - Simulated progress: `LaunchedEffect(isActive)` incrementing a float from 0 to 1 over 5 seconds, looping

5. **GalleryScreen.kt** — add in the grid before "Gallery" section:
   ```kotlin
   // Reel demo button
   Button(onClick = onNavigateToReel) { Text("Open Reel Demo") }
   ```
   Add `onNavigateToReel: () -> Unit = {}` parameter to `GalleryScreen`.

6. **MainActivity.kt** — add navigation state:
   ```kotlin
   var currentScreen by remember { mutableStateOf("gallery") }
   when (currentScreen) {
       "gallery" -> GalleryScreen(onNavigateToReel = { currentScreen = "reel" })
       "reel" -> {
           val vm: GalleryViewModel = hiltViewModel()
           val reelImages by vm.reelImages.collectAsStateWithLifecycle()
           ReelDemoScreen(reelImages = reelImages, onBack = { currentScreen = "gallery" })
       }
   }
   ```

7. Run `./gradlew :app:compileDebugKotlin` to verify

## Todo List

- [x] Add `reelImages` to `MockData.kt`
- [x] Add `getReelImages()` to `GalleryRepository.kt`
- [x] Add `reelImages` flow to `GalleryViewModel.kt`
- [x] Create `ReelDemoScreen.kt` with all reel components
- [x] Add navigation button to `GalleryScreen.kt`
- [x] Update `MainActivity.kt` with screen toggle
- [x] Compile check passes
- [x] Manual verification: reel scrolls vertically, gestures fire, progress animates

## Success Criteria

- Tapping "Open Reel Demo" in gallery navigates to fullscreen reel
- Reel pages swipe vertically with snap behavior
- Each page shows a portrait image with title overlay and gradient
- Single tap logs/toasts "Tap on page X"
- Double tap shows brief visual feedback
- Progress bar auto-animates from 0 to 1 and loops
- Back button returns to gallery
- All files < 200 lines

## Risk Assessment

| Risk | Mitigation |
|------|-----------|
| Portrait Picsum images return 404 for some IDs | Use known-good IDs; Coil shows placeholder on failure |
| Simulated progress looks artificial | Acceptable for demo; real apps provide actual playback progress |
| Navigation state lost on config change | `rememberSaveable` for `currentScreen` string; trivial for demo |
