# Phase 04 — Pager Indicators

**Priority:** Medium | **Status:** ✅ Completed

## Context

Animeko doesn't have indicators (relies on peek-based visual). But for full-width banners in Apero apps, indicators are essential for UX.

## Goal

Reusable pager indicator components: dot, line, worm (animated).

## Files

- Create: `app/src/main/kotlin/com/apero/uikit/ui/components/carousel/DotIndicator.kt`
- Create: `app/src/main/kotlin/com/apero/uikit/ui/components/carousel/LineIndicator.kt`

## Implementation Steps

### 1. DotIndicator

```kotlin
@Composable
fun DotIndicator(
    pagerState: PagerState,
    pageCount: Int,
    modifier: Modifier = Modifier,
    activeColor: Color = MaterialTheme.colorScheme.primary,
    inactiveColor: Color = MaterialTheme.colorScheme.outlineVariant,
    dotSize: Dp = 8.dp,
    spacing: Dp = 6.dp,
    animateScale: Boolean = true,  // active dot slightly larger
)
```

Features:
- Animate dot scale on page change (active = 1.2x)
- Smooth color transition between dots during scroll
- Use `pagerState.currentPageOffsetFraction` for scroll-linked animation

### 2. LineIndicator

```kotlin
@Composable
fun LineIndicator(
    pagerState: PagerState,
    pageCount: Int,
    modifier: Modifier = Modifier,
    activeColor: Color = MaterialTheme.colorScheme.primary,
    inactiveColor: Color = MaterialTheme.colorScheme.outlineVariant,
    lineWidth: Dp = 24.dp,
    lineHeight: Dp = 3.dp,
    spacing: Dp = 4.dp,
)
```

Features:
- Active line expands width (animateFloatAsState)
- Scroll-linked position tracking

### 3. Integration with HeroCenterCarousel

Add optional `indicator` slot:
```kotlin
HeroCenterCarousel(
    items = items,
    indicator = { pagerState, count ->
        DotIndicator(pagerState, count)
    },
)
```

## Todo

- [x] Create `DotIndicator.kt` with scroll-linked animation
- [x] Create `LineIndicator.kt` with expanding active line
- [x] Add `indicator` slot to `HeroCenterCarousel`
- [x] Verify animation smoothness

## Success Criteria

- Indicators sync smoothly with pager scroll
- No frame drops during fast swipe
- Compile passes
