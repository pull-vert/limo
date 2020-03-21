/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.bytes;

import org.jetbrains.annotations.NotNull;

public final class ByteBufferBytesTests implements BytesTests, MutableBytesTests {

    @Override
    public Bytes instanciateBytes(byte @NotNull [] byteArray) {
        return new ByteBufferBytes(byteArray);
    }

    @Override
    public MutableBytes instanciateMutableBytes() {
        return new MutableByteBufferBytes(true, 10);
    }
}
