---
title: "Vertical Reel Pager Component"
description: "Port Reel/Shorts vertical pager pattern from Flow into ui-kit as generic composable components"
status: pending
priority: P1
effort: 5h
branch: feat/hero-carousel-pager
tags: [compose, pager, reel, component-library]
created: 2026-04-09
---

# Vertical Reel Pager — Implementation Plan

## Goal

Add a `VerticalReelPager` component family to ui-kit matching the API style of existing `carousel/` components. Port the **UI pattern** from Flow's Shorts feature — NOT the YouTube/media-specific code.

## Architecture Overview

```
reel/
  VerticalReelPager.kt      — Main pager (List<T> + LazyPagingItems<T> overloads)
  ReelPageContainer.kt      — Fullscreen page container with overlay slots + gradient
  ReelGestureDetector.kt    — Configurable tap/double-tap/long-press gesture layer
  ReelProgressIndicator.kt  — Linear progress bar with optional scrubbing
  ReelPlayerPool.kt         — Generic interface for N-player pooling (no Media3 dep)
  ReelPageLifecycleEffect.kt — Page active/settled lifecycle side-effects
```

## Data Flow

```
List<T> or LazyPagingItems<T>
        |
        v
  VerticalReelPager (VerticalPager + beyondViewportPageCount=1)
        |
        +---> ReelPageLifecycleEffect (isActive, onSettled, onLoadMore)
        |
        +---> ReelPageContainer (fullscreen box + gradient overlay)
        |         |
        |         +---> mediaContent slot (consumer-provided: video, image, etc.)
        |         +---> overlayContent slot (consumer-provided: text, buttons, etc.)
        |
        +---> ReelGestureDetector (tap/doubleTap/longPress callbacks)
        |
        +---> ReelProgressIndicator (LinearProgressIndicator + optional scrub)

Consumer provides:
  - ReelPlayerPool<P> implementation (optional, for video use cases)
  - Content lambdas for media + overlay slots
  - Gesture callbacks
```

## Phases

| # | Phase | Files | Effort | Status |
|---|-------|-------|--------|--------|
| 1 | [Core Pager + Lifecycle](./phase-01-core-pager.md) | VerticalReelPager.kt, ReelPageLifecycleEffect.kt | 1.5h | Pending |
| 2 | [Page Container + Gestures](./phase-02-container-gestures.md) | ReelPageContainer.kt, ReelGestureDetector.kt | 1h | Pending |
| 3 | [Progress + Player Pool Interface](./phase-03-progress-player-pool.md) | ReelProgressIndicator.kt, ReelPlayerPool.kt | 1h | Pending |
| 4 | [Demo Integration](./phase-04-demo-integration.md) | GalleryScreen.kt, GalleryViewModel.kt, MockData.kt, model | 1h | Pending |
| 5 | [Verification + Polish](./phase-05-verification.md) | All files | 0.5h | Pending |

## Dependency Graph

```
Phase 1 (Core Pager)
    |
    +---> Phase 2 (Container + Gestures)
    |         |
    |         +---> Phase 4 (Demo)
    |
    +---> Phase 3 (Progress + Player Pool)
              |
              +---> Phase 4 (Demo)

Phase 4 ---> Phase 5 (Verification)
```

Phases 2 and 3 can run in parallel after Phase 1 completes.

## Key Constraints

1. **No new heavy dependencies** — NO Media3/ExoPlayer in core components. Demo uses images or simple AndroidView+MediaPlayer.
2. **<200 lines per file** — Modularize exactly like carousel/ does.
3. **Match carousel/ API style** — Generic `<T>`, two overloads (List + LazyPagingItems), slot-based.
4. **minSdk 24** — No API 29+ only calls.
5. **Package**: `com.apero.uikit.ui.components.reel`

## Risk Assessment

| Risk | Likelihood | Impact | Mitigation |
|------|-----------|--------|------------|
| VerticalPager fling behavior needs tuning | Medium | Low | Expose `snapAnimationSpec` param like HeroCenterCarousel |
| Player pool interface too abstract/useless | Low | Medium | Keep interface minimal (3 methods); demo proves it works |
| Gesture conflicts with VerticalPager scroll | Medium | Medium | Use `pointerInput` with proper `detectTapGestures` (Flow pattern proven) |
| File count grows if gestures are complex | Low | Low | ReelGestureDetector is a single composable wrapper |

## Rollback

Each phase adds NEW files only (no existing file modifications until Phase 4). Phase 4 modifications to GalleryScreen/ViewModel are additive (new section in grid). Revert = delete `reel/` directory + undo Phase 4 changes.

## Success Criteria

- [ ] `VerticalReelPager` renders with List<T> and scrolls vertically page-by-page
- [ ] LazyPagingItems overload loads more items near end of list
- [ ] Gesture callbacks (tap, doubleTap, longPress) fire correctly
- [ ] Progress indicator animates with provided progress value
- [ ] Demo screen shows vertical reel section with images
- [ ] All files <200 lines
- [ ] `./gradlew :app:compileDebugKotlin` passes
