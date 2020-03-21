/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.bytes;

import org.jetbrains.annotations.NotNull;

public final class ByteArrayBytesTests implements BytesTests, MutableBytesTests {

    @Override
    public Bytes instanciateBytes(byte @NotNull [] byteArray) {
        return new ByteArrayBytes(byteArray);
    }

    @Override
    public MutableBytes instanciateMutableBytes() {
        return new MutableByteArrayBytes(10);
    }
}
