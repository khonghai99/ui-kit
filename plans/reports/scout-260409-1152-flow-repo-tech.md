# Scout Report: Flow Repo Tech Stack

**Repo:** `/Users/haikhong/RepoHub/Flow`
**Date:** 2026-04-09
**Branch:** main

---

## Overview

**Flow** — privacy-respecting YouTube + YouTube Music client for Android with on-device AI recommendation engine (FlowNeuroEngine). Open-source, GPL v3, by A-EDev.

## Code Metrics

| Metric | Value |
|--------|-------|
| Kotlin files | 402 |
| Lines of code | ~103,445 |
| ViewModels | 42 |
| Repositories | 11+ |
| Room entities | 11 (schema v13) |
| DAOs | 7 |
| Services | 4 |
| Unit tests | 11 files |
| Localizations | 6 languages |

---

## Tech Stack

### Build System
- **Gradle KTS** with version catalog (`libs.versions.toml`)
- **AGP** 8.7.2, **Kotlin** 1.9.22, **KSP** 1.9.22-1.0.17
- **Compose Compiler** 1.5.10
- **Java** 17 (source + target)
- **compileSdk** 34, **minSdk** 21, **targetSdk** 34
- **Flavors:** `github` (updater enabled), `foss` (F-Droid compatible)
- **Build types:** debug, nightly (release perf + debug signing), release

### Architecture
- **Clean Architecture + MVVM** (Presentation → ViewModel → Repository → Data)
- **Single Activity** (`MainActivity`) + Navigation Compose
- **Reactive state:** `StateFlow<T>` everywhere, `collectAsStateWithLifecycle()`
- **Hilt DI** with 6 modules (App, Database, Network, Repository, Download, CacheQualifiers)

### UI
- **Jetpack Compose** 100% (BOM 2024.09.00) — zero XML layouts
- **Material 3** with dynamic theming (11 theme variants)
- **Navigation Compose** 2.8.0 with deep linking (YouTube URLs)
- **ConstraintLayout Compose** 1.1.0

### Networking
- **NewPipe Extractor** v0.26.0 — YouTube data extraction (no OAuth)
- **InnerTube API** — custom client (67 files) for metadata/search
- **OkHttp** 4.12.0 — HTTP foundation
- **Ktor** 2.3.12 — async HTTP with content negotiation + Brotli
- **Conscrypt** 2.5.2 — modern TLS on older devices
- **kotlinx-serialization** 1.6.3 + **Gson** 2.11.0

### Media
- **Media3/ExoPlayer** 1.4.1 — video + audio playback (HLS, DASH)
- **Media3 Session** — foreground service, notification controls
- **SponsorBlock** integration — skip sponsors/intros/outros
- **DeArrow** — community-sourced titles/thumbnails
- **PiP** (Picture-in-Picture) support
- **DLNA/UPnP** casting + Google Cast
- **Audio visualizations** + equalizer

### Persistence
- **Room** 2.6.1 — 11 entities, 7 DAOs, v13 with migrations
- **DataStore Preferences** 1.1.1 — user settings, player prefs
- **Coil** 2.7.0 — image loading (memory 25% RAM + disk cache)

### Async
- **Kotlin Coroutines** 1.8.1 — `viewModelScope`, `Dispatchers.IO`
- **Paging 3** (3.3.2) — paginated lists
- **RxJava 3** 3.1.8 — required by NewPipe Extractor
- **WorkManager** 2.9.1 — background subscription checks

### AI/ML
- **FlowNeuroEngine** v10.0 (~6,859 LOC, 9 files) — on-device recommendation
  - TF-IDF tokenization, cosine similarity scoring
  - Time-bucketed user profiles (8 periods: weekday/weekend × morning/afternoon/evening/night)
  - 100+ tunable scoring constants
  - Anti-binge, anti-repetition, topic diversity logic
  - DataStore persistence with export/import

### Testing
- **JUnit 4** + **MockK** 1.13.12 + **Truth** 1.1.5 + **Turbine** 1.1.0
- **Hilt Testing** 2.51.1
- **Espresso** 3.6.1 (instrumented)
- Tests cover: ViewModels, Repositories, FlowNeuroEngine

### CI/CD
- **GitHub Actions** — 4 parallel APK variants (github.debug, github.release, github.nightly, foss.release)
- **Fastlane** — metadata management
- **ProGuard** minification on release (no resource shrinking for reproducible builds)

### Other
- **Picasso** 2.8 (alongside Coil — likely legacy)
- **Palette KTX** — color extraction from images
- **Brotli** 0.1.2 — HTTP compression
- **re2j** 1.7 — regex engine
- **Multidex** — API <21 support
- **Core Splashscreen** 1.0.1

---

## Package Structure (Top Level)

```
io.github.aedev.flow/
├── FlowApplication.kt          # @HiltAndroidApp entry
├── MainActivity.kt              # Single activity, deep links
├── data/ (131 files)
│   ├── local/                   # Room DB, DAOs, entities, DataStore
│   ├── repository/              # YouTube, DeArrow, SponsorBlock
│   ├── recommendation/          # FlowNeuroEngine (9 files)
│   ├── music/, shorts/          # Domain-specific repos
│   ├── lyrics/ (13 files)       # Lyrics fetching/display
│   ├── innertube/               # YouTube InnerTube client
│   ├── download/                # Download management
│   └── paging/, model/, search/
├── di/ (6 files)                # Hilt modules
├── player/ (76 files)
│   ├── EnhancedPlayerManager    # Video playback singleton
│   ├── EnhancedMusicPlayerManager
│   ├── GlobalPlayerState        # Shared state
│   ├── dlna/, cast/, gesture/   # Casting, controls
│   └── resolver/, cache/, quality/
├── ui/ (125 files)
│   ├── FlowApp.kt              # Compose root
│   ├── FlowNavigation.kt       # NavHost + routes
│   ├── theme/ (4 files)        # M3 theming
│   ├── components/ (32 files)  # Shared UI
│   └── screens/ (82 files, 16 features)
│       ├── home/, player/, music/, settings/
│       ├── search/, channel/, shorts/
│       ├── library/, playlists/, history/
│       └── personality/, onboarding/
├── innertube/ (67 files)        # YouTube API models
├── service/ (4 files)           # Media + download services
├── notification/ (6 files)      # Notifications, workers
├── extensions/ (1 file)
└── utils/ (8 files)             # Crash handler, update manager
```

---

## Architectural Strengths
- Clean layer separation with reactive StateFlow binding
- Modular DI via Hilt with clear module boundaries
- 100% Compose UI — modern, no XML legacy
- Privacy-first: on-device ML, zero server telemetry
- Offline-capable: Room caching, local recommendations
- Multi-format casting (Google Cast + DLNA/UPnP)

## Technical Debt / Observations
- Dual JSON libs (Gson + kotlinx-serialization) — migration incomplete
- Dual image loaders (Coil + Picasso) — Picasso likely legacy
- `legacy/` dir has old algorithm versions + deprecated screens
- FlowApp.kt ~405 lines — complex overlay state management
- 100+ hardcoded scoring constants in NeuroScoring
- RxJava dependency forced by NewPipe Extractor (not used elsewhere)
- Kotlin 1.9.22 (not latest), Compose BOM Sep 2024

## Unresolved Questions
- How are NeuroScoring constants calibrated? (empirical tuning?)
- Performance at scale with >2000 watch history entries?
- Plan to migrate off Picasso/Gson completely?
- InnerTube API stability — how often does YouTube break it?
