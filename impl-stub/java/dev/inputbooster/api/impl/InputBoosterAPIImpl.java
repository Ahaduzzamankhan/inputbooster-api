package dev.inputbooster.api.impl;

import dev.inputbooster.InputBoosterConfig;
import dev.inputbooster.InputBoosterMod;
import dev.inputbooster.api.InputBoosterAPI;
import dev.inputbooster.api.InputBoosterAPIProvider;
import dev.inputbooster.api.events.InputBoosterEvent;
import dev.inputbooster.api.events.InputBoosterEventListener;
import dev.inputbooster.feature.LatencyProfiler;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Implementation of {@link InputBoosterAPI}.
 *
 * Copy this file into the main InputBooster mod source tree:
 *   src/main/java/dev/inputbooster/api/impl/InputBoosterAPIImpl.java
 *
 * Then call {@code InputBoosterAPIProvider.register(new InputBoosterAPIImpl())}
 * at the end of {@code InputBoosterMod.onInitializeClient()}.
 *
 * Also call {@code InputBoosterAPIProvider.unregister()} inside
 * {@code InputBoosterMod.shutdown()}.
 *
 * @author Ahaduzzaman Khan
 */
public final class InputBoosterAPIImpl implements InputBoosterAPI {

    // Thread-safe listener list — CopyOnWriteArrayList is ideal for
    // read-heavy workloads with infrequent adds/removes.
    private final CopyOnWriteArrayList<InputBoosterEventListener> listeners =
        new CopyOnWriteArrayList<>();

    // ── Poll rate ─────────────────────────────────────────────────────────────

    @Override public int getPollRateHz() { return InputBoosterMod.currentPollHz; }

    @Override
    public void setPollRateHz(int hz) {
        hz = Math.max(60, Math.min(1000, hz));
        InputBoosterConfig.setPollRateHz(hz);
        InputBoosterMod.adjustPollRateManual();
        fire(InputBoosterEvent.ofInt(InputBoosterEvent.Type.POLL_RATE_CHANGED, hz));
    }

    @Override public boolean isAutoMode() { return InputBoosterConfig.isPollRateAutoMode(); }

    @Override
    public void setAutoMode(boolean auto) {
        InputBoosterConfig.setPollRateAutoMode(auto);
        fire(InputBoosterEvent.ofBool(InputBoosterEvent.Type.MODE_CHANGED, auto));
    }

    // ── Mod state ─────────────────────────────────────────────────────────────

    @Override public boolean isActive()    { return InputBoosterMod.active; }
    @Override public boolean isGameReady() { return InputBoosterMod.gameReady; }
    @Override public int getCurrentFps()   { return InputBoosterMod.currentFps; }

    @Override
    public void setActive(boolean active) {
        InputBoosterMod.active = active;
        fire(InputBoosterEvent.ofBool(InputBoosterEvent.Type.MOD_TOGGLED, active));
    }

    // ── Statistics ────────────────────────────────────────────────────────────

    @Override public long getTotalHits()      { return InputBoosterMod.totalHits.get(); }
    @Override public long getRecoveredInputs(){ return InputBoosterMod.recoveredInputs.get(); }
    @Override public int  getCurrentCps()     {
        return InputBoosterMod.cpsLimiter != null ? InputBoosterMod.cpsLimiter.getCps() : 0;
    }
    @Override public int getMaxCps() { return InputBoosterConfig.getMaxCps(); }

    // ── Feature states ────────────────────────────────────────────────────────

