/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.bytes;

import jdk.incubator.foreign.MemorySegment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.nio.ByteBuffer;

/**
 * A read-write (mutable) byte sequence based on a {@link ByteBuffer}
 */
public class MutableByteBufferBytes extends ByteBufferBytes implements MutableBytes {

    /**
     * Build a read-write (mutable) byte sequence from a fresh {@link ByteBuffer}
     *
     * @param direct           true for a direct ByteBuffer
     * @param capacity         total capacity of the ByteBuffer
     */
    public MutableByteBufferBytes(boolean direct, @Range(from = 1, to = Integer.MAX_VALUE) int capacity) {
        this(direct, false, capacity);
    }

    /**
     * Build a read-write (mutable) byte sequence from a fresh {@link ByteBuffer}
     *
     * @param direct           true for a direct ByteBuffer
     * @param useMemorySegment true to use a MemorySegment
     * @param capacity         total capacity of the ByteBuffer
     */
    public MutableByteBufferBytes(boolean direct, boolean useMemorySegment, @Range(from = 1, to = Integer.MAX_VALUE) int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be > 0");
        }
        if (direct) {
            if (useMemorySegment) {
                this.segment = MemorySegment.allocateNative(capacity);
                this.bb = this.segment.asByteBuffer();
            } else {
                this.bb = ByteBuffer.allocateDirect(capacity);
            }
        } else {
            this.bb = ByteBuffer.allocate(capacity);
        }
        this.capacity = capacity;
        this.isReadOnly = false;
    }

    /**
     * Build a read-write (mutable) byte sequence from an existing {@link MemorySegment}
     *
     * @param segment   the memory segment
     */
    private MutableByteBufferBytes(@NotNull MemorySegment segment) {
        super(segment);
        this.isReadOnly = false;
    }

    @Override
    public void writeByteAt(@Range(from = 0, to = Integer.MAX_VALUE - 1) int index, byte value) {
        checkNotReadOnly();
        this.bb.put(index, value);
    }

    @Override
    public void writeIntAt(@Range(from = 0, to = Integer.MAX_VALUE - 1) int index, int value) {
        checkNotReadOnly();
        this.bb.putInt(index, value);
    }

    @Override
    public @NotNull Bytes asReadOnly() {
        this.isReadOnly = true;
        return this;
    }

    @Override
    public @NotNull MutableBytes acquire() {
        if (this.segment != null) {
            return new MutableByteBufferBytes(this.segment.acquire());
        }
        return this;
    }

    private void checkNotReadOnly() {
        if (this.isReadOnly) {
            throw new UnsupportedOperationException("Cannot write in a readOnly Bytes");
        }
    }
}
