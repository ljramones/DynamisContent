package org.dynamiscontent.api.id;

import java.util.Objects;

/**
 * Stable runtime identifier for an asset.
 * Canonical form is a normalized string (e.g. "textures/sky/sunset.hdr").
 */
public record AssetId(String value) {

    public AssetId {
        Objects.requireNonNull(value, "value");
        if (value.isBlank()) {
            throw new IllegalArgumentException("AssetId value must not be blank");
        }
    }

    public static AssetId of(String value) {
        return new AssetId(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
