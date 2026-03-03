package org.dynamiscontent.api.id;

import java.util.Objects;

/**
 * Typed asset identity. Equality is based on id only.
 */
public final class AssetType<T> {

    private final String id;
    private final Class<T> type;

    private AssetType(String id, Class<T> type) {
        this.id = id;
        this.type = type;
    }

    public static <T> AssetType<T> of(String id, Class<T> type) {
        Objects.requireNonNull(type, "type");
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("AssetType id must not be null/blank");
        }
        return new AssetType<>(id, type);
    }

    public String id() {
        return id;
    }

    public Class<T> type() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AssetType<?> other)) {
            return false;
        }
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "AssetType[" + id + ", " + type.getSimpleName() + "]";
    }
}
