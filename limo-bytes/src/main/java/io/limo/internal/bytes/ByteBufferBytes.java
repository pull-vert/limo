/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.bytes;

import jdk.incubator.foreign.MemorySegment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;

/**
 * A read-only (immutable) byte sequence based on a {@link ByteBuffer}
 */
public class ByteBufferBytes implements Bytes {

    @NotNull ByteBuffer bb;
    @Range(from = 1, to = Integer.MAX_VALUE) int capacity;
    boolean isReadOnly = true;
    @Nullable MemorySegment segment = null;

    ByteBufferBytes() {
    }

    /**
     * Build a read-only (immutable) byte sequence from an existing {@link ByteBuffer}
     *
     * @param bb the ByteBuffer
     */
    public ByteBufferBytes(@NotNull ByteBuffer bb) {
        this.bb = Objects.requireNonNull(bb);
        this.capacity = bb.capacity();
    }

    /**
     * Build a read-only (immutable) byte sequence from a {@link ByteBuffer} built from a byte array
     *
     * @param byteArray the byte array
     */
    public ByteBufferBytes(byte @NotNull [] byteArray) {
        this.bb = ByteBuffer.wrap(Objects.requireNonNull(byteArray));
        this.capacity = byteArray.length;
    }

    /**
     * Build a read-only (immutable) byte sequence from an existing {@link MemorySegment}
     *
     * @param segment   the memory segment
     */
    ByteBufferBytes(@NotNull MemorySegment segment) {
        this.segment = Objects.requireNonNull(segment);
        this.bb = this.segment.asByteBuffer();
        this.capacity = bb.capacity();
    }

    @Override
    public byte readByteAt(@Range(from = 0, to = Integer.MAX_VALUE - 1) int index) {
        return this.bb.get(index);
    }

    @Override
    public int readIntAt(@Range(from = 0, to = Integer.MAX_VALUE - 1) int index) {
        return this.bb.getInt(index);
    }

    @Override
    public @Range(from = 1, to = Integer.MAX_VALUE) int getByteSize() {
        return this.capacity;
    }

    @Override
    public void setByteOrder(@NotNull ByteOrder byteOrder) {
        this.bb.order(byteOrder);
    }

    @Override
    public @NotNull Bytes acquire() {
        if (this.segment != null) {
            return new ByteBufferBytes(this.segment.acquire());
        }
        return this;
    }

    @Override
    public boolean isReadOnly() {
        return this.isReadOnly;
    }

    @Override
    public byte @NotNull [] toByteArray() {
        // fast-path if ByteBuffer is backed by an accessible byte array
        if (this.bb.hasArray()) {
            return this.bb.array();
        }

        if (this.segment != null) {
            return this.segment.toByteArray();
        }

        final var limit = this.bb.limit();
        final var byteArray = new byte[limit];
        this.bb.get(byteArray, 0, limit);
        return byteArray;
    }

    @Override
    public @NotNull ByteBuffer toByteBuffer() {
        return this.bb;
    }

    /**
     * Closes associated {@link #segment} if exists
     */
    @Override
    public void close() {
        if (this.segment != null) {
            this.segment.close();
        }
    }
}
