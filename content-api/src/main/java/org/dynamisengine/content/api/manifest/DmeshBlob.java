package org.dynamisengine.content.api.manifest;

import java.util.Objects;

public record DmeshBlob(int version, byte[] payload, long contentHash64) {
    public DmeshBlob {
        if (version <= 0) {
            throw new IllegalArgumentException("version must be > 0");
        }
        Objects.requireNonNull(payload, "payload");
        payload = payload.clone();
    }
}
