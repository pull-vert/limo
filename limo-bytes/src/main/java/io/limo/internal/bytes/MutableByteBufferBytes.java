/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.bytes;

import org.jetbrains.annotations.Range;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * A read-write (mutable) byte sequence based on a {@link ByteBuffer}
 */
public class MutableByteBufferBytes extends ByteBufferBytes implements MutableBytes {

    /**
     * Build read-write (mutable) a byte sequence from a fresh {@link ByteBuffer}
     * <p>The byte order of a newly-created Bytes is always {@link ByteOrder#BIG_ENDIAN BIG_ENDIAN}
     *
     * @param direct   true for a direct ByteBuffer
     * @param capacity total capacity of the ByteBuffer
     */
    public MutableByteBufferBytes(boolean direct, @Range(from = 1, to = Integer.MAX_VALUE) int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be > 0");
        }
        if (direct) {
            this.bb = ByteBuffer.allocateDirect(capacity);
        } else {
            this.bb = ByteBuffer.allocate(capacity);
        }
        this.capacity = capacity;
    }

    @Override
    public void writeByteAt(@Range(from = 0, to = Integer.MAX_VALUE - 1) int index, byte value) {
        this.bb.put(index, value);
    }

    @Override
    public void writeIntAt(@Range(from = 0, to = Integer.MAX_VALUE - 1) int index, int value) {
        this.bb.putInt(index, value);
    }
}
