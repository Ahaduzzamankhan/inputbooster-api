package dev.inputbooster.api.util;

import dev.inputbooster.api.InputBoosterAPIProvider;
import dev.inputbooster.api.events.InputBoosterEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InputBoosterCompatTest {

    @AfterEach
    void tearDown() {
        InputBoosterAPIProvider.unregister();
    }

    @Test
    void registerForTypesReturnsFalseWhenNoTypesAreProvided() {
        assertFalse(InputBoosterCompat.registerForTypes(event -> {}, new InputBoosterEvent.Type[0]));
    }

    @Test
    void registerForTypesRejectsNullListener() {
        assertThrows(NullPointerException.class,
            () -> InputBoosterCompat.registerForTypes(null, InputBoosterEvent.Type.MOD_TOGGLED));
    }

    @Test
    void mapOrDefaultReturnsFallbackWhenApiIsAbsent() {
        assertFalse(InputBoosterCompat.mapOrDefault(api -> true, false));
    }
}
