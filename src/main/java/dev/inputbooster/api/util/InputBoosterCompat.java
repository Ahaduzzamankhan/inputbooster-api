package dev.inputbooster.api.util;

import dev.inputbooster.api.InputBoosterAPI;
import dev.inputbooster.api.events.InputBoosterEvent;
import dev.inputbooster.api.events.InputBoosterEventListener;

/**
 * Compatibility utilities for mods that list InputBooster as an
 * <em>optional</em> dependency.
 *
 * <h2>Pattern for optional dependency</h2>
 * <p>In {@code fabric.mod.json} use {@code "suggests"} instead of {@code "depends"}:</p>
 * <pre>
 * "suggests": {
 *     "inputbooster": "*"
 * }
 * </pre>
 *
 * <p>Then guard all API calls with the helpers here:</p>
 * <pre>{@code
 * // In your mod init:
 * InputBoosterCompat.ifLoaded(api -> {
 *     api.registerListener(myListener);
 *     api.setMaxCps(15);
 * });
 *
 * // For quick reads:
 * int hz = InputBoosterCompat.getPollRateOrDefault(200);
 * }</pre>
 *
 * @author Ahaduzzaman Khan
 */
public final class InputBoosterCompat {

    private InputBoosterCompat() {}

    /**
     * Runs {@code action} with the API instance if InputBooster is loaded.
     * Does nothing (no exception) if the mod is absent.
     */
    public static void ifLoaded(java.util.function.Consumer<InputBoosterAPI> action) {
        if (InputBoosterAPI.isLoaded()) {
            action.accept(InputBoosterAPI.getInstance());
        }
    }

    /**
     * Returns the poll rate if InputBooster is loaded, or {@code defaultHz} otherwise.
     */
    public static int getPollRateOrDefault(int defaultHz) {
        return InputBoosterAPI.isLoaded()
               ? InputBoosterAPI.getInstance().getPollRateHz()
               : defaultHz;
    }

    /**
     * Registers a listener only if InputBooster is loaded.
     * Returns true if the listener was registered, false if InputBooster is absent.
     */
    public static boolean tryRegisterListener(InputBoosterEventListener listener) {
        if (InputBoosterAPI.isLoaded()) {
            InputBoosterAPI.getInstance().registerListener(listener);
            return true;
        }
        return false;
    }

    /**
     * Registers a listener only for specific event types.
     * All other event types are silently ignored.
     *
     * <pre>{@code
     * InputBoosterCompat.registerForTypes(
     *     event -> doSomething(event.intValue()),
     *     InputBoosterEvent.Type.POLL_RATE_CHANGED,
     *     InputBoosterEvent.Type.MODE_CHANGED
     * );
     * }</pre>
     */
    public static boolean registerForTypes(
            InputBoosterEventListener listener,
            InputBoosterEvent.Type... types) {
        if (!InputBoosterAPI.isLoaded()) return false;
        java.util.Set<InputBoosterEvent.Type> allowed =
            java.util.EnumSet.copyOf(java.util.Arrays.asList(types));
        InputBoosterAPI.getInstance().registerListener(event -> {
            if (allowed.contains(event.type())) {
                listener.onEvent(event);
            }
        });
        return true;
    }

    /**
     * Returns the API version string (same as the mod version).
     * Returns "not loaded" if InputBooster is absent.
     */
    public static String getVersion() {
        // Fabric's mod metadata is the canonical source; fall back gracefully.
        if (!InputBoosterAPI.isLoaded()) return "not loaded";
        try {
            return net.fabricmc.loader.api.FabricLoader.getInstance()
                .getModContainer("inputbooster")
                .map(c -> c.getMetadata().getVersion().getFriendlyString())
                .orElse("unknown");
        } catch (Exception e) {
            return "unknown";
        }
    }
}
