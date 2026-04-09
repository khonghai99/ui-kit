# Hero Carousel Pager — Implementation Plan

**Branch:** `feat/hero-carousel-pager`
**Reference:** [animeko TrendingSubjectsCarousel](https://github.com/open-ani/animeko)
**Goal:** Extract animeko's carousel patterns into reusable, generic components for Apero apps

## Status Overview

| Phase | Description | Status |
|-------|-------------|--------|
| 01 | Upgrade AutoAdvanceEffect | ✅ Completed |
| 02 | Generic slot-based carousel items | ✅ Completed |
| 03 | HeroCenterCarousel component | ✅ Completed |
| 04 | Pager indicators | ✅ Completed |
| 05 | Demo integration & verify | ✅ Completed |

## Architecture

```
ui/components/carousel/
├── AutoAdvanceEffect.kt       # Phase 1 — enhanced auto-scroll
├── CarouselMotionScheme.kt    # Phase 1 — animation specs
├── CarouselItemScope.kt       # Phase 2 — generic item with gradient
├── PagerIndicator.kt          # Phase 4 — dot/line indicators
└── HeroCenterCarousel.kt      # Phase 3 — main carousel composable

ui/screen/
└── GalleryScreen.kt           # Phase 5 — updated demo
```

## Key Decisions

1. **Keep HorizontalPager** (not Material3 Carousel API) — Material3 `HorizontalCenteredHeroCarousel` requires M3 1.4+ experimental, complex API. HorizontalPager + manual scale effect is simpler, more controllable, and already working.
2. **Slot-based content** — `content: @Composable (index: Int) -> Unit` lambda, not hardcoded model.
3. **Support both List and LazyPagingItems** — two overloads.
