package org.dynamiscontent.api.loader;

import org.dynamiscontent.api.id.AssetId;
import org.dynamiscontent.api.id.AssetType;

public interface AssetResolver {
    <T> T resolve(AssetId id, AssetType<T> type);
}
