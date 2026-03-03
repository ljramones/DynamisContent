package org.dynamiscontent.api;

import org.dynamiscontent.api.id.AssetId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AssetIdTest {

    @Test
    void constructorRejectsBlankValue() {
        assertThrows(IllegalArgumentException.class, () -> new AssetId(" "));
    }

    @Test
    void constructorAcceptsNonBlankValue() {
        AssetId id = new AssetId("textures/sky/sunset.hdr");
        assertEquals("textures/sky/sunset.hdr", id.value());
    }
}
