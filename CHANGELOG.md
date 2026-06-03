# Changelog

## 3.1.0

- Declared Minecraft support as `>=1.21 <1.22` in `fabric.mod.json`.
- Added GitHub Actions builds, 1.21.x compatibility checks, and tagged release publishing.
- Added `InputBoosterAPI.getOptional()`, `InputBoosterCompat.getOptional()`, and `InputBoosterCompat.mapOrDefault(...)`.
- Added `InputBoosterStats.Snapshot` and `InputBoosterStats.snapshot()` for one-call HUD/debug reads.
- Fixed `InputBoosterCompat.registerForTypes(...)` throwing when called with no event types.
- Added missing license and cleaned build metadata.
