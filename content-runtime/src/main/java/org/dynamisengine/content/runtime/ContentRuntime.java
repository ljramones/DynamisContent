package org.dynamisengine.content.runtime;

import org.dynamisengine.core.exception.DynamisException;
import org.dynamisengine.content.api.AssetManager;
import org.dynamisengine.content.api.cache.AssetCache;
import org.dynamisengine.content.api.loader.AssetLoader;
import org.dynamisengine.content.api.manifest.AssetManifest;
import org.dynamisengine.content.core.DefaultAssetManager;
import org.dynamisengine.content.core.cache.DefaultAssetCache;
import org.dynamisengine.content.core.loader.DmeshBlobLoader;
import org.dynamisengine.content.core.manifest.JsonAssetManifestReader;

import java.nio.file.Path;
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
        private Path baseDir;
        private final List<AssetLoader<?>> loaders = new ArrayList<>();

        public Builder manifest(AssetManifest manifest) {
            this.manifest = Objects.requireNonNull(manifest, "manifest");
            return this;
        }

        public Builder manifest(Path jsonFile) {
            this.manifest = new JsonAssetManifestReader().read(Objects.requireNonNull(jsonFile, "jsonFile"));
            return this;
        }

        public Builder baseDir(Path baseDir) {
            this.baseDir = Objects.requireNonNull(baseDir, "baseDir");
            return this;
        }

        public Builder registerDefaultLoaders(Path baseDir) {
            this.loaders.add(new DmeshBlobLoader(Objects.requireNonNull(baseDir, "baseDir")));
            return this;
        }

        public Builder registerDefaultLoaders() {
            Path resolvedBaseDir = baseDir != null ? baseDir : Path.of(".");
            return registerDefaultLoaders(resolvedBaseDir);
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
