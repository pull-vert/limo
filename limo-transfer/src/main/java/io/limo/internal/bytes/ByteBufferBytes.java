/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.bytes;

import jdk.incubator.foreign.MemorySegment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;
import java.util.Optional;

/**
 * A read-only (immutable) byte sequence based on a direct {@link ByteBuffer} associated to a native {@link MemorySegment}
 */
public class ByteBufferBytes implements Bytes {

    @NotNull MemorySegment segment;

    @NotNull ByteBuffer bb;

    @Range(from = 1, to = Integer.MAX_VALUE) int capacity;

    boolean isReadOnly = true;

    /**
     * Build a read-only (immutable) byte sequence from an existing direct {@link ByteBuffer}
     *
     * @param bb the ByteBuffer
     */
    public ByteBufferBytes(@NotNull ByteBuffer bb) {
        this(MemorySegment.ofByteBuffer(
            Optional.of(Objects.requireNonNull(bb))
                .filter(ByteBuffer::isDirect)
                .orElseThrow(() -> new IllegalArgumentException("ByteBuffer must be direct")))
        );
    }

    /**
     * Build a read-only (immutable) byte sequence from a direct {@link ByteBuffer} filled with a byte array
     *
     * @param byteArray the byte array
     */
    public ByteBufferBytes(byte @NotNull [] byteArray) {
        this.segment = MemorySegment.allocateNative(Objects.requireNonNull(byteArray).length);
        this.bb = this.segment.asByteBuffer()
            .put(byteArray);
        this.capacity = byteArray.length;
    }

    /**
     * Build a read-only (immutable) byte sequence from an existing {@link MemorySegment}
     *
     * @param segment the memory segment
     */
    ByteBufferBytes(@NotNull MemorySegment segment) {
        this(Objects.requireNonNull(segment), segment.asByteBuffer());
    }

    /**
     * Build a read-only (immutable) byte sequence from an existing {@link MemorySegment} and associated {@link ByteBuffer}
     *
     * @param segment the memory segment
     * @param bb      the ByteBuffer
     */
    public ByteBufferBytes(@NotNull MemorySegment segment, @NotNull ByteBuffer bb) {
        this.segment = Objects.requireNonNull(segment);
        this.bb = Objects.requireNonNull(bb);
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
        return new ByteBufferBytes(this.segment.acquire());
    }

    @Override
    public boolean isReadOnly() {
        return this.isReadOnly;
    }

    @Override
    public byte @NotNull [] toByteArray() {
        return this.segment.toByteArray();
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
        this.segment.close();
    }
}
