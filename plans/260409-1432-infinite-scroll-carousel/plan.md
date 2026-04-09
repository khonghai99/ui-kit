---
title: "Infinite Scroll Carousel"
description: "Add infinite (looping) scroll to HeroCenterCarousel using virtual page count pattern"
status: pending
priority: P1
effort: 1h
branch: feat/hero-carousel-pager
tags: [compose, carousel, infinite-scroll, m3]
created: 2026-04-09
blockedBy: []
blocks: []
---

# Infinite Scroll Carousel — Implementation Plan

## Goal

Make `HeroCenterCarousel` scroll infinitely (loop) in both directions for `List<T>` items. User swipes past last item → continues to first item seamlessly. Swipes before first item → loops to last item.

## Approach: Virtual Page Count Pattern

Standard technique for infinite pagers in Compose:

```
Actual items:  [A, B, C, D, E]  (size = 5)
Virtual pages: [...A,B,C,D,E,A,B,C,D,E,A,B,C,D,E...]  (size = 5 * 10000 = 50000)
Start index:   25000 (center of virtual space, aligned to item A)
Content map:   virtualIndex % actualSize → actual item index
```

User sees seamless looping. Auto-advance naturally increments — no wrap-around needed.

## Phases

| # | Phase | Files | Status |
|---|-------|-------|--------|
| 1 | [Add infinite scroll to HeroCenterCarousel](./phase-01-infinite-scroll.md) | HeroCenterCarousel.kt, GalleryScreen.kt | Pending |

Single phase — changes are tightly coupled, no parallel opportunity.

## Key Decisions

1. **Only List\<T\> overload** — LazyPagingItems is already infinite by nature of paging. Looping doesn't apply.
2. **No AutoAdvanceEffect changes needed** — virtual page count makes wrap-around logic unreachable. Auto-advance just increments `currentPage + 1`, which lands on the next virtual page (same actual item cycle).
3. **MULTIPLIER = 10000** — 50k+ virtual pages. User can't reach edges in normal usage.
4. **Guard: items.size < 2** — disable infinite scroll for single-item lists (no point looping 1 item).
5. **Default off** — `infiniteScroll = false` by default. Caller opts in.

## Risk Assessment

| Risk | Likelihood | Impact | Mitigation |
|------|-----------|--------|------------|
| M3 Carousel perf with large virtualCount | Low | Medium | CarouselState is lazy — only visible pages render. Same pattern used in production pagers. |
| Edge-of-virtual-space reached | Negligible | Low | 50k pages = 25k swipes to reach edge. Practically impossible. |
| Key conflicts with virtual indices | Low | Low | Content-based, not key-based. No explicit keys used. |

## Success Criteria

- [ ] Carousel loops seamlessly forward: item E → item A (no jump/snap-back visible)
- [ ] Carousel loops seamlessly backward: item A → item E
- [ ] Auto-advance continues indefinitely without hiccups
- [ ] `infiniteScroll = false` behaves exactly as before (no regression)
- [ ] `items.size == 1` with `infiniteScroll = true` doesn't loop
- [ ] `./gradlew :app:compileDebugKotlin` passes
