# InputBooster API

Public Fabric API for mods that integrate with InputBooster. It exposes live input stats, configuration controls, profile helpers, latency metrics, and an event listener surface without forcing dependent mods to require InputBooster at runtime.

## Compatibility

- Minecraft: `>=1.21 <1.22`
- Java: 21+
- Fabric Loader: `>=0.19.2`
- Current API version: `3.1.0`

The GitHub Actions workflow compiles the API against every stable Minecraft 1.21.x release reported by Fabric metadata.

## Features

- Real-time stats for poll rate, CPS, FPS, recovered inputs, and latency.
- Optional dependency helpers for safe integration when InputBooster is not installed.
- Event listener API for queued input, mode, profile, burst mode, and CPS events.
- Profile save/load helpers.
- One-call `InputBoosterStats.snapshot()` for HUD and debug overlays.

## Installation

```groovy
repositories {
    maven { url "https://maven.ahaduzzamankhan.dev/releases" }
}

dependencies {
    modImplementation "dev.inputbooster:inputbooster-api:3.1.0"

    // Optional dependency pattern:
    modCompileOnly "dev.inputbooster:inputbooster-api:3.1.0"
}
```

Required dependency:

```json
"depends": {
  "inputbooster": ">=3.1.0"
}
```

Optional dependency:

```json
"suggests": {
  "inputbooster": ">=3.1.0"
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

Optional dependency helper:

```java
InputBoosterCompat.ifLoaded(api -> {
    api.setMaxCps(16);
    api.registerListener(event -> myMod.onInputBoosterEvent(event));
});

int hz = InputBoosterCompat.getPollRateOrDefault(200);
InputBoosterStats.Snapshot stats = InputBoosterStats.snapshot();
```

## Releases

Pushing a tag like `v3.1.0` builds the jar, verifies every stable 1.21.x target, generates release notes from git history, and publishes a GitHub release with the jar attached.

You can also run the workflow manually and provide a version input such as `3.1.0`; the release job will publish `v3.1.0` from the selected commit.

## Maintainer Notes

Copy `impl-stub/java/dev/inputbooster/api/impl/InputBoosterAPIImpl.java` into the main InputBooster mod source tree and register it after the mod is initialized:

```java
InputBoosterAPIProvider.register(new InputBoosterAPIImpl());
```

Unregister on shutdown:

```java
InputBoosterAPIProvider.unregister();
```
