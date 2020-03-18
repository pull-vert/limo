/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.bytes;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public final class ByteArrayBytesTests extends BytesTests {

    @Override
    protected Bytes instanciateBytes(byte @NotNull [] byteArray) {
        return new ByteArrayBytes(byteArray);
    }

    @Test
    @DisplayName("Verify write using native Big Endian is working")
    void writeBE() {
        testWriteBE(new ByteArrayBytes(10));
    }

    @Test
    @DisplayName("Verify write using Little Endian is working")
    void writeLE() {
        testWriteLE(new ByteArrayBytes(10));
    }
}
