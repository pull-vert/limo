/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.bytes;

import io.limo.utils.MemorySegmentOps;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.nio.ByteBuffer;
import java.util.Objects;

/**
 *  A read-only (immutable) byte sequence based on a {@link MemorySegment} from a {@code byte[]} or a {@code ByteBuffer}
 */
public final class MemorySegmentBytes implements Bytes {

    private final @NotNull MemorySegment segment;
    private final @NotNull MemoryAddress base;

    /**
     * Build a read-only (immutable) byte sequence from a read-only {@link MemorySegment} built from an existing {@code byte[]}
     *
     * @param byteArray the byte array
     */
    public MemorySegmentBytes(byte @NotNull [] byteArray) {
        this.segment = MemorySegment.ofArray(Objects.requireNonNull(byteArray)).asReadOnly();
        this.base = segment.baseAddress();
    }

    /**
     * Build a read-only (immutable) byte sequence from a read-only {@link MemorySegment} built from an existing {@link ByteBuffer}
     *
     * @param bb the ByteBuffer
     */
    public MemorySegmentBytes(@NotNull ByteBuffer bb) {
        this.segment = MemorySegment.ofByteBuffer(Objects.requireNonNull(bb)).asReadOnly();
        this.base = segment.baseAddress();
    }

    @Override
    public byte readByteAt(@Range(from = 0, to = Integer.MAX_VALUE - 1) int index) {
        return MemorySegmentOps.readByte(this.base.addOffset(index));
    }

    @Override
    public int readIntAt(@Range(from = 0, to = Integer.MAX_VALUE - 1) int index, boolean isBigEndian) {
        return MemorySegmentOps.readInt(this.base.addOffset(index), isBigEndian);
    }

    @Override
    public @Range(from = 1, to = Integer.MAX_VALUE) int getByteSize() {
        return (int) this.segment.byteSize();
    }

    @Override
    public byte[] toByteArray() {
        return this.segment.toByteArray();
    }

    @Override
    public @NotNull ByteBuffer toByteBuffer() {
        return this.segment.asByteBuffer();
    }

    /**
     * Closes associated {@link #segment}
     */
    @Override
    public void close() {
        this.segment.close();
    }
}
