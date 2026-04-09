# Phase 5 — Verification + Polish

## Context Links
- [plan.md](./plan.md) — overall success criteria
- All phase files for checklist cross-reference
- [app/build.gradle.kts](../../app/build.gradle.kts) — verify no new dependencies added

## Overview
- **Priority**: P1
- **Status**: Pending
- **Effort**: 0.5h
- **Blocked by**: Phase 4

Final verification pass. Compile, line-count audit, API consistency review, and polish.

## Verification Checklist

### 1. Build Verification
- [ ] `./gradlew :app:compileDebugKotlin` — BUILD SUCCESSFUL
- [ ] `./gradlew :app:compileReleaseKotlin` — BUILD SUCCESSFUL (proguard compat)
- [ ] No new warnings related to reel/ files

### 2. File Size Audit (< 200 lines each)
- [ ] `reel/VerticalReelPager.kt`
- [ ] `reel/ReelPageLifecycleEffect.kt`
- [ ] `reel/ReelPageContainer.kt`
- [ ] `reel/ReelGestureDetector.kt`
- [ ] `reel/ReelProgressIndicator.kt`
- [ ] `reel/ReelPlayerPool.kt`
- [ ] `screen/ReelDemoScreen.kt`

Command: `wc -l app/src/main/kotlin/com/apero/uikit/ui/components/reel/*.kt`

### 3. Dependency Audit
- [ ] `build.gradle.kts` has NO new `implementation` lines added for this feature
- [ ] No Media3 / ExoPlayer imports anywhere in `reel/` directory
- [ ] No Flow-specific imports (io.github.aedev.flow) anywhere

Command: `grep -r "media3\|exoplayer\|io.github.aedev" app/src/main/kotlin/com/apero/uikit/ui/components/reel/`

### 4. API Consistency Review
- [ ] `VerticalReelPager` follows same generic `<T>` pattern as `HeroCenterCarousel`
- [ ] Both `List<T>` and `LazyPagingItems<T>` overloads present
- [ ] Default parameter values use Material3 theme where appropriate
- [ ] `Modifier` is always second parameter after required params
- [ ] All public composables have KDoc
- [ ] `@Immutable` on data classes, `@Composable` annotations correct

### 5. Package Structure
Expected final structure:
```
ui/components/reel/
    VerticalReelPager.kt
    ReelPageLifecycleEffect.kt
    ReelPageContainer.kt
    ReelGestureDetector.kt
    ReelProgressIndicator.kt
    ReelPlayerPool.kt
```

- [ ] All files in `com.apero.uikit.ui.components.reel` package
- [ ] No files outside `reel/` directory modified except: GalleryScreen, GalleryViewModel, MockData, GalleryRepository, MainActivity

### 6. Functional Smoke Test (Manual)
- [ ] Launch app → GalleryScreen loads with carousel demos + "Open Reel Demo" button
- [ ] Tap button → ReelDemoScreen opens with first reel page visible
- [ ] Swipe up → next page snaps into view
- [ ] Swipe down → previous page snaps back
- [ ] Single tap on page → visual feedback / log
- [ ] Double tap on page → visual feedback
- [ ] Progress bar animates at bottom of each page
- [ ] Back button → returns to GalleryScreen
- [ ] Gradient overlays visible (top dark fade, bottom dark fade)
- [ ] Images load correctly (portrait aspect ratio fills screen)

### 7. Edge Cases
- [ ] Fast scrolling through all pages — no crash
- [ ] Rotate device — reel survives config change (basic)
- [ ] Empty list — `VerticalReelPager(items = emptyList())` returns without crash

## Polish Items (if time permits)
- Add `@Preview` composable to `ReelPageContainer` for IDE preview
- Ensure progress indicator animates smoothly (no jank at 0→1 reset)
- Verify `isActive` correctly toggles to false for non-visible pages

## Implementation Steps

1. Run compile checks (debug + release)
2. Run `wc -l` on all reel files
3. Run grep for forbidden imports
4. Visual audit of API signatures against HeroCenterCarousel
5. Fix any issues found
6. Re-compile if fixes were needed

## Success Criteria

All checkboxes above are checked. The feature is complete when:
1. Both builds pass
2. All files under line limit
3. No forbidden dependencies
4. API matches library style
5. Demo works end-to-end
