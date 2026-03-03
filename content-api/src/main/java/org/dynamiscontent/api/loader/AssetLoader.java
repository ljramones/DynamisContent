package org.dynamiscontent.api.loader;

import org.dynamiscontent.api.id.AssetId;
import org.dynamiscontent.api.id.AssetType;
import org.dynamiscontent.api.manifest.ManifestEntry;

public interface AssetLoader<T> {

    AssetType<T> type();

    T load(AssetId id, ManifestEntry entry, AssetResolver resolver);
}
