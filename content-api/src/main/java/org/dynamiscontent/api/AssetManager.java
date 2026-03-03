package org.dynamiscontent.api;

import org.dynamiscontent.api.id.AssetId;
import org.dynamiscontent.api.id.AssetType;

import java.util.Optional;

public interface AssetManager {

    <T> T get(AssetId id, AssetType<T> type);

    <T> Optional<T> tryGet(AssetId id, AssetType<T> type);

    void invalidate(AssetId id);

    void clear();
}
