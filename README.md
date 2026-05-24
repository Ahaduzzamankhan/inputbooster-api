# InputBooster API

<p align="left">
  <a href="https://modrinth.com/mod/inputbooster"><img src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3.3.1/assets/compact/available/modrinth_vector.svg" alt="Modrinth"></a>
  <a href="https://fabricmc.net/"><img src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3.3.1/assets/compact/supported/fabric_vector.svg" alt="Fabric"></a>
  <a href="https://github.com/ahaduzzamankhan/inputbooster"><img src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3.3.1/assets/compact/supported/github_vector.svg" alt="GitHub"></a>
  <a href="https://discord.gg/your-invite"><img src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3.3.1/assets/compact/available/discord_vector.svg" alt="Discord"></a>
  <a href="LICENSE"><img src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3.3.1/assets/compact/documentation/mit-license_vector.svg" alt="License"></a>
</p>

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
    maven { url "[https://maven.ahaduzzamankhan.dev/releases](https://maven.ahaduzzamankhan.dev/releases)" }
}

dependencies {
    // Required dependency
    modImplementation "dev.inputbooster:inputbooster-api:3.0.1"
    
    // OR optional dependency (InputBooster doesn't need to be installed)
    modCompileOnly "dev.inputbooster:inputbooster-api:3.0.1"
}