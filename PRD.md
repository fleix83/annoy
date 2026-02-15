# ScreenBrake - Product Requirements Document

**Version:** 1.0
**Date:** 2026-02-15
**Status:** Draft
**Author:** Product Team

---

## Table of Contents

1. [Overview](#1-overview)
2. [Problem Statement](#2-problem-statement)
3. [User Personas](#3-user-personas)
4. [Feature Requirements](#4-feature-requirements)
5. [Technical Architecture](#5-technical-architecture)
6. [Data Model](#6-data-model)
7. [Permissions Required](#7-permissions-required)
8. [Release Plan](#8-release-plan)
9. [Success Metrics](#9-success-metrics)

---

## 1. Overview

### 1.1 Product Name

**ScreenBrake** (working title)

### 1.2 Elevator Pitch

ScreenBrake is a minimalist Android app that makes using your phone mildly unpleasant. It introduces configurable friction — annoying sounds, vibrations, and grayscale display — to deter mindless phone usage. It does not block anything. It simply makes the experience worse, so you choose to put the phone down on your own.

### 1.3 Core Philosophy

- **Friction, not force.** The app never prevents the user from doing anything.
- **Boring by design.** No gamification, no streaks, no social features. The app should be forgettable — its job is to make the *phone* forgettable.
- **Privacy-first.** No internet access, no analytics, no data collection, no accounts. Everything stays on-device.
- **Simple and honest.** One screen. All controls visible. Nothing hidden behind menus or onboarding flows.

### 1.4 Non-Goals

| Explicitly Out of Scope | Rationale |
|--------------------------|-----------|
| App usage tracking / screen time stats | Tracking can become its own addiction loop. Other apps (Digital Wellbeing) already do this. |
| Social features (leaderboards, sharing) | Contradicts the "boring and utilitarian" principle. |
| Content blocking or app blocking | ScreenBrake is a deterrent, not a parental control tool. |
| iOS version | Android-only for MVP. iOS lacks the required system-level APIs without jailbreaking. |
| Gamification (streaks, achievements, points) | Engagement mechanics are the opposite of the app's purpose. |
| Onboarding flows or tutorials | The UI should be self-explanatory. A single screen with labeled toggles needs no tutorial. |

---

## 2. Problem Statement

### 2.1 The Problem

The average smartphone user checks their phone 96+ times per day, often unconsciously. Existing solutions fall into two camps:

1. **Blockers** (app timers, lock-out tools) — these create an adversarial relationship. Users circumvent them, uninstall them, or feel resentful. They treat the symptom (app usage) rather than the habit (reaching for the phone).
2. **Trackers** (screen time dashboards, weekly reports) — these rely on self-awareness and willpower. The data is interesting for a week, then ignored. Worse, checking your screen time stats is itself more screen time.

Neither approach addresses the root behavior: the phone is *pleasant to use*, so the user picks it up reflexively.

### 2.2 The Insight

Behavioral psychology shows that adding small, consistent negative stimuli to a behavior is one of the most effective ways to reduce it — more effective than willpower, more sustainable than blocking. The phone doesn't need to be locked. It just needs to be slightly annoying.

### 2.3 The Solution

ScreenBrake adds a configurable annoyance layer to the phone experience:
- A subtle but irritating sound or vibration that plays at intervals while the screen is on
- A grayscale mode that strips the visual dopamine from the display
- Nothing else. No dashboards, no reports, no social pressure. Just friction.

---

## 3. User Personas

### 3.1 Primary Persona — "The Self-Aware Scroller"

| Attribute | Detail |
|-----------|--------|
| **Name** | Alex, 28 |
| **Occupation** | Software developer |
| **Phone usage** | 4–6 hours/day, mostly Reddit, Twitter, YouTube |
| **Awareness** | Knows they use their phone too much. Has tried Screen Time, app timers, and deleting apps. Always relapses. |
| **Frustration** | "I don't want an app that treats me like a child. I want something that makes me *want* to put the phone down." |
| **Tech comfort** | High. Comfortable granting permissions, understanding foreground services, etc. |
| **What they want** | A dead-simple tool that adds friction. No setup beyond flipping a switch. No notifications guilting them. No weekly reports. |

### 3.2 Secondary Persona — "The Bedtime Doomscroller"

| Attribute | Detail |
|-----------|--------|
| **Name** | Jordan, 34 |
| **Occupation** | Teacher |
| **Phone usage** | 2–3 hours/day, but heavily concentrated at night (10pm–1am) |
| **Awareness** | Specifically wants to reduce nighttime phone use. Daytime use is fine. |
| **Frustration** | "I set my phone to Do Not Disturb but I still pick it up and scroll for hours." |
| **Tech comfort** | Moderate. Can follow simple instructions but dislikes complex setup. |
| **What they want** | Scheduling. The deterrent should only be active during their problem hours. During the day, the phone should work normally. |

### 3.3 Tertiary Persona — "The Reluctant Pragmatist"

| Attribute | Detail |
|-----------|--------|
| **Name** | Sam, 41 |
| **Occupation** | Sales manager |
| **Phone usage** | 3–4 hours/day, mixed personal and work |
| **Awareness** | Needs the phone for work (calls, maps, email) but wants friction for everything else. |
| **Frustration** | "Every screen time app either blocks everything or nothing. I need something in between." |
| **Tech comfort** | Low-to-moderate. Needs things to just work. |
| **What they want** | Whitelist/pause functionality. The deterrent should get out of the way when they're on a call or navigating somewhere, then come back automatically. |

---

## 4. Feature Requirements

### 4.1 F1 — Master Toggle

**Priority:** P0 (MVP)

**Description:**
A single, prominent toggle that enables or disables all ScreenBrake functionality globally. When off, the app does nothing — no service running, no sounds, no vibrations, no grayscale.

**Acceptance Criteria:**

| # | Criterion |
|---|-----------|
| AC-1.1 | A clearly labeled ON/OFF toggle is displayed at the top of the main screen. |
| AC-1.2 | When toggled ON, the foreground service starts and a persistent notification appears. |
| AC-1.3 | When toggled OFF, the foreground service stops, the persistent notification is removed, and all deterrent behavior ceases immediately. |
| AC-1.4 | The toggle state persists across app restarts and device reboots. |
| AC-1.5 | If the app lacks required permissions when toggled ON, the user is prompted to grant them before the service starts. |

---

### 4.2 F2 — Screen-On Sound/Vibration Deterrent

**Priority:** P0 (MVP)

**Description:**
When the phone screen turns on and ScreenBrake is active, the app begins playing a deterrent (sound, vibration, or both) at configurable intervals. The deterrent is designed to be mildly irritating — enough to make idle scrolling unpleasant, but not so aggressive that it disrupts legitimate phone use.

**Deterrent Modes:**

| Mode | Behavior |
|------|----------|
| Sound only | Plays the selected sound at each interval. No vibration. |
| Vibration only | Triggers the selected vibration pattern at each interval. No sound. |
| Both | Plays sound and vibration simultaneously at each interval. |

**Built-in Sound Options:**

| Sound | Description | Duration |
|-------|-------------|----------|
| Mosquito buzz | High-frequency whine, soft volume | ~0.5s |
| Low hum | Deep, resonant tone | ~0.8s |
| Faint beep | Short, clinical beep (think: hospital monitor) | ~0.3s |
| Ticking | Rhythmic tick-tock pattern | ~1.0s |

**Vibration Patterns:**

| Pattern | Description |
|---------|-------------|
| Single pulse | One short buzz (~100ms) |
| Double tap | Two quick buzzes (~80ms each, 50ms gap) |
| Long buzz | One sustained vibration (~300ms) |

**Configurable Parameters:**

| Parameter | Options | Default |
|-----------|---------|---------|
| Deterrent mode | Sound / Vibration / Both | Both |
| Sound selection | Mosquito / Hum / Beep / Ticking | Mosquito buzz |
| Vibration pattern | Single pulse / Double tap / Long buzz | Double tap |
| Interval | 10s / 15s / 30s / 60s | 15 seconds |
| Volume | Slider (range: 10%–50% of system media volume) | 25% |
| Grace period | 0s / 3s / 5s / 10s | 5 seconds |

**Acceptance Criteria:**

| # | Criterion |
|---|-----------|
| AC-2.1 | When the screen turns on (ACTION_SCREEN_ON received) and ScreenBrake is active, a countdown begins equal to the configured grace period. |
| AC-2.2 | After the grace period expires, the configured deterrent plays immediately. |
| AC-2.3 | The deterrent repeats at the configured interval for as long as the screen remains on. |
| AC-2.4 | When the screen turns off (ACTION_SCREEN_OFF received), all timers are cancelled and any in-progress deterrent stops immediately. |
| AC-2.5 | If the screen turns off and back on within the grace period, the grace period timer resets. |
| AC-2.6 | The sound plays through the media audio stream at the configured volume percentage, independent of the system notification volume. |
| AC-2.7 | The user can preview each sound from the settings UI without enabling the full deterrent. |
| AC-2.8 | Volume slider is restricted to 10%–50% range to prevent accidental ear damage or social embarrassment. |
| AC-2.9 | The deterrent respects the system Do Not Disturb mode: vibration still triggers, but sound is suppressed. |
| AC-2.10 | If the whitelisted app (see F4) is in the foreground, the deterrent is suppressed. |

---

### 4.3 F3 — Grayscale Toggle

**Priority:** P0 (MVP)

**Description:**
A toggle that enables or disables system-wide grayscale display mode. Grayscale reduces the visual appeal of the phone by removing color, which studies have shown reduces engagement with apps designed around colorful, dopamine-triggering UIs.

**Acceptance Criteria:**

| # | Criterion |
|---|-----------|
| AC-3.1 | A clearly labeled Grayscale ON/OFF toggle is displayed on the main screen. |
| AC-3.2 | When toggled ON, the system display switches to grayscale using `Settings.Secure.ACCESSIBILITY_DISPLAY_DALTONIZER` (value `0` = grayscale) and `ACCESSIBILITY_DISPLAY_DALTONIZER_ENABLED` (value `1`). |
| AC-3.3 | When toggled OFF, the daltonizer is disabled (`ACCESSIBILITY_DISPLAY_DALTONIZER_ENABLED` = `0`). |
| AC-3.4 | The current state (ON/OFF) is clearly indicated visually on the toggle. |
| AC-3.5 | If the app lacks `WRITE_SECURE_SETTINGS` permission, the user is shown a one-time instruction dialog explaining the ADB command needed to grant it: `adb shell pm grant <package> android.permission.WRITE_SECURE_SETTINGS`. |
| AC-3.6 | The grayscale toggle operates independently of the master toggle — it can be used standalone even if the deterrent is off. |
| AC-3.7 | Grayscale state persists across app restarts. |
| AC-3.8 | When the schedule (F5) deactivates ScreenBrake, grayscale is restored to the user's pre-ScreenBrake state (if it was off before activation, it should turn off again). |

---

### 4.4 F4 — Whitelist / Pause

**Priority:** P0 (MVP)

**Description:**
Allows the user to temporarily disable the deterrent or exempt specific apps from triggering it. This ensures legitimate phone use (calls, navigation, camera) isn't punished.

#### 4.4.1 Pause Timer

| Parameter | Options | Default |
|-----------|---------|---------|
| Pause duration | 5 min / 15 min / 30 min / 60 min / Custom | 15 min |

**Acceptance Criteria:**

| # | Criterion |
|---|-----------|
| AC-4.1 | A "Pause" button is displayed on the main screen. |
| AC-4.2 | Tapping Pause opens a duration picker (5 / 15 / 30 / 60 min). |
| AC-4.3 | While paused, all deterrent sounds and vibrations are suppressed. Grayscale state is unaffected. |
| AC-4.4 | A countdown is displayed on the main screen showing remaining pause time. |
| AC-4.5 | When the pause timer expires, the deterrent resumes automatically with no user action required. |
| AC-4.6 | The user can cancel the pause early by tapping "Resume" on the main screen. |
| AC-4.7 | The persistent notification updates to show "Paused — Xm remaining" during a pause. |
| AC-4.8 | Pause state survives app process death (timer is persisted and recalculated on restart). |

#### 4.4.2 App Whitelist

**Priority:** P1 (V1.1)

**Acceptance Criteria:**

| # | Criterion |
|---|-----------|
| AC-4.9 | A "Whitelisted Apps" section is accessible from the main screen. |
| AC-4.10 | The user can select apps from an installed-apps list to whitelist. |
| AC-4.11 | When a whitelisted app is the foreground app, the deterrent is suppressed. |
| AC-4.12 | When the user navigates away from the whitelisted app, the deterrent resumes (respecting the grace period). |
| AC-4.13 | Pre-populated defaults: Phone (dialer), Google Maps, Camera. The user can remove these. |
| AC-4.14 | Detecting the foreground app requires `USAGE_STATS` permission. The app prompts for this only when the user first adds a whitelisted app. |

---

### 4.5 F5 — Scheduling

**Priority:** P0 (MVP)

**Description:**
Allows the user to define active hours during which ScreenBrake's deterrent is operational. Outside active hours, the deterrent is dormant (as if the master toggle were off), but the service remains running so it can reactivate on schedule.

**Configurable Parameters:**

| Parameter | Options | Default |
|-----------|---------|---------|
| Schedule mode | Always on / Scheduled | Always on |
| Active start time | Time picker (HH:MM) | 08:00 |
| Active end time | Time picker (HH:MM) | 22:00 |
| Active days | Day-of-week multi-select | Mon–Sun (all) |

**Acceptance Criteria:**

| # | Criterion |
|---|-----------|
| AC-5.1 | A "Schedule" section is displayed on the main screen with a mode selector (Always on / Scheduled). |
| AC-5.2 | When "Scheduled" is selected, start time, end time, and active days pickers are displayed. |
| AC-5.3 | The deterrent only fires during the configured active window on the selected days. |
| AC-5.4 | Overnight schedules are supported (e.g., start: 22:00, end: 06:00 = active from 10pm to 6am). |
| AC-5.5 | The main screen shows whether the deterrent is currently "Active" or "Scheduled — resumes at HH:MM". |
| AC-5.6 | Schedule transitions happen silently — no notifications or sounds when entering/leaving the active window. |
| AC-5.7 | If grayscale is linked to the schedule (user preference), grayscale toggles on/off with the schedule. If unlinked, grayscale is manual-only. |
| AC-5.8 | Schedule uses `AlarmManager` for reliable activation/deactivation, not in-process timers. |

---

### 4.6 F6 — Persistent Notification

**Priority:** P0 (MVP — required by Android)

**Description:**
Android requires a persistent notification for foreground services. Rather than treating this as a nuisance, ScreenBrake uses it as a minimal status display and quick-action surface.

**Acceptance Criteria:**

| # | Criterion |
|---|-----------|
| AC-6.1 | When the foreground service is running, a persistent (non-dismissible) notification is shown. |
| AC-6.2 | The notification displays the current state: "Active", "Paused — Xm remaining", or "Scheduled — resumes at HH:MM". |
| AC-6.3 | The notification includes a "Pause" quick action button. |
| AC-6.4 | The notification includes a "Stop" quick action button that turns off the master toggle. |
| AC-6.5 | Tapping the notification body opens the app's main screen. |
| AC-6.6 | The notification uses a low-priority channel to minimize visual intrusiveness. |
| AC-6.7 | The notification icon is a simple, monochrome icon that is legible at small sizes. |

---

### 4.7 F7 — Minimal UI (Main Screen)

**Priority:** P0 (MVP)

**Description:**
A single-screen dashboard with all controls visible. No navigation drawer, no tabs, no bottom bar, no settings buried in sub-screens. The app opens and everything is right there.

**Layout (top to bottom):**

```
┌─────────────────────────────────┐
│  ScreenBrake              [ON]  │  ← Master toggle
├─────────────────────────────────┤
│  Status: Active                 │  ← Current state
│  Next deterrent in: 12s         │  ← Live countdown
├─────────────────────────────────┤
│  Deterrent                      │
│  ┌─────────┐ ┌────────────────┐ │
│  │ Sound ▼ │ │ Mosquito buzz  │ │  ← Mode + sound selector
│  └─────────┘ └────────────────┘ │
│  Vibration: Double tap     [▼]  │  ← Vibration pattern
│  Interval:  ●──────○  15s      │  ← Interval selector
│  Volume:    ●───○      25%     │  ← Volume slider
│  Grace:     ●──────○  5s       │  ← Grace period
├─────────────────────────────────┤
│  Grayscale                [OFF] │  ← Independent toggle
├─────────────────────────────────┤
│  Schedule                       │
│  ○ Always on  ● Scheduled       │
│  08:00 ──── 22:00   [Mon–Sun]   │
├─────────────────────────────────┤
│  [    Pause (15 min)    ▼    ]  │  ← Pause button w/ duration
└─────────────────────────────────┘
```

**Acceptance Criteria:**

| # | Criterion |
|---|-----------|
| AC-7.1 | All controls from F1–F6 are accessible from the single main screen without navigation. |
| AC-7.2 | Dark theme is the default and only theme. |
| AC-7.3 | No onboarding flow, splash screen, or tutorial overlays. The app opens directly to the dashboard. |
| AC-7.4 | The UI uses Jetpack Compose with Material 3 components. |
| AC-7.5 | All interactive elements are accessible (content descriptions, minimum touch targets of 48dp). |
| AC-7.6 | The UI renders correctly on screen sizes from 5" to 7" at standard densities. |
| AC-7.7 | No animations beyond standard Material transitions. The UI should feel static and utilitarian. |
| AC-7.8 | The app name, version, and a link to the permission-setup instructions are available via a minimal "About" section at the bottom of the screen. |

---

## 5. Technical Architecture

### 5.1 Architecture Overview

```
┌─────────────────────────────────────────────────────┐
│                    UI Layer                          │
│            (Jetpack Compose + ViewModel)             │
│                                                     │
│  MainActivity ──── MainViewModel ──── MainScreen     │
└──────────────────────┬──────────────────────────────┘
                       │ observes / writes
┌──────────────────────▼──────────────────────────────┐
│                 Data Layer                           │
│          (DataStore Preferences)                     │
│                                                     │
│  SettingsRepository ──── DataStore<Preferences>      │
└──────────────────────┬──────────────────────────────┘
                       │ reads
┌──────────────────────▼──────────────────────────────┐
│               Service Layer                          │
│                                                     │
│  DeterrentService (Foreground Service)               │
│    ├── ScreenStateReceiver (BroadcastReceiver)       │
│    ├── DeterrentPlayer (SoundPool + Vibrator)        │
│    ├── ScheduleManager (AlarmManager)                │
│    ├── ForegroundAppMonitor (UsageStatsManager)      │
│    └── GrayscaleController (Settings.Secure)         │
│                                                     │
│  BootReceiver (restarts service on reboot)           │
└─────────────────────────────────────────────────────┘
```

### 5.2 Key Components

#### 5.2.1 DeterrentService (Foreground Service)

- **Type:** `Service` with `startForeground()`
- **Lifecycle:** Started when master toggle is ON, stopped when OFF
- **Responsibilities:**
  - Registers/unregisters `ScreenStateReceiver`
  - Manages deterrent interval timer (`Handler` / `postDelayed`)
  - Hosts `DeterrentPlayer` for sound/vibration playback
  - Reads configuration from `SettingsRepository`
  - Updates persistent notification state
- **Wake behavior:** Service is restarted via `START_STICKY` return and `BootReceiver`

#### 5.2.2 ScreenStateReceiver (BroadcastReceiver)

- **Registered dynamically** within `DeterrentService` (not in manifest)
- **Listens for:**
  - `Intent.ACTION_SCREEN_ON` — starts grace period countdown
  - `Intent.ACTION_SCREEN_OFF` — cancels all timers, stops any playing deterrent
  - `Intent.ACTION_USER_PRESENT` (optional, for keyguard-aware behavior)
- **Note:** `ACTION_SCREEN_ON` / `ACTION_SCREEN_OFF` cannot be registered in the manifest since API 26; dynamic registration within a running service is required.

#### 5.2.3 DeterrentPlayer

- **Sound playback:** `SoundPool` (low-latency, pre-loaded short clips)
  - Sound files stored as raw resources (`res/raw/`)
  - Volume calculated as: `userVolumePercent * systemMediaVolume`
- **Vibration:** `Vibrator` / `VibratorManager` (API 31+)
  - Patterns defined as `VibrationEffect` objects
- **Respects DND:** Checks `NotificationManager.getCurrentInterruptionFilter()` before playing sound

#### 5.2.4 ScheduleManager

- Uses `AlarmManager.setExactAndAllowWhileIdle()` for schedule transitions
- Sets two alarms: one for schedule-start, one for schedule-end
- Recalculates alarms daily via a `DailyAlarmReceiver`
- Handles overnight schedules (end time < start time)

#### 5.2.5 GrayscaleController

- Writes to `Settings.Secure`:
  - `ACCESSIBILITY_DISPLAY_DALTONIZER_ENABLED` → `1` or `0`
  - `ACCESSIBILITY_DISPLAY_DALTONIZER` → `0` (grayscale) or `-1` (disabled)
- Requires `WRITE_SECURE_SETTINGS` (granted via ADB, not at runtime)
- Provides a `isAvailable()` check — if permission not granted, the toggle shows a setup prompt instead

#### 5.2.6 ForegroundAppMonitor (V1.1)

- Uses `UsageStatsManager.queryUsageStats()` to determine the current foreground app
- Polled every ~2 seconds when the screen is on (not continuously)
- Compares foreground package name against whitelist
- Only initialized if the user has added whitelisted apps and granted `USAGE_STATS` permission

### 5.3 Threading Model

| Operation | Thread | Mechanism |
|-----------|--------|-----------|
| UI rendering | Main | Compose recomposition |
| Settings read/write | IO | DataStore coroutines |
| Sound playback | Dedicated | SoundPool (internal thread) |
| Vibration trigger | Main | System call (non-blocking) |
| Interval timer | Main | Handler.postDelayed |
| Foreground app polling | IO | Coroutine with delay |
| Schedule alarms | System | AlarmManager → BroadcastReceiver |

### 5.4 Battery Considerations

- The foreground service itself consumes negligible battery when the screen is off (no timers running, receiver is passive)
- `SoundPool` is more battery-efficient than `MediaPlayer` for short clips
- Foreground app polling (V1.1) is the most battery-intensive operation; it only runs while the screen is on and at a 2-second interval
- `AlarmManager` exact alarms are used sparingly (2 per day for schedule)

---

## 6. Data Model

All data is stored locally using Jetpack DataStore (Preferences). No database. No files. No network.

### 6.1 Preferences Schema

```
screenbrake_preferences {

    // Master state
    master_enabled: Boolean          = false

    // Deterrent configuration
    deterrent_mode: String           = "both"          // "sound" | "vibration" | "both"
    sound_selection: String          = "mosquito"       // "mosquito" | "hum" | "beep" | "ticking"
    vibration_pattern: String        = "double_tap"     // "single_pulse" | "double_tap" | "long_buzz"
    interval_seconds: Int            = 15               // 10 | 15 | 30 | 60
    volume_percent: Int              = 25               // 10..50
    grace_period_seconds: Int        = 5                // 0 | 3 | 5 | 10

    // Grayscale
    grayscale_enabled: Boolean       = false
    grayscale_linked_to_schedule: Boolean = false

    // Schedule
    schedule_mode: String            = "always"         // "always" | "scheduled"
    schedule_start_hour: Int         = 8
    schedule_start_minute: Int       = 0
    schedule_end_hour: Int           = 22
    schedule_end_minute: Int         = 0
    schedule_active_days: Set<String> = {"MON","TUE","WED","THU","FRI","SAT","SUN"}

    // Pause
    pause_end_timestamp: Long        = 0L               // epoch millis; 0 = not paused
    pause_default_minutes: Int       = 15               // 5 | 15 | 30 | 60

    // Whitelist (V1.1)
    whitelisted_packages: Set<String> = {
        "com.google.android.dialer",
        "com.google.android.apps.maps",
        "com.android.camera"
    }
}
```

### 6.2 Data Flow

```
User adjusts setting
    → ViewModel writes to DataStore
    → DataStore emits Flow<Preferences>
    → DeterrentService collects Flow, updates behavior in real-time
    → No restart required for any setting change
```

### 6.3 Migration Strategy

- DataStore Preferences does not require schema migrations
- If keys are added in future versions, they simply use their default values
- No versioning needed for MVP

---

## 7. Permissions Required

### 7.1 Manifest Permissions

| Permission | Purpose | Required For | Granted By |
|------------|---------|--------------|------------|
| `FOREGROUND_SERVICE` | Run the deterrent service in the foreground | Core functionality | Auto-granted at install |
| `FOREGROUND_SERVICE_SPECIAL_USE` | Foreground service type declaration (API 34+) | Core functionality | Auto-granted at install |
| `RECEIVE_BOOT_COMPLETED` | Restart service after device reboot | Persistence | Auto-granted at install |
| `VIBRATE` | Trigger vibration patterns | Vibration deterrent | Auto-granted at install |
| `POST_NOTIFICATIONS` | Show foreground service notification (API 33+) | Core functionality | Runtime prompt |
| `SCHEDULE_EXACT_ALARM` | Schedule activation/deactivation times | Scheduling | Runtime prompt (API 31+) |
| `WRITE_SECURE_SETTINGS` | Toggle grayscale display mode | Grayscale feature | ADB command (manual) |
| `PACKAGE_USAGE_STATS` | Detect foreground app for whitelist (V1.1) | App whitelist | System settings prompt |

### 7.2 Permission Acquisition Flow

```
App first launch
    │
    ├── POST_NOTIFICATIONS (API 33+)
    │   └── Runtime dialog: "ScreenBrake needs to show a notification while running."
    │
    ├── SCHEDULE_EXACT_ALARM (if Scheduled mode selected)
    │   └── Runtime dialog via system settings intent
    │
    ├── WRITE_SECURE_SETTINGS (if Grayscale toggled)
    │   └── One-time instruction dialog:
    │       "Grayscale requires a one-time setup via computer.
    │        Run: adb shell pm grant <pkg> android.permission.WRITE_SECURE_SETTINGS"
    │       [Copy Command] [Dismiss]
    │
    └── PACKAGE_USAGE_STATS (V1.1, if whitelist used)
        └── System settings intent for usage access
```

### 7.3 Graceful Degradation

| Missing Permission | App Behavior |
|--------------------|-------------|
| `POST_NOTIFICATIONS` | Service cannot run. User must grant to use app. |
| `WRITE_SECURE_SETTINGS` | Grayscale toggle is disabled with setup instructions shown. All other features work. |
| `SCHEDULE_EXACT_ALARM` | Falls back to inexact alarms (`setAndAllowWhileIdle`). Schedule may drift by a few minutes. |
| `PACKAGE_USAGE_STATS` | Whitelist feature is disabled. Deterrent plays in all apps. |

---

## 8. Release Plan

### 8.1 MVP (V1.0)

**Target:** Initial public release on Google Play

| Feature | Scope |
|---------|-------|
| Master toggle | Full |
| Sound/vibration deterrent | Full (4 sounds, 3 vibration patterns, all config options) |
| Grayscale toggle | Full (with ADB setup flow) |
| Pause timer | Full (preset durations) |
| Scheduling | Full (time range + day selection) |
| Persistent notification | Full (status + quick actions) |
| Single-screen UI | Full |
| Boot persistence | Full |

**Excluded from MVP:**
- App whitelist (requires `USAGE_STATS`, adds complexity)
- Custom sounds
- Widget

**Quality Gates for MVP:**
- All acceptance criteria met
- Tested on Android 10, 12, 13, 14 (physical or emulator)
- Battery drain < 2% over 8 hours with screen off
- No ANR (Application Not Responding) events
- Foreground service restarts reliably after process death
- All sounds play correctly on both speaker and wired headphones
- Grayscale toggles correctly and restores on disable

### 8.2 V1.1

**Target:** 4–6 weeks after MVP

| Feature | Scope |
|---------|-------|
| App whitelist | Full (installed app picker, foreground detection, default apps) |
| Custom pause duration | User-entered minutes |
| Notification quick action: toggle grayscale | Add to persistent notification |

### 8.3 V1.2

**Target:** 8–12 weeks after MVP

| Feature | Scope |
|---------|-------|
| Custom sounds | Import sound from device storage |
| Home screen widget | 1x1 toggle widget (on/off + pause) |
| Tasker/automation integration | Broadcast intents for external automation |
| Per-day schedule profiles | Different schedules for weekdays vs. weekends |

### 8.4 Explicitly Not Planned

These features are intentionally excluded from the roadmap. Revisit only with strong evidence of user need:

- Usage statistics / screen time tracking
- Social features / sharing
- Achievements / gamification
- Cloud sync / backup
- Multi-device support
- iOS version
- Monetization (ads, premium)

---

## 9. Success Metrics

### 9.1 Guiding Principle

ScreenBrake's success is paradoxical: the app succeeds when users use their phones *less*. Traditional engagement metrics (DAU, session length, retention) are inversely correlated with product value. The metrics below are designed around this reality.

### 9.2 MVP Success Criteria

Since the app collects no analytics, these metrics are measured through **external channels only** (Play Store data, user reviews, crash reports).

| Metric | Target | Measurement |
|--------|--------|-------------|
| **Crash-free rate** | > 99.5% | Play Console / Android Vitals |
| **ANR rate** | < 0.5% | Play Console / Android Vitals |
| **Play Store rating** | >= 4.0 stars | Play Console |
| **Uninstall rate (7-day)** | < 50% | Play Console |
| **Uninstall rate (30-day)** | < 70% | Play Console |
| **Excessive battery drain reports** | < 1% of installs | Play Console vitals |
| **Permission setup completion** | Qualitative (via review sentiment) | Play Store reviews |

### 9.3 Qualitative Success Indicators

Since the app has no analytics, success is validated through review sentiment analysis:

| Positive Signal | Negative Signal |
|-----------------|-----------------|
| "I actually use my phone less" | "Annoying" (without context of it being intentional) |
| "Simple and does what it says" | "Too many permissions" |
| "Finally something that works" | "Drains my battery" |
| "I keep it on all the time" | "Stops working after a while" |
| "Boring in the best way" | "Needs more features" (this may actually be success) |

### 9.4 Anti-Metrics (Things We Explicitly Do NOT Optimize For)

| Anti-Metric | Why |
|-------------|-----|
| Daily Active Users | High DAU means people are opening the app a lot, which means the UI isn't simple enough. |
| Session length | Users should spend < 10 seconds in the app. |
| Feature adoption rate | Low adoption of Grayscale or Scheduling is fine — it means the core deterrent is sufficient. |
| Notification engagement | Users should mostly ignore the notification. |

---

## Appendix A: Sound Design Guidelines

- All built-in sounds should be:
  - **Short** (< 1 second)
  - **Non-musical** (no melodies, no pleasant tones)
  - **Low-information** (should not sound like a notification or alarm)
  - **Mildly irritating at low volume** (the "mosquito in a dark room" principle)
- Sounds should be distinct from each other so the user can choose based on what annoys them personally
- File format: OGG Vorbis (Android-native, small file size, good quality)
- Sample rate: 44.1kHz, mono

## Appendix B: Accessibility Considerations

- All text meets WCAG AA contrast ratios against dark background
- Touch targets minimum 48dp x 48dp
- All controls have `contentDescription` for screen readers
- Sound-only mode is not the default (vibration is included) for hearing-impaired users
- Grayscale feature includes a note that it may affect accessibility overlays

## Appendix C: Privacy Statement (for Play Store listing)

> ScreenBrake does not collect, store, transmit, or share any personal data. The app requires no internet permission. All settings are stored locally on your device. No analytics, no tracking, no accounts. Your phone usage is your business.
