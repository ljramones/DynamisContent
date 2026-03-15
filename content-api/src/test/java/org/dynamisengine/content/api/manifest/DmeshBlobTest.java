package org.dynamisengine.content.api.manifest;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DmeshBlobTest {

    @Test
    void rejectsInvalidVersion() {
        assertThrows(IllegalArgumentException.class, () -> new DmeshBlob(0, new byte[]{1}, 1L));
    }

    @Test
    void rejectsNullPayload() {
        assertThrows(NullPointerException.class, () -> new DmeshBlob(1, null, 1L));
    }

    @Test
    void defensivelyCopiesPayload() {
        byte[] payload = new byte[]{1, 2, 3};
        DmeshBlob blob = new DmeshBlob(1, payload, 42L);

        payload[0] = 9;

        assertArrayEquals(new byte[]{1, 2, 3}, blob.payload());
    }
}
