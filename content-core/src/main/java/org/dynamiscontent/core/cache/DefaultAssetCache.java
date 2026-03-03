package org.dynamiscontent.core.cache;

import org.dynamiscontent.api.cache.AssetCache;
import org.dynamiscontent.api.id.AssetId;
import org.dynamiscontent.api.id.AssetType;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class DefaultAssetCache implements AssetCache {

    private record CacheKey(AssetId id, String typeId) {
    }

    private final ConcurrentHashMap<CacheKey, Object> map = new ConcurrentHashMap<>();

    @Override
    public <T> Optional<T> get(AssetId id, AssetType<T> type) {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(type, "type");
        Object v = map.get(new CacheKey(id, type.id()));
        if (v == null) {
            return Optional.empty();
        }
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
}
