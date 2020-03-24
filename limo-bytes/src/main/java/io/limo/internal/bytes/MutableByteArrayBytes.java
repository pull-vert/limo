/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.bytes;

import jdk.incubator.foreign.MemorySegment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

/**
 * A read-write (mutable) byte sequence based on a {@code byte[]}
 */
public final class MutableByteArrayBytes extends ByteArrayBytes implements MutableBytes {

    /**
     * Build a read-write (mutable) byte sequence from a fresh {@code byte[]}
     *
     * @param capacity         total capacity of the ByteBuffer
     */
    public MutableByteArrayBytes(@Range(from = 1, to = Integer.MAX_VALUE) int capacity) {
        this(false, capacity);
    }

    /**
     * Build a read-write (mutable) byte sequence from a fresh {@code byte[]}
     *
     * @param useMemorySegment true to use a MemorySegment
     * @param capacity         total capacity of the ByteBuffer
     */
    public MutableByteArrayBytes(boolean useMemorySegment, @Range(from = 1, to = Integer.MAX_VALUE) int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be > 0");
        }
        this.byteArray = new byte[capacity];
        if (useMemorySegment) {
            this.segment = MemorySegment.ofArray(this.byteArray);
        }
        this.capacity = capacity;
        this.isReadOnly = false;
    }

    @Override
    public void writeByteAt(@Range(from = 0, to = Integer.MAX_VALUE - 1) int index, byte value) {
        checkNotReadOnly();
        this.byteArray[index] = value;
    }

    @Override
    public void writeIntAt(@Range(from = 0, to = Integer.MAX_VALUE - 1) int index, int value, boolean isBigEndian) {
        checkNotReadOnly();
        if (isBigEndian) {
            INT_HANDLE_BE.set(byteArray, index, value);
        } else {
            INT_HANDLE_LE.set(byteArray, index, value);
        }
    }

    @Override
    public @NotNull Bytes asReadOnly() {
        this.isReadOnly = true;
        return this;
    }

    @Override
    public @NotNull MutableBytes acquire() {
        if (this.segment != null) {
            this.segment = this.segment.acquire();
        }
        return this;
    }

    private void checkNotReadOnly() {
        if (this.isReadOnly) {
            throw new UnsupportedOperationException("Cannot write in a readOnly Bytes");
        }
    }
}
