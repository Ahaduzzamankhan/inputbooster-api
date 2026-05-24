package dev.inputbooster.api.events;

/**
 * Represents an event fired by InputBooster.
 *
 * <h2>Example usage</h2>
 * <pre>{@code
 * InputBoosterAPI.getInstance().registerListener(event -> {
 *     switch (event.type()) {
 *         case ATTACK_QUEUED       -> myMod.onAttack();
 *         case POLL_RATE_CHANGED   -> System.out.println("Hz: " + event.intValue());
 *         case MOD_TOGGLED         -> System.out.println("Active: " + event.boolValue());
 *         case BURST_MODE_STARTED  -> System.out.println("Burst!");
 *     }
 * });
 * }</pre>
 *
 * @author Ahaduzzaman Khan
 */
public final class InputBoosterEvent {

    /**
     * All event types fired by InputBooster.
     */
    public enum Type {
        // Input events
        ATTACK_QUEUED,
        ATTACK_DRAINED,
        USE_QUEUED,
        SPRINT_QUEUED,
        JUMP_QUEUED,
        /** Any other input action. {@link InputBoosterEvent#actionName()} identifies it. */
        INPUT_QUEUED,
        // Poll rate
        /** intValue() = new Hz */
        POLL_RATE_CHANGED,
        /** boolValue() = true for AUTO */
        MODE_CHANGED,
        // Burst
        BURST_MODE_STARTED,
        BURST_MODE_ENDED,
        // Lifecycle
        /** boolValue() = new active state */
        MOD_TOGGLED,
        SAFE_MODE_ACTIVATED,
        /** stringValue() = profile name */
        PROFILE_LOADED,
        /** stringValue() = profile name */
        PROFILE_SAVED,
        // CPS
        CPS_CLICK_BLOCKED,
        /** intValue() = current CPS */
        CPS_CLICK_ACCEPTED,
    }

    private final Type    type;
    private final long    timestamp;
    private final int     intPayload;
    private final boolean boolPayload;
    private final String  stringPayload;

    private InputBoosterEvent(Type type, int intPayload, boolean boolPayload, String stringPayload) {
        this.type          = type;
        this.timestamp     = System.nanoTime();
        this.intPayload    = intPayload;
        this.boolPayload   = boolPayload;
        this.stringPayload = stringPayload != null ? stringPayload : "";
    }

    public static InputBoosterEvent of(Type type) {
        return new InputBoosterEvent(type, 0, false, null);
    }
    public static InputBoosterEvent ofInt(Type type, int value) {
        return new InputBoosterEvent(type, value, false, null);
    }
    public static InputBoosterEvent ofBool(Type type, boolean value) {
        return new InputBoosterEvent(type, 0, value, null);
    }
    public static InputBoosterEvent ofString(Type type, String value) {
        return new InputBoosterEvent(type, 0, false, value);
    }

    public Type    type()        { return type; }
    public long    timestamp()   { return timestamp; }
    public int     intValue()    { return intPayload; }
    public boolean boolValue()   { return boolPayload; }
    public String  stringValue() { return stringPayload; }
    public String  actionName()  { return stringPayload; }

    @Override
    public String toString() {
        return "InputBoosterEvent{type=" + type + ", int=" + intPayload +
               ", bool=" + boolPayload + ", str='" + stringPayload + "'}";
    }
}
