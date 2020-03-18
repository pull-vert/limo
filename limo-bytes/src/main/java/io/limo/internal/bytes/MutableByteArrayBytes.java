/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.bytes;

import org.jetbrains.annotations.Range;

import java.nio.ByteOrder;

/**
 * A read-write (mutable) byte sequence based on a {@code byte[]}
 */
public final class MutableByteArrayBytes extends ByteArrayBytes implements MutableBytes {

    /**
     * Build a read-write (mutable) byte sequence from a fresh {@code byte[]}
     * <p>The byte order of a newly-created Bytes is always {@link ByteOrder#BIG_ENDIAN BIG_ENDIAN}
     *
     * @param capacity total capacity of the ByteBuffer
     */
    public MutableByteArrayBytes(@Range(from = 1, to = Integer.MAX_VALUE) int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be > 0");
        }
        this.byteArray = new byte[capacity];
        this.capacity = capacity;
        this.isReadOnly = false;
    }

    @Override
    public void writeByteAt(@Range(from = 0, to = Integer.MAX_VALUE - 1) int index, byte value) {
        checkNotReadOnly();
        this.byteArray[index] = value;
    }

    @Override
    public void writeIntAt(@Range(from = 0, to = Integer.MAX_VALUE - 1) int index, int value) {
        checkNotReadOnly();
        this.intHandle.set(byteArray, index, value);
    }

    private void checkNotReadOnly() {
        if (this.isReadOnly) {
            throw new UnsupportedOperationException("Cannot write in a readOnly Bytes");
        }
    }
}
