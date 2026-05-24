# InputBooster API — Integration Guide

## Overview

The InputBooster API lets your mod read stats, change settings, and react to events
from InputBooster in real time. It supports both **required** and **optional** dependency patterns.

---

## Setup

### 1. Add the dependency

**`build.gradle`**
```groovy
repositories {
    maven { url "https://maven.ahaduzzamankhan.dev/releases" }  // or local file dep
}

dependencies {
    // Required dependency (InputBooster must be installed)
    modImplementation "dev.inputbooster:inputbooster-api:3.0.1"

    // OR — optional dependency (InputBooster doesn't have to be installed)
    modCompileOnly "dev.inputbooster:inputbooster-api:3.0.1"
}
```

### 2. Declare in `fabric.mod.json`

**Required:**
```json
"depends": {
    "inputbooster": ">=3.0.1"
}
```

**Optional:**
```json
"suggests": {
    "inputbooster": ">=3.0.1"
}
```

---

## Quick Examples

### Read the current poll rate
```java
import dev.inputbooster.api.InputBoosterAPI;

// Safe — works for both required and optional dependency patterns
if (InputBoosterAPI.isLoaded()) {
    int hz = InputBoosterAPI.getInstance().getPollRateHz();
    System.out.println("InputBooster is polling at " + hz + " Hz");
}
```

### Change a setting
```java
InputBoosterAPI api = InputBoosterAPI.getInstance();
api.setPollRateHz(500);
api.setAutoMode(false);
api.setMaxCps(15);
api.setBurstModeEnabled(true);
```

### Listen to events
```java
import dev.inputbooster.api.events.InputBoosterEvent;

InputBoosterAPI.getInstance().registerListener(event -> {
    switch (event.type()) {
        case ATTACK_QUEUED      -> onAttackQueued();
        case POLL_RATE_CHANGED  -> System.out.println("New Hz: " + event.intValue());
        case MOD_TOGGLED        -> System.out.println("Active: " + event.boolValue());
        case BURST_MODE_STARTED -> System.out.println("Burst mode on!");
        case PROFILE_LOADED     -> System.out.println("Profile: " + event.stringValue());
    }
});
```

### Optional dependency (safest pattern)
```java
import dev.inputbooster.api.util.InputBoosterCompat;

// In your mod init — runs only if InputBooster is installed
InputBoosterCompat.ifLoaded(api -> {
    api.setMaxCps(16);
    api.registerListener(event -> myMod.onInputBoosterEvent(event));
});

// Quick stat reads with fallback defaults
int hz  = InputBoosterCompat.getPollRateOrDefault(200);
String v = InputBoosterCompat.getVersion();
```

### One-liner stats (no null checks needed)
```java
import dev.inputbooster.api.util.InputBoosterStats;

int  fps      = InputBoosterStats.fps();       // 0 if not loaded
int  hz       = InputBoosterStats.hz();        // 0 if not loaded
long recovered = InputBoosterStats.recovered(); // 0 if not loaded
boolean burst = InputBoosterStats.isBursting(); // false if not loaded
String summary = InputBoosterStats.summary();  // "InputBooster | 200 Hz | AUTO | ..."
```

### Manage profiles
```java
InputBoosterAPI api = InputBoosterAPI.getInstance();

// Save current settings as "PvP"
api.saveProfile("PvP");

// Load it later
api.loadProfile("PvP");

// List all profiles
List<String> names = api.getProfileNames(); // ["PvP", "Mining", "Idle"]
```

### Latency stats
```java
double avg  = api.getAverageLatencyMs(); // e.g. 3.2
double peak = api.getPeakLatencyMs();    // e.g. 18.0
api.resetPeakLatency();
```

---

## API Reference

### `InputBoosterAPI` (interface)

| Method | Returns | Description |
|--------|---------|-------------|
| `isLoaded()` | `boolean` | Static. True if InputBooster is present and initialized |
| `getInstance()` | `InputBoosterAPI` | Static. Get the API instance |
| `getPollRateHz()` | `int` | Current Hz (60–1000) |
| `setPollRateHz(int)` | `void` | Set Hz, clamped to [60, 1000] |
| `isAutoMode()` | `boolean` | True when in FPS-adaptive AUTO mode |
| `setAutoMode(boolean)` | `void` | Switch AUTO/MANUAL |
| `isActive()` | `boolean` | True when mod is processing inputs |
| `setActive(boolean)` | `void` | Enable/disable the mod |
| `getCurrentFps()` | `int` | FPS as seen by InputBooster |
| `getTotalHits()` | `long` | Total ATTACK_PRESSED events processed |
| `getRecoveredInputs()` | `long` | Inputs queued and replayed by polling thread |
| `getCurrentCps()` | `int` | CPS in the last second |
| `getMaxCps()` | `int` | Max CPS cap from config |
| `isBurstModeActive()` | `boolean` | True during a burst mode spike |
| `saveProfile(String)` | `boolean` | Save current settings as a named profile |
| `loadProfile(String)` | `boolean` | Load a profile by name |
| `getProfileNames()` | `List<String>` | Names of all saved profiles |
| `getAverageLatencyMs()` | `double` | Rolling average input latency |
| `getPeakLatencyMs()` | `double` | Session peak input latency |
| `resetPeakLatency()` | `void` | Reset peak counter |
| `registerListener(...)` | `void` | Register an event listener |
| `unregisterListener(...)` | `void` | Remove an event listener |

### `InputBoosterEvent.Type` (enum)

| Type | Payload |
|------|---------|
| `ATTACK_QUEUED` | — |
| `ATTACK_DRAINED` | — |
| `USE_QUEUED` | — |
| `SPRINT_QUEUED` | — |
| `JUMP_QUEUED` | — |
| `INPUT_QUEUED` | `stringValue()` = action name |
| `POLL_RATE_CHANGED` | `intValue()` = new Hz |
| `MODE_CHANGED` | `boolValue()` = true for AUTO |
| `BURST_MODE_STARTED` | — |
| `BURST_MODE_ENDED` | — |
| `MOD_TOGGLED` | `boolValue()` = new active state |
| `SAFE_MODE_ACTIVATED` | — |
| `PROFILE_LOADED` | `stringValue()` = profile name |
| `PROFILE_SAVED` | `stringValue()` = profile name |
| `CPS_CLICK_BLOCKED` | — |
| `CPS_CLICK_ACCEPTED` | `intValue()` = current CPS |

---

## Notes for InputBooster maintainer

To wire up the API in the main mod, two changes are needed:

**1. Add to `onInitializeClient()` (at the end, after `initialized.set(true)`):**
```java
InputBoosterAPIProvider.register(new InputBoosterAPIImpl());
```

**2. Add to `shutdown()`:**
```java
InputBoosterAPIProvider.unregister();
```

**3. Copy `InputBoosterAPIImpl.java`** from `impl-stub/` into your source tree at:
`src/main/java/dev/inputbooster/api/impl/InputBoosterAPIImpl.java`

**4. Fire events** from internal code by casting to the impl:
```java
if (InputBoosterAPIProvider.isAvailable()) {
    ((InputBoosterAPIImpl) InputBoosterAPIProvider.get())
        .fire(InputBoosterEvent.ofInt(InputBoosterEvent.Type.POLL_RATE_CHANGED, newHz));
}
```
