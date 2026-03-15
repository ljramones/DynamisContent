package org.dynamisengine.content.api.loader;

import org.dynamisengine.content.api.id.AssetId;
import org.dynamisengine.content.api.id.AssetType;

public interface AssetResolver {
    <T> T resolve(AssetId id, AssetType<T> type);
}
