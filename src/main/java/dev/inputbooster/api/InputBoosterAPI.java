package dev.inputbooster.api;

/**
 * InputBoosterAPI — Entry point for all mods that depend on InputBooster.
 *
 * <h2>Quick Start</h2>
 * <pre>{@code
 * // Check mod is present and get the instance
 * if (InputBoosterAPI.isLoaded()) {
 *     InputBoosterAPI api = InputBoosterAPI.getInstance();
 *     int hz = api.getPollRateHz();
 * }
 * }</pre>
 *
 * <h2>Dependency Setup (build.gradle)</h2>
 * <pre>
 * repositories {
 *     maven { url "https://maven.fabricmc.net/" }
 * }
 * dependencies {
 *     modImplementation "dev.inputbooster:inputbooster-api:3.1.0"
 * }
 * </pre>
 *
 * <h2>fabric.mod.json</h2>
 * <pre>
 * "depends": {
 *     "inputbooster": ">=3.1.0"
 * }
 * </pre>
 *
 * @author Ahaduzzaman Khan
 * @version 3.1.0
 */
public interface InputBoosterAPI {

    // ── Singleton access ──────────────────────────────────────────────────────

    /**
     * Returns the live InputBoosterAPI instance.
     * Never null when InputBooster is loaded; throws if the mod is absent
     * (guard with {@link #isLoaded()} first).
     */
    static InputBoosterAPI getInstance() {
        return InputBoosterAPIProvider.get();
    }

    /**
     * Returns true if InputBooster is present and fully initialized.
     * Always call this before {@link #getInstance()} to avoid crashes
     * when InputBooster is an optional dependency.
     */
    static boolean isLoaded() {
        return InputBoosterAPIProvider.isAvailable();
    }

    /**
     * Returns the live API instance if available.
     */
    static java.util.Optional<InputBoosterAPI> getOptional() {
        return isLoaded()
            ? java.util.Optional.of(getInstance())
            : java.util.Optional.empty();
    }

    // ── Poll rate ─────────────────────────────────────────────────────────────

    /** Current active poll rate in Hz (60–1000). */
    int getPollRateHz();

    /** Set the poll rate. Value is clamped to [60, 1000]. */
    void setPollRateHz(int hz);

    /** True when the mod is in AUTO (FPS-adaptive) mode. */
    boolean isAutoMode();

    /** Switch between AUTO and MANUAL mode. */
    void setAutoMode(boolean auto);

    // ── Mod state ─────────────────────────────────────────────────────────────

    /** True when InputBooster is actively processing inputs. */
    boolean isActive();

    /** Enable or disable InputBooster processing. */
    void setActive(boolean active);

    /** True when the game has a player loaded and InputBooster is ready. */
    boolean isGameReady();

    /** Current FPS as seen by the mod. */
    int getCurrentFps();

    // ── Statistics ────────────────────────────────────────────────────────────

    /** Total inputs recovered this session (across all key types). */
    long getTotalHits();

    /** Total inputs that were queued and replayed by the polling thread. */
    long getRecoveredInputs();

    /** CPS for the last second (accepted clicks, after the limiter). */
    int getCurrentCps();

    /** Max CPS cap from config. */
    int getMaxCps();

    // ── Feature states ────────────────────────────────────────────────────────

    boolean isSprintFixEnabled();
    boolean isAutoSprintEnabled();
    boolean isWTapAssistEnabled();
    boolean isAntiIdleEnabled();
    boolean isAutoStrafeEnabled();
    boolean isCpsLimiterEnabled();
    boolean isBurstModeEnabled();
    boolean isBurstModeActive();

    // ── Config setters ────────────────────────────────────────────────────────

    void setSprintFixEnabled(boolean enabled);
    void setAutoSprintEnabled(boolean enabled);
    void setWTapAssistEnabled(boolean enabled);
    void setAntiIdleEnabled(boolean enabled);
    void setAutoStrafeEnabled(boolean enabled);
    void setCpsLimiterEnabled(boolean enabled);
    void setBurstModeEnabled(boolean enabled);
    void setMaxCps(int maxCps);

    // ── Profiles ──────────────────────────────────────────────────────────────

    /**
     * Save the current settings as a named profile.
     * Returns false if the profile list is full and the name is new.
     */
    boolean saveProfile(String name);

    /**
     * Load a previously saved profile by name.
     * Returns false if no profile with that name exists.
     */
    boolean loadProfile(String name);

    /** Names of all currently saved profiles. Never null. */
    java.util.List<String> getProfileNames();

    // ── Latency ───────────────────────────────────────────────────────────────

    /** Rolling average input-to-drain latency in milliseconds. */
    double getAverageLatencyMs();

    /** Peak input-to-drain latency in milliseconds (session-wide). */
    double getPeakLatencyMs();

    /** Reset the peak latency counter. */
    void resetPeakLatency();

    // ── Events ────────────────────────────────────────────────────────────────

    /**
     * Register a listener that is called every time InputBooster queues a
     * {@link dev.inputbooster.api.events.InputBoosterEvent}.
     *
     * <pre>{@code
     * InputBoosterAPI.getInstance().registerListener(event -> {
     *     if (event.type() == InputBoosterEvent.Type.POLL_RATE_CHANGED) {
     *         System.out.println("New Hz: " + event.intValue());
     *     }
     * });
     * }</pre>
     */
    void registerListener(dev.inputbooster.api.events.InputBoosterEventListener listener);

    /** Unregister a previously registered listener. */
    void unregisterListener(dev.inputbooster.api.events.InputBoosterEventListener listener);
}
