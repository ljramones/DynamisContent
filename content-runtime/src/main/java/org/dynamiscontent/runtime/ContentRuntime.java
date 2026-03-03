package org.dynamiscontent.runtime;

import org.dynamis.core.exception.DynamisException;
import org.dynamiscontent.api.AssetManager;
import org.dynamiscontent.api.cache.AssetCache;
import org.dynamiscontent.api.loader.AssetLoader;
import org.dynamiscontent.api.manifest.AssetManifest;
import org.dynamiscontent.core.DefaultAssetManager;
import org.dynamiscontent.core.cache.DefaultAssetCache;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Runtime wiring facade for DynamisContent.
 *
 * Provides a small builder to assemble an AssetManager from:
 *  - AssetManifest
 *  - AssetCache (default is DefaultAssetCache)
 *  - AssetLoaders registry
 */
public final class ContentRuntime {

    private final AssetManifest manifest;
    private final AssetCache cache;
    private final DefaultAssetManager assetManager;

    private ContentRuntime(Builder b) {
        this.manifest = b.manifest;
        this.cache = b.cache != null ? b.cache : new DefaultAssetCache();
        this.assetManager = new DefaultAssetManager(manifest, cache);
        for (AssetLoader<?> loader : b.loaders) {
            assetManager.registerLoader(loader);
        }
    }

    public AssetManifest manifest() {
        return manifest;
    }

    public AssetCache cache() {
        return cache;
    }

    public AssetManager assets() {
        return assetManager;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private AssetManifest manifest;
        private AssetCache cache;
        private final List<AssetLoader<?>> loaders = new ArrayList<>();

        public Builder manifest(AssetManifest manifest) {
            this.manifest = Objects.requireNonNull(manifest, "manifest");
            return this;
        }

        public Builder cache(AssetCache cache) {
            this.cache = Objects.requireNonNull(cache, "cache");
            return this;
        }

        public Builder loader(AssetLoader<?> loader) {
            this.loaders.add(Objects.requireNonNull(loader, "loader"));
            return this;
        }

        public ContentRuntime build() {
            if (manifest == null) {
                throw new DynamisException("ContentRuntime requires a manifest");
            }
            return new ContentRuntime(this);
        }
    }
}
