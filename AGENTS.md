# AGENTS.md

This file provides guidance to AI coding agents (Claude Code, Codex, etc.) when working with code in this repository.
CLAUDE.md imports this file — edit here, not in CLAUDE.md.

## Project Overview

SimpleMeditationLogger is a single-module Android app (Kotlin, Jetpack Compose, Material 3) that logs meditation sessions with a countdown timer and optionally syncs them to Health Connect as mindfulness sessions. Min SDK 34, target/compile SDK 36, JVM target 11. Project docs (VERSIONING.md, RELEASE_GUIDE.md, etc.) are written in Japanese.

## Commands

```bash
./gradlew assembleDebug                 # Build debug APK
./gradlew test                          # Run all unit tests
./gradlew testDebugUnitTest --tests "com.github.kmachida12345.simplemeditationlogger.domain.usecase.EndMeditationSessionUseCaseTest"   # Single test class
./gradlew connectedAndroidTest          # Instrumented tests (requires device/emulator)
./gradlew bundleRelease                 # Release AAB for Google Play
./gradlew assembleRelease               # Release APK (minified, ProGuard)
```

## Versioning / Releases

- `versionCode`/`versionName` are defined as `app.versionCode`/`app.versionName` in `gradle.properties` and read by `app/build.gradle.kts` `defaultConfig`. Bump versions with `./bump-version.sh [patch|minor|major]` (interactive unless `-y` is passed; edits `gradle.properties`) — see VERSIONING.md.
- Release automation: when a push to master increases `app.versionCode`, `.github/workflows/tag-on-version-bump.yml` tags `v<versionName>` and calls `release.yml`, which uploads a signed AAB to the Play Console internal track via Gradle Play Publisher (`publishReleaseBundle`) and then opens the next bump PR (`chore/bump-version`, versionCode+1 / patch+1). Pushing a `v*` tag manually also runs `release.yml`; the tag must match `app.versionName`.
- Release flow (signing setup, Google Play checklist) is documented in RELEASE_GUIDE.md.

## Architecture

Three layers under `app/src/main/java/com/github/kmachida12345/simplemeditationlogger/`, wired with Hilt (`di/` has AppModule, DatabaseModule, RepositoryModule; app class is `MeditationApp`):

- **data/** — Room database (`data/database/MeditationDatabase`, entity `MeditationSession`, DAO, `Converters` for `Instant`; schemas exported to `app/schemas/`), Preferences DataStore (`data/datastore/AppSettingsDataStore` for default duration + Health Connect toggle), and `data/healthconnect/HealthConnectManager` (wraps HealthConnectClient; owns the permission set and writes `MindfulnessSessionRecord`s). Repository interfaces live next to their `*Impl` in `data/repository/`.
- **domain/usecase/** — one class per operation with `@Inject constructor` and `suspend operator fun invoke(...)` returning `Result<T>`. Key flow: `EndMeditationSessionUseCase` saves the session to Room, then calls `SyncToHealthConnectUseCase` only if the Health Connect setting is enabled.
- **ui/** — one package per screen (home, countdown, complete, history, drawer), each with a Screen composable + `@HiltViewModel`. Navigation is type-safe Navigation Compose: routes are `@Serializable` types in `ui/navigation/Screen.kt` (kotlinx.serialization), hosted by `MeditationNavHost` which also owns the modal drawer.

## Testing

Unit tests use JUnit4 + MockK (`relaxed = true` mocks) + `kotlinx-coroutines-test` (`runTest`) + Turbine for Flows, with backtick `` `given does expected` `` names and Given/When/Then comments. Shared test utilities live in `app/src/test/.../testutil/`: `MainDispatcherRule` (swaps `Dispatchers.Main` for a `StandardTestDispatcher`) and `MutableClock` (advanceable `java.time.Clock`; `CountdownViewModel` takes an injected `Clock` for this). Caution when testing `CountdownViewModel`: its timer loop pairs virtual `delay(1s)` with clock reads, so never `advanceUntilIdle()` while the fake clock is still before the timer's end time (infinite loop) — advance the virtual scheduler and `MutableClock` in lockstep, or drive `onEnd()`-based paths.

## Conventions

- Dependencies are managed via the version catalog `gradle/libs.versions.toml`; add new libraries there, not as raw coordinates.
- Room schema location is configured via KSP arg; commit generated JSON under `app/schemas/`.
- Kotlin 2.x with the Compose compiler plugin and KSP (not kapt) for Room/Hilt.
