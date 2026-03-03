package org.dynamiscontent.api.cache;

import org.dynamiscontent.api.id.AssetId;
import org.dynamiscontent.api.id.AssetType;

import java.util.Optional;

public interface AssetCache {

    <T> Optional<T> get(AssetId id, AssetType<T> type);

    <T> void put(AssetId id, AssetType<T> type, T asset);

    void invalidate(AssetId id);

    void clear();
}