    @Override public boolean isSprintFixEnabled()  { return InputBoosterConfig.isSprintFixEnabled(); }
    @Override public boolean isAutoSprintEnabled() { return InputBoosterConfig.isAutoSprintEnabled(); }
    @Override public boolean isWTapAssistEnabled() { return InputBoosterConfig.isWTapAssistEnabled(); }
    @Override public boolean isAntiIdleEnabled()   { return InputBoosterConfig.isAntiIdleEnabled(); }
    @Override public boolean isAutoStrafeEnabled() { return InputBoosterConfig.isAutoStrafeEnabled(); }
    @Override public boolean isCpsLimiterEnabled() { return InputBoosterConfig.isCpsLimiterEnabled(); }
    @Override public boolean isBurstModeEnabled()  { return InputBoosterConfig.isBurstModeEnabled(); }
    @Override public boolean isBurstModeActive()   {
        return InputBoosterMod.burstMode != null && InputBoosterMod.burstMode.isBursting();
    }

    // ── Config setters ────────────────────────────────────────────────────────

    @Override public void setSprintFixEnabled(boolean v)  { InputBoosterConfig.setSprintFixEnabled(v); }
    @Override public void setAutoSprintEnabled(boolean v) { InputBoosterConfig.setAutoSprintEnabled(v); }
    @Override public void setWTapAssistEnabled(boolean v) { InputBoosterConfig.setWTapAssistEnabled(v); }
    @Override public void setAntiIdleEnabled(boolean v)   { InputBoosterConfig.setAntiIdleEnabled(v); }
    @Override public void setAutoStrafeEnabled(boolean v) { InputBoosterConfig.setAutoStrafeEnabled(v); }
    @Override public void setCpsLimiterEnabled(boolean v) { InputBoosterConfig.setCpsLimiterEnabled(v); }
    @Override public void setBurstModeEnabled(boolean v)  { InputBoosterConfig.setBurstModeEnabled(v); }
    @Override public void setMaxCps(int maxCps)           { InputBoosterConfig.setMaxCps(maxCps); }

    // ── Profiles ──────────────────────────────────────────────────────────────

    @Override
    public boolean saveProfile(String name) {
        if (InputBoosterMod.profileManager == null) return false;
        boolean ok = InputBoosterMod.profileManager.saveProfile(name);
        if (ok) fire(InputBoosterEvent.ofString(InputBoosterEvent.Type.PROFILE_SAVED, name));
        return ok;
    }

    @Override
    public boolean loadProfile(String name) {
        if (InputBoosterMod.profileManager == null) return false;
        boolean ok = InputBoosterMod.profileManager.loadProfile(name, null);
        if (ok) fire(InputBoosterEvent.ofString(InputBoosterEvent.Type.PROFILE_LOADED, name));
        return ok;
    }

    @Override
    public List<String> getProfileNames() {
        if (InputBoosterMod.profileManager == null) return List.of();
        return InputBoosterMod.profileManager.getProfiles()
            .stream()
            .map(p -> p.name())
            .toList();
    }

    // ── Latency ───────────────────────────────────────────────────────────────

    @Override public double getAverageLatencyMs() { return LatencyProfiler.getAverageMs(); }
    @Override public double getPeakLatencyMs()    { return LatencyProfiler.getPeakMs(); }
    @Override public void   resetPeakLatency()    { LatencyProfiler.resetPeak(); }

    // ── Events ────────────────────────────────────────────────────────────────

    @Override
    public void registerListener(InputBoosterEventListener listener) {
        if (listener != null) listeners.addIfAbsent(listener);
    }

    @Override
    public void unregisterListener(InputBoosterEventListener listener) {
        listeners.remove(listener);
    }

    /**
     * Fire an event to all registered listeners.
     * Call this from InputBooster's internal code wherever a notable
     * state change happens (poll rate change, burst mode start, etc.).
     *
     * This method is NOT part of the public API interface — it is only
     * accessible to InputBooster's own code.
     */
    public void fire(InputBoosterEvent event) {
        for (InputBoosterEventListener listener : listeners) {
            try {
                listener.onEvent(event);
            } catch (Exception e) {
                InputBoosterMod.LOGGER.warn(
                    "[InputBooster API] Listener threw exception for event {}: {}",
                    event.type(), e.getMessage());
            }
        }
    }
}
