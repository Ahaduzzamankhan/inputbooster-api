# ⚠️ InputBooster API — No Longer Maintained

> **Maintenance has ended.** This repository is no longer actively developed or supported. It is preserved for reference only.

---

# InputBooster API

Public Fabric API for mods that integrate with InputBooster. It exposes live input stats, configuration controls, profile helpers, latency metrics, and an event listener surface without forcing dependent mods to require InputBooster at runtime.

## Compatibility
- Minecraft: `>=1.21 <1.22`
- Java: 21+
- Fabric Loader: `>=0.19.2`
- Current API version: `3.1.0`

## Features
- Real-time stats for poll rate, CPS, FPS, recovered inputs, and latency.
- Optional dependency helpers.
- Event listener API.
- Profile save/load helpers.
- `InputBoosterStats.snapshot()` for HUD and debug overlays.

## Installation
```groovy
repositories {
    maven { url "https://maven.ahaduzzamankhan.dev/releases" }
}

dependencies {
    modImplementation "dev.inputbooster:inputbooster-api:3.1.0"
}
```

## Quick Use
```java
if (InputBoosterAPI.isLoaded()) {
    InputBoosterAPI api = InputBoosterAPI.getInstance();
    int hz = api.getPollRateHz();
    api.setAutoMode(true);
}
```

## Releases
Pushing a tag like `v3.1.0` builds the jar, verifies stable 1.21.x targets, generates release notes, and publishes a GitHub release.

## Maintainer Notes
Copy `impl-stub/java/dev/inputbooster/api/impl/InputBoosterAPIImpl.java` into the main InputBooster mod source tree and register it after initialization.

## Project Status
Development has ended. This API should not be assumed to receive compatibility updates for future Minecraft or InputBooster versions.
