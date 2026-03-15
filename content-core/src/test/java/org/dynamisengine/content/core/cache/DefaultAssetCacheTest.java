package org.dynamisengine.content.core.cache;

import org.dynamisengine.content.api.id.AssetId;
import org.dynamisengine.content.api.id.AssetType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultAssetCacheTest {

    private static final AssetType<String> TEXT = AssetType.of("demo.text", String.class);

    @Test
    void putAndGetWork() {
        DefaultAssetCache cache = new DefaultAssetCache();
        AssetId id = AssetId.of("alpha");

        cache.put(id, TEXT, "value");

        assertTrue(cache.get(id, TEXT).isPresent());
        assertEquals("value", cache.get(id, TEXT).orElseThrow());
    }

    @Test
    void invalidateRemovesAssetAcrossTypes() {
        DefaultAssetCache cache = new DefaultAssetCache();
        AssetId id = AssetId.of("alpha");

        cache.put(id, AssetType.of("demo.text", String.class), "value");
        cache.put(id, AssetType.of("demo.other", String.class), "other");

        cache.invalidate(id);

        assertTrue(cache.get(id, AssetType.of("demo.text", String.class)).isEmpty());
        assertTrue(cache.get(id, AssetType.of("demo.other", String.class)).isEmpty());
    }

    @Test
    void getWithWrongClassThrowsClassCastException() {
        DefaultAssetCache cache = new DefaultAssetCache();
        AssetId id = AssetId.of("alpha");

        cache.put(id, AssetType.of("demo.text", String.class), "value");

        assertThrows(ClassCastException.class, () -> cache.get(id, AssetType.of("demo.text", Integer.class)));
    }
}
