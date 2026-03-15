package org.dynamisengine.content.core;

import org.dynamisengine.core.exception.DynamisException;
import org.dynamisengine.content.api.AssetManager;
import org.dynamisengine.content.api.cache.AssetCache;
import org.dynamisengine.content.api.id.AssetId;
import org.dynamisengine.content.api.id.AssetType;
import org.dynamisengine.content.api.loader.AssetLoader;
import org.dynamisengine.content.api.loader.AssetResolver;
import org.dynamisengine.content.api.manifest.AssetManifest;
import org.dynamisengine.content.api.manifest.ManifestEntry;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class DefaultAssetManager implements AssetManager, AssetResolver {

    private final AssetManifest manifest;
    private final AssetCache cache;
    private final Map<String, AssetLoader<?>> loaders = new ConcurrentHashMap<>();

    public DefaultAssetManager(AssetManifest manifest, AssetCache cache) {
        this.manifest = Objects.requireNonNull(manifest, "manifest");
        this.cache = Objects.requireNonNull(cache, "cache");
    }

    public void registerLoader(AssetLoader<?> loader) {
        Objects.requireNonNull(loader, "loader");
        loaders.put(loader.type().id(), loader);
    }

    @Override
    public <T> T get(AssetId id, AssetType<T> type) {
        return tryGet(id, type).orElseThrow(() ->
                new DynamisException("Asset not found or failed to load: " + id + " type=" + type.id()));
    }

    @Override
    public <T> Optional<T> tryGet(AssetId id, AssetType<T> type) {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(type, "type");

        Optional<T> cached = cache.get(id, type);
        if (cached.isPresent()) {
            return cached;
        }

        ManifestEntry entry = manifest.find(id).orElseThrow(() ->
                new DynamisException("No manifest entry for asset: " + id));

        AssetLoader<?> rawLoader = loaders.get(entry.typeId());
        if (rawLoader == null) {
            throw new DynamisException("No loader registered for typeId: " + entry.typeId() + " (asset " + id + ")");
        }

        if (!rawLoader.type().id().equals(type.id())) {
            throw new DynamisException("Requested typeId " + type.id() + " but manifest/type loader is " +
                    rawLoader.type().id() + " for asset " + id);
        }

        @SuppressWarnings("unchecked")
        AssetLoader<T> loader = (AssetLoader<T>) rawLoader;

        T asset = loader.load(id, entry, this);
        if (asset == null) {
            throw new DynamisException("Loader returned null for asset: " + id);
        }

        cache.put(id, type, asset);
        return Optional.of(asset);
    }

    @Override
    public void invalidate(AssetId id) {
        cache.invalidate(id);
    }

    @Override
    public void clear() {
        cache.clear();
    }

    @Override
    public <T> T resolve(AssetId id, AssetType<T> type) {
        return get(id, type);
    }
}
