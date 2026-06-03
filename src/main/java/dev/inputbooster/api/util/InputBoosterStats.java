package dev.inputbooster.api.util;

import dev.inputbooster.api.InputBoosterAPI;

/**
 * Convenience wrapper around {@link InputBoosterAPI} for accessing live stats
 * without holding a reference to the API instance yourself.
 *
 * <pre>{@code
 * int fps = InputBoosterStats.fps();
 * int hz  = InputBoosterStats.hz();
 * long recovered = InputBoosterStats.recovered();
 * }</pre>
 *
 * All methods return safe default values (0, false) if InputBooster is not loaded.
 *
 * @author Ahaduzzaman Khan
 */
public final class InputBoosterStats {

    private InputBoosterStats() {}

    /**
     * Immutable snapshot of the most commonly displayed InputBooster stats.
     */
    public record Snapshot(
        boolean loaded,
        boolean active,
        boolean autoMode,
        boolean burstModeActive,
        int pollRateHz,
        int fps,
        int currentCps,
        int maxCps,
        long recoveredInputs,
        long totalHits,
        double averageLatencyMs,
        double peakLatencyMs
    ) {
        public static Snapshot unloaded() {
            return new Snapshot(false, false, false, false, 0, 0, 0, 0, 0L, 0L, 0.0, 0.0);
        }
    }

    /** Current poll rate in Hz, or 0 if not loaded. */
    public static int hz() {
        return InputBoosterAPI.isLoaded() ? InputBoosterAPI.getInstance().getPollRateHz() : 0;
    }

    /** Current FPS as seen by InputBooster, or 0 if not loaded. */
    public static int fps() {
        return InputBoosterAPI.isLoaded() ? InputBoosterAPI.getInstance().getCurrentFps() : 0;
    }

    /** Total inputs recovered this session, or 0 if not loaded. */
    public static long recovered() {
        return InputBoosterAPI.isLoaded() ? InputBoosterAPI.getInstance().getRecoveredInputs() : 0;
    }

    /** Total hits (total ATTACK_PRESSED events processed), or 0 if not loaded. */
    public static long totalHits() {
        return InputBoosterAPI.isLoaded() ? InputBoosterAPI.getInstance().getTotalHits() : 0;
    }

    /** Current CPS (last second), or 0 if not loaded. */
    public static int cps() {
        return InputBoosterAPI.isLoaded() ? InputBoosterAPI.getInstance().getCurrentCps() : 0;
    }

    /** True when InputBooster burst mode is currently active. */
    public static boolean isBursting() {
        return InputBoosterAPI.isLoaded() && InputBoosterAPI.getInstance().isBurstModeActive();
    }

    /** Average input-to-drain latency in ms, or 0 if not loaded. */
    public static double avgLatencyMs() {
        return InputBoosterAPI.isLoaded() ? InputBoosterAPI.getInstance().getAverageLatencyMs() : 0.0;
    }

    /** Peak input-to-drain latency in ms, or 0 if not loaded. */
    public static double peakLatencyMs() {
        return InputBoosterAPI.isLoaded() ? InputBoosterAPI.getInstance().getPeakLatencyMs() : 0.0;
    }

    /** True if InputBooster is active and processing inputs. */
    public static boolean isActive() {
        return InputBoosterAPI.isLoaded() && InputBoosterAPI.getInstance().isActive();
    }

    /** True if InputBooster is in AUTO (FPS-adaptive) mode. */
    public static boolean isAutoMode() {
        return InputBoosterAPI.isLoaded() && InputBoosterAPI.getInstance().isAutoMode();
    }

    /**
     * Captures a consistent one-call view of the current InputBooster stats.
     */
    public static Snapshot snapshot() {
        if (!InputBoosterAPI.isLoaded()) return Snapshot.unloaded();
        InputBoosterAPI api = InputBoosterAPI.getInstance();
        return new Snapshot(
            true,
            api.isActive(),
            api.isAutoMode(),
            api.isBurstModeActive(),
            api.getPollRateHz(),
            api.getCurrentFps(),
            api.getCurrentCps(),
            api.getMaxCps(),
            api.getRecoveredInputs(),
            api.getTotalHits(),
            api.getAverageLatencyMs(),
            api.getPeakLatencyMs()
        );
    }

    /**
     * Returns a human-readable one-line summary of InputBooster state,
     * suitable for HUD overlays or debug screens.
     *
     * Example: {@code "InputBooster | 200 Hz | AUTO | CPS 14/20 | Latency 3.1ms"}
     */
    public static String summary() {
        if (!InputBoosterAPI.isLoaded()) return "InputBooster: not loaded";
        InputBoosterAPI api = InputBoosterAPI.getInstance();
        return String.format(
            "InputBooster | %d Hz | %s | CPS %d/%d | Latency %.1fms",
            api.getPollRateHz(),
            api.isAutoMode() ? "AUTO" : "MANUAL",
            api.getCurrentCps(),
            api.getMaxCps(),
            api.getAverageLatencyMs()
        );
    }
}
