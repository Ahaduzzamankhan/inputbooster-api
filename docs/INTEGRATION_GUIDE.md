# InputBooster API Integration Guide

The InputBooster API lets your mod read stats, change settings, and react to InputBooster events. It supports both required and optional dependency patterns.

## Setup

Add the dependency:

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

Declare the dependency in `fabric.mod.json`.

Required:

```json
"depends": {
  "inputbooster": ">=3.1.0"
}
```

Optional:

```json
"suggests": {
  "inputbooster": ">=3.1.0"
}
```

## Examples

Read the current poll rate:

```java
if (InputBoosterAPI.isLoaded()) {
    int hz = InputBoosterAPI.getInstance().getPollRateHz();
    System.out.println("InputBooster is polling at " + hz + " Hz");
}
```

Change settings:

```java
InputBoosterAPI api = InputBoosterAPI.getInstance();
api.setPollRateHz(500);
api.setAutoMode(false);
api.setMaxCps(15);
api.setBurstModeEnabled(true);
```

Listen to events:

```java
InputBoosterAPI.getInstance().registerListener(event -> {
    switch (event.type()) {
        case ATTACK_QUEUED -> onAttackQueued();
        case POLL_RATE_CHANGED -> System.out.println("New Hz: " + event.intValue());
        case MOD_TOGGLED -> System.out.println("Active: " + event.boolValue());
        case BURST_MODE_STARTED -> System.out.println("Burst mode on!");
        case PROFILE_LOADED -> System.out.println("Profile: " + event.stringValue());
        default -> {}
    }
});
```

Optional dependency helper:

```java
InputBoosterCompat.ifLoaded(api -> {
    api.setMaxCps(16);
    api.registerListener(event -> myMod.onInputBoosterEvent(event));
});

int hz = InputBoosterCompat.getPollRateOrDefault(200);
String version = InputBoosterCompat.getVersion();
boolean active = InputBoosterCompat.mapOrDefault(InputBoosterAPI::isActive, false);
```

Stats snapshot:

```java
InputBoosterStats.Snapshot stats = InputBoosterStats.snapshot();
if (stats.loaded()) {
    drawHud(stats.pollRateHz(), stats.currentCps(), stats.averageLatencyMs());
}
```

Profiles:

```java
InputBoosterAPI api = InputBoosterAPI.getInstance();
api.saveProfile("PvP");
api.loadProfile("PvP");
List<String> names = api.getProfileNames();
```

## Maintainer Wiring

Copy `impl-stub/java/dev/inputbooster/api/impl/InputBoosterAPIImpl.java` into the main InputBooster mod source tree at:

```text
src/main/java/dev/inputbooster/api/impl/InputBoosterAPIImpl.java
```

Register after InputBooster is initialized:

```java
InputBoosterAPIProvider.register(new InputBoosterAPIImpl());
```

Unregister during shutdown:

```java
InputBoosterAPIProvider.unregister();
```

Fire events from internal code by casting to the implementation:

```java
if (InputBoosterAPIProvider.isAvailable()) {
    ((InputBoosterAPIImpl) InputBoosterAPIProvider.get())
        .fire(InputBoosterEvent.ofInt(InputBoosterEvent.Type.POLL_RATE_CHANGED, newHz));
}
```
