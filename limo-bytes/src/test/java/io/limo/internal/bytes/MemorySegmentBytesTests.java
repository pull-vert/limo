/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.bytes;

public final class MemorySegmentBytesTests extends BytesTests {

    @Override
    protected Bytes instanciateBytes(byte[] byteArray) {
        return new MemorySegmentBytes(byteArray);
    }
}
