# InputBooster API

[![Modrinth](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/social/modrinth_vector.svg)](https://modrinth.com/mod/inputbooster)
[![Discord](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/social/discord_vector.svg)](https://discord.gg/your-invite)
[![GitHub](https://img.shields.io/github/stars/ahaduzzamankhan/inputbooster?style=flat-square&logo=github)](https://github.com/ahaduzzamankhan/inputbooster)
[![License](https://img.shields.io/github/license/ahaduzzamankhan/inputbooster?style=flat-square)](LICENSE)
[![Version](https://img.shields.io/modrinth/game-versions/inputbooster?style=flat-square&logo=fabric&label=Fabric)](https://modrinth.com/mod/inputbooster)

A public API for mods that depend on **InputBooster** — a Minecraft Fabric mod that enhances input handling with advanced polling, burst mode, and latency optimization.

---

## ✨ Features

- 🎯 **Real-time Stats** — Read poll rate, CPS, FPS, latency, and recovered inputs
- ⚙️ **Dynamic Configuration** — Change settings programmatically (Hz, auto mode, max CPS, profiles)
- 🔔 **Event System** — React to InputBooster events (attack queued, profile loaded, mode changes, etc.)
- 🛡️ **Optional Dependency** — Safe integration patterns for optional InputBooster dependency
- 📦 **Easy Integration** — Maven repository support + simple API

---

## 📦 Installation

### Add to your `build.gradle`

```groovy
repositories {
    maven { url "https://maven.ahaduzzamankhan.dev/releases" }
}

dependencies {
    // Required dependency
    modImplementation "dev.inputbooster:inputbooster-api:3.0.1"
    
    // OR optional dependency (InputBooster doesn't need to be installed)
    modCompileOnly "dev.inputbooster:inputbooster-api:3.0.1"
}
```

### Configure `fabric.mod.json`

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

## 🚀 Quick Start

### Read current poll rate
```java
import dev.inputbooster.api.InputBoosterAPI;

if (InputBoosterAPI.isLoaded()) {
    int hz = InputBoosterAPI.getInstance().getPollRateHz();
    System.out.println("InputBooster is polling at " + hz + " Hz");
}
```

### One-liner stats (no null checks!)
```java
import dev.inputbooster.api.util.InputBoosterStats;

int fps = InputBoosterStats.fps();           // 0 if not loaded
int hz = InputBoosterStats.hz();             // 0 if not loaded
String summary = InputBoosterStats.summary(); // Full status string
```

### Listen to events
```java
import dev.inputbooster.api.events.InputBoosterEvent;

InputBoosterAPI.getInstance().registerListener(event -> {
    switch (event.type()) {
        case POLL_RATE_CHANGED -> System.out.println("New Hz: " + event.intValue());
        case MOD_TOGGLED -> System.out.println("Active: " + event.boolValue());
        case PROFILE_LOADED -> System.out.println("Profile: " + event.stringValue());
    }
});
```

---

## 📚 Documentation

See the full [Integration Guide](docs/integration.md) for:
- Complete API reference
- Event types and payloads
- Profile management
- Optional dependency patterns
- Implementation guide for InputBooster maintainers

---

## 🔗 Links

- [📥 Download on Modrinth](https://modrinth.com/mod/inputbooster)
- [💬 Join our Discord](https://discord.gg/your-invite)
- [🐛 Report Issues](https://github.com/ahaduzzamankhan/inputbooster/issues)
- [📖 Full Documentation](docs/integration.md)

---

## 📄 License

This project is licensed under the [MIT License](LICENSE).

---

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request or open an issue on GitHub.
