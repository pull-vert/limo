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
import java.nio.ByteOrder;
import java.util.Objects;

/**
 *  A read-only byte sequence based on a {@link MemorySegment} from a {@code byte[]} or a {@code ByteBuffer}
 */
public final class MemorySegmentBytes implements Bytes {

    private final @NotNull MemorySegment segment;

    private final @NotNull MemoryAddress base;

    private boolean isBigEndian = true;

    /**
     * Build a byte sequence from a read-only {@link MemorySegment} built from a byte array
     * <p> The byte order of a newly-created ByteSequence is always {@link ByteOrder#BIG_ENDIAN BIG_ENDIAN}. </p>
     *
     * @param byteArray the byte array
     */
    public MemorySegmentBytes(byte @NotNull [] byteArray) {
        this.segment = MemorySegment.ofArray(Objects.requireNonNull(byteArray)).asReadOnly();
        this.base = segment.baseAddress();
    }

    /**
     * Build a byte sequence from a read-only {@link MemorySegment} built from a {@link ByteBuffer}
     * <p> The byte order of a newly-created ByteSequence is always {@link ByteOrder#BIG_ENDIAN BIG_ENDIAN}. </p>
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
    public int readIntAt(@Range(from = 0, to = Integer.MAX_VALUE - 1) int index) {
        return MemorySegmentOps.readInt(this.base.addOffset(index), isBigEndian);
    }

    @Override
    public void writeByteAt(@Range(from = 0, to = Integer.MAX_VALUE - 1) int index, byte value) {
        throwUnsupportedWriteException();
    }

    @Override
    public void writeIntAt(@Range(from = 0, to = Integer.MAX_VALUE - 1) int index, int value) {
        throwUnsupportedWriteException();
    }

    @Override
    public @Range(from = 1, to = Integer.MAX_VALUE) int getByteSize() {
        return (int) this.segment.byteSize();
    }

    @Override
    public final void setByteOrder(@NotNull ByteOrder byteOrder) {
        this.isBigEndian = (byteOrder == ByteOrder.BIG_ENDIAN);
    }

    /**
     * Closes associated {@link #segment}
     */
    @Override
    public void close() {
        this.segment.close();
    }

    private Exception throwUnsupportedWriteException() {
        throw new UnsupportedOperationException("No write operation on MemorySegmentBytes !");
    }
}
