# Phase 01 — Upgrade AutoAdvanceEffect

**Priority:** High | **Status:** ✅ Completed

## Context

- Current: [AutoAdvanceEffect.kt](/Users/haikhong/RepoHub/ui-kit/app/src/main/kotlin/com/apero/uikit/ui/components/AutoAdvanceEffect.kt) — basic lifecycle-aware, pause on scroll
- Reference: [animeko CarouselAutoAdvanceEffect.kt](/Users/haikhong/RepoHub/animeko/app/shared/ui-foundation/src/commonMain/kotlin/ui/foundation/layout/CarouselAutoAdvanceEffect.kt)

## Gap vs Animeko

| Feature | Current | Target |
|---------|---------|--------|
| Lifecycle-aware | ✅ | ✅ |
| Pause on user scroll | ✅ | ✅ |
| Hover-to-pause (desktop/foldable) | ❌ | ✅ |
| Configurable animation spec | ❌ (default) | ✅ tween 1000ms EmphasizedEasing |
| Enable/disable toggle | ❌ | ✅ `enabled` param |
| Wrap-around detection (75% visibility) | ❌ (modulo only) | ✅ smart last-item check |
| Configurable period | ✅ hardcoded 3s | ✅ param |

## Files to Modify

- `app/src/main/kotlin/com/apero/uikit/ui/components/AutoAdvanceEffect.kt` — upgrade
- Create: `app/src/main/kotlin/com/apero/uikit/ui/components/carousel/CarouselMotionScheme.kt` — animation specs

## Implementation Steps

### 1. Create CarouselMotionScheme.kt

```kotlin
// Default animation specs for carousel transitions
object CarouselMotionScheme {
    val autoAdvanceSpec: FiniteAnimationSpec<Float> = tween(
        durationMillis = 1000,
        easing = EmphasizedDecelerateEasing  // M3 emphasized easing
    )
}
```

### 2. Upgrade AutoAdvanceEffect

Add parameters:
- `enabled: Boolean = true` — external toggle (for hover, visibility, etc.)
- `animationSpec` — from CarouselMotionScheme
- Keep `period: Duration = 3.seconds`

Enhance logic:
- `snapshotFlow { enabled && !pagerState.isScrollInProgress }` — combined skip condition
- Wrap-around: detect last page 75% visible → `animateScrollToPage(0)` instead of modulo increment
- Use `animateScrollToPage(target)` with custom animationSpec

### 3. Keep backward compatible

Current `BannerCarousel.kt` calls `AutoAdvanceEffect(pagerState, pageCount)` → new params have defaults, no breaking change.

## Todo

- [x] Create `carousel/` package under components
- [x] Create `CarouselMotionScheme.kt`
- [x] Upgrade `AutoAdvanceEffect` with enabled + animationSpec params
- [x] Add smart wrap-around detection (layoutInfo-based)
- [x] Verify BannerCarousel still compiles with no changes

## Success Criteria

- `./gradlew :app:compileDebugKotlin` passes
- Auto-scroll pauses when `enabled = false`
- Wraps smoothly from last → first with 1000ms tween
