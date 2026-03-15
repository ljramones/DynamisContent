package org.dynamisengine.content.api.id;

import org.dynamisengine.content.api.manifest.DmeshBlob;

public final class AssetTypes {
    private AssetTypes() {
    }

    public static final AssetType<DmeshBlob> DMESH_BLOB =
            AssetType.of("mesh.packed.dmesh.v0", DmeshBlob.class);
}
