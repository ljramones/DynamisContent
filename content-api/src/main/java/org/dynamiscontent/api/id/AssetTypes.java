package org.dynamiscontent.api.id;

import org.dynamiscontent.api.manifest.DmeshBlob;

public final class AssetTypes {
    private AssetTypes() {
    }

    public static final AssetType<DmeshBlob> DMESH_BLOB =
            AssetType.of("mesh.packed.dmesh.v0", DmeshBlob.class);
}
