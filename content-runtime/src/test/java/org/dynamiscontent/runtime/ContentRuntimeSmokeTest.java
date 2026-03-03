package org.dynamiscontent.runtime;

import org.dynamiscontent.api.id.AssetId;
import org.dynamiscontent.api.id.AssetType;
import org.dynamiscontent.api.loader.AssetLoader;
import org.dynamiscontent.api.loader.AssetResolver;
import org.dynamiscontent.api.manifest.ManifestEntry;
import org.dynamiscontent.core.manifest.AssetManifestBuilder;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class ContentRuntimeSmokeTest {

    private static final AssetType<String> TEXT = AssetType.of("demo.text", String.class);

    @Test
    void runtime_builder_wires_manifest_cache_and_loader() {
        AssetId id = AssetId.of("demo/hello.txt");

        var manifest = new AssetManifestBuilder()
                .add(id, TEXT.id(), "Hello, Content!")
                .build();

        AtomicInteger loads = new AtomicInteger(0);

        AssetLoader<String> loader = new AssetLoader<>() {
            @Override
            public AssetType<String> type() {
                return TEXT;
            }

            @Override
            public String load(AssetId assetId, ManifestEntry entry, AssetResolver resolver) {
                loads.incrementAndGet();
                return entry.uri();
            }
        };

        ContentRuntime runtime = ContentRuntime.builder()
                .manifest(manifest)
                .loader(loader)
                .build();

        String a = runtime.assets().get(id, TEXT);
        String b = runtime.assets().get(id, TEXT);

        assertEquals("Hello, Content!", a);
        assertEquals("Hello, Content!", b);
        assertEquals(1, loads.get(), "Second get() should be a cache hit");
    }
}
