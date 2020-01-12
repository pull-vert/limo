/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.bytes.memory;

import io.limo.common.NotNull;

import java.nio.ByteBuffer;

/**
 * A memory chunk based on a ByteBuffer
 */
public final class ByteBufferMemory implements Memory {

    @NotNull
    ByteBuffer bb;

    /**
     * Build a memory chunk from a {@link ByteBuffer}
     * @param bb the ByteBuffer
     */
    public ByteBufferMemory(@NotNull ByteBuffer bb) {
        this.bb = bb;
    }

    @Override
    public byte readByteAt(long index) {
        return bb.get((int) index);
    }

    @Override
    public int readIntAt(long index) {
        return bb.getInt((int) index);
    }

    @Override
    public void writeIntAt(long index, int value) {
        bb.putInt((int) index, value);
    }

    /**
     * Close is no op because {@link ByteBuffer} does not provide close
     */
    @Override
    public void close() { }
}
