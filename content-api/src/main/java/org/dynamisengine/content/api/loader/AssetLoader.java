package org.dynamisengine.content.api.loader;

import org.dynamisengine.content.api.id.AssetId;
import org.dynamisengine.content.api.id.AssetType;
import org.dynamisengine.content.api.manifest.ManifestEntry;

public interface AssetLoader<T> {

    AssetType<T> type();

    T load(AssetId id, ManifestEntry entry, AssetResolver resolver);
}
