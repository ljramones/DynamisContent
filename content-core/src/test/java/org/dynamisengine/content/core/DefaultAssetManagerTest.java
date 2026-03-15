package org.dynamisengine.content.core;

import org.dynamisengine.core.exception.DynamisException;
import org.dynamisengine.content.api.id.AssetId;
import org.dynamisengine.content.api.id.AssetType;
import org.dynamisengine.content.api.loader.AssetLoader;
import org.dynamisengine.content.api.manifest.AssetManifest;
import org.dynamisengine.content.core.cache.DefaultAssetCache;
import org.dynamisengine.content.core.manifest.AssetManifestBuilder;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DefaultAssetManagerTest {

    private static final AssetType<String> TEXT = AssetType.of("demo.text", String.class);

    @Test
    void cacheHitAvoidsReload() {
        AtomicInteger loadCounter = new AtomicInteger();

        AssetManifest manifest = new AssetManifestBuilder()
                .add(AssetId.of("a"), TEXT.id(), "value-a")
                .build();

        DefaultAssetManager manager = new DefaultAssetManager(manifest, new DefaultAssetCache());
        manager.registerLoader(new AssetLoader<String>() {
            @Override
            public AssetType<String> type() {
                return TEXT;
            }

            @Override
            public String load(AssetId id, org.dynamisengine.content.api.manifest.ManifestEntry entry,
                               org.dynamisengine.content.api.loader.AssetResolver resolver) {
                loadCounter.incrementAndGet();
                return entry.uri();
            }
        });

        assertEquals("value-a", manager.get(AssetId.of("a"), TEXT));
        assertEquals("value-a", manager.get(AssetId.of("a"), TEXT));
        assertEquals(1, loadCounter.get());
    }

    @Test
    void dependencyResolutionWorks() {
        AssetId assetA = AssetId.of("a");
        AssetId assetB = AssetId.of("b");

        AssetManifest manifest = new AssetManifestBuilder()
                .add(assetB, TEXT.id(), "uri-b")
                .add(assetA, TEXT.id(), "uri-a", List.of(assetB))
                .build();

        DefaultAssetManager manager = new DefaultAssetManager(manifest, new DefaultAssetCache());
        manager.registerLoader(new AssetLoader<String>() {
            @Override
            public AssetType<String> type() {
                return TEXT;
            }

            @Override
            public String load(AssetId id, org.dynamisengine.content.api.manifest.ManifestEntry entry,
                               org.dynamisengine.content.api.loader.AssetResolver resolver) {
                if (id.equals(assetA)) {
                    String b = resolver.resolve(assetB, TEXT);
                    return "A+" + b;
                }
                return entry.uri();
            }
        });

        assertEquals("A+uri-b", manager.get(assetA, TEXT));
    }

    @Test
    void missingLoaderThrows() {
        AssetManifest manifest = new AssetManifestBuilder()
                .add(AssetId.of("x"), "missing.type", "uri-x")
                .build();

        DefaultAssetManager manager = new DefaultAssetManager(manifest, new DefaultAssetCache());

        DynamisException ex = assertThrows(DynamisException.class,
                () -> manager.get(AssetId.of("x"), AssetType.of("missing.type", String.class)));

        assertEquals("No loader registered for typeId: missing.type (asset x)", ex.getMessage());
    }

    @Test
    void missingManifestEntryThrows() {
        AssetManifest manifest = new AssetManifestBuilder().build();
        DefaultAssetManager manager = new DefaultAssetManager(manifest, new DefaultAssetCache());

        DynamisException ex = assertThrows(DynamisException.class,
                () -> manager.get(AssetId.of("missing"), TEXT));

        assertEquals("No manifest entry for asset: missing", ex.getMessage());
    }
}
