package org.dynamisengine.content.core.cache;

import org.dynamisengine.content.api.cache.AssetCache;
import org.dynamisengine.content.api.id.AssetId;
import org.dynamisengine.content.api.id.AssetType;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public final class DefaultAssetCache implements AssetCache {

    private record CacheKey(AssetId id, String typeId) {
    }

    private final ConcurrentHashMap<CacheKey, Object> map = new ConcurrentHashMap<>();
    private final AtomicLong hitCount = new AtomicLong();
    private final AtomicLong missCount = new AtomicLong();

    @Override
    public <T> Optional<T> get(AssetId id, AssetType<T> type) {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(type, "type");
        Object v = map.get(new CacheKey(id, type.id()));
        if (v == null) {
            missCount.incrementAndGet();
            return Optional.empty();
        }
        hitCount.incrementAndGet();
        return Optional.of(type.type().cast(v));
    }

    @Override
    public <T> void put(AssetId id, AssetType<T> type, T asset) {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(asset, "asset");
        map.put(new CacheKey(id, type.id()), asset);
    }

    @Override
    public void invalidate(AssetId id) {
        Objects.requireNonNull(id, "id");
        map.keySet().removeIf(k -> k.id().equals(id));
    }

    @Override
    public void clear() {
        map.clear();
    }

    /** Number of cached assets. */
    public int size() { return map.size(); }

    /** Cumulative cache hits since creation. */
    public long cacheHits() { return hitCount.get(); }

    /** Cumulative cache misses since creation. */
    public long cacheMisses() { return missCount.get(); }
}
