package dev.inputbooster.api.events;

/**
 * Functional interface for receiving {@link InputBoosterEvent} notifications.
 *
 * <pre>{@code
 * InputBoosterAPI.getInstance().registerListener(event -> {
 *     if (event.type() == InputBoosterEvent.Type.ATTACK_QUEUED) {
 *         // your logic here
 *     }
 * });
 * }</pre>
 *
 * Listeners are called on the <b>Minecraft main thread</b> from within
 * InputBooster's client tick handler. Keep implementations short and
 * non-blocking; do not call OpenGL or do heavy I/O here.
 *
 * @author Ahaduzzaman Khan
 */
@FunctionalInterface
public interface InputBoosterEventListener {
    /**
     * Called when InputBooster fires an event.
     *
     * @param event the event; never null
     */
    void onEvent(InputBoosterEvent event);
}
