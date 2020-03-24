/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.bytes;

import io.limo.internal.BinaryTestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public final class ByteArrayBytesTests implements BytesTests, MutableBytesTests {

    @Test
    @DisplayName("Verify read using Big Endian is working")
    void readBE() {
        readBETest(new ByteArrayBytes(BinaryTestData.BYTES_BIG_ENDIAN));
    }

    @Test
    @DisplayName("Verify read using Little Endian is working")
    void readLE() {
        readLETest(new ByteArrayBytes(BinaryTestData.BYTES_LITTLE_ENDIAN));
    }

    @Test
    @DisplayName("Verify write using Big Endian is working")
    void writeBE() {
        writeBETest(new MutableByteArrayBytes(10));
    }

    @Test
    @DisplayName("Verify write using Little Endian is working")
    void writeLE() {
        writeLETest(new MutableByteArrayBytes(10));
    }
}
