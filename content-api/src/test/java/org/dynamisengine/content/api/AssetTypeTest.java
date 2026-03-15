package org.dynamisengine.content.api;

import org.dynamisengine.content.api.id.AssetType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AssetTypeTest {

    @Test
    void factoryRejectsBlankId() {
        assertThrows(IllegalArgumentException.class, () -> AssetType.of(" ", String.class));
    }

    @Test
    void factoryRejectsNullType() {
        assertThrows(NullPointerException.class, () -> AssetType.of("demo.text", null));
    }

    @Test
    void equalityIsBasedOnIdOnly() {
        AssetType<String> left = AssetType.of("demo.text", String.class);
        AssetType<Object> right = AssetType.of("demo.text", Object.class);

        assertEquals(left, right);
        assertEquals(left.hashCode(), right.hashCode());
    }
}
