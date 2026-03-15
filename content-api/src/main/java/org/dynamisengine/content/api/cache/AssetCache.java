package org.dynamisengine.content.api.cache;

import org.dynamisengine.content.api.id.AssetId;
import org.dynamisengine.content.api.id.AssetType;

import java.util.Optional;

public interface AssetCache {

    <T> Optional<T> get(AssetId id, AssetType<T> type);

    <T> void put(AssetId id, AssetType<T> type, T asset);

    void invalidate(AssetId id);

    void clear();
}
