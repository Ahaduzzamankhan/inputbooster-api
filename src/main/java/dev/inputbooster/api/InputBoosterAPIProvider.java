package dev.inputbooster.api;

/**
 * Internal singleton holder for the InputBoosterAPI implementation.
 *
 * InputBooster's main mod class calls {@link #register(InputBoosterAPI)}
 * during {@code onInitializeClient()}. Dependent mods never call this directly.
 *
 * @author Ahaduzzaman Khan
 */
public final class InputBoosterAPIProvider {

    private static volatile InputBoosterAPI instance = null;

    private InputBoosterAPIProvider() {}

    /**
     * Called exclusively by InputBooster's own init code.
     * Dependent mods should never call this.
     */
    public static void register(InputBoosterAPI impl) {
        if (impl == null) throw new IllegalArgumentException("InputBoosterAPI impl must not be null");
        if (instance != null) throw new IllegalStateException("InputBoosterAPI already registered");
        instance = impl;
    }

    /**
     * Returns the registered API instance.
     * @throws IllegalStateException if InputBooster has not been initialized yet
     */
    public static InputBoosterAPI get() {
        InputBoosterAPI api = instance;
        if (api == null) {
            throw new IllegalStateException(
                "InputBoosterAPI is not available. " +
                "Check InputBoosterAPI.isLoaded() before calling getInstance(), " +
                "or add 'inputbooster' as a required dependency in your fabric.mod.json.");
        }
        return api;
    }

    /** Safe check — never throws. */
    public static boolean isAvailable() {
        return instance != null;
    }

    /** Internal: called by InputBooster during shutdown. */
    public static void unregister() {
        instance = null;
    }
}
