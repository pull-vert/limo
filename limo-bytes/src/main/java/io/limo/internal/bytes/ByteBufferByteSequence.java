/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.bytes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;

/**
 *  A byte sequence based on a {@code ByteBuffer}
 */
public final class ByteBufferByteSequence implements ByteSequence {

    private static final VarHandle INT_HANDLE_BIG_ENDIAN = MethodHandles.byteBufferViewVarHandle(int[].class, ByteOrder.BIG_ENDIAN);
    private static final VarHandle LONG_HANDLE_BIG_ENDIAN = MethodHandles.byteBufferViewVarHandle(long[].class, ByteOrder.BIG_ENDIAN);

    private static final VarHandle INT_HANDLE_LITTLE_ENDIAN = MethodHandles.byteBufferViewVarHandle(int[].class, ByteOrder.LITTLE_ENDIAN);
    private static final VarHandle LONG_HANDLE_LITTLE_ENDIAN = MethodHandles.byteBufferViewVarHandle(long[].class, ByteOrder.LITTLE_ENDIAN);

    @NotNull
    private final ByteBuffer bb;

    private VarHandle intHandle;
    private VarHandle longHandle;

    /**
     * Build a byte sequence from a {@link ByteBuffer}
     * <p> The byte order of a newly-created ByteSequence is always {@link ByteOrder#BIG_ENDIAN BIG_ENDIAN}. </p>
     *
     * @param bb the ByteBuffer
     */
    public ByteBufferByteSequence(@NotNull ByteBuffer bb) {
        this.bb = Objects.requireNonNull(bb);
        intHandle = INT_HANDLE_BIG_ENDIAN;
        longHandle = LONG_HANDLE_BIG_ENDIAN;
    }

    /**
     * Build a byte sequence from a fresh {@link ByteBuffer}
     * <p> The byte order of a newly-created ByteSequence is always {@link ByteOrder#BIG_ENDIAN BIG_ENDIAN}. </p>
     *
     * @param direct true for a direct ByteBuffer
     * @param capacity total capacity of the ByteBuffer
     */
    public ByteBufferByteSequence(boolean direct, @Range(from = 1, to = Integer.MAX_VALUE) int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be > 0");
        }
        if (direct) {
            this.bb = ByteBuffer.allocateDirect(capacity);
        } else {
            this.bb = ByteBuffer.allocate(capacity);
        }
        intHandle = INT_HANDLE_BIG_ENDIAN;
        longHandle = LONG_HANDLE_BIG_ENDIAN;
    }

    @Override
    public byte readByteAt(@Range(from = 0, to = Integer.MAX_VALUE) long index) {
        return bb.get((int) index);
    }

    @Override
    public int readIntAt(@Range(from = 0, to = Integer.MAX_VALUE) long index) {
        return (int) intHandle.get(bb, (int) index);
    }

    @Override
    public void writeByteAt(@Range(from = 0, to = Integer.MAX_VALUE) long index, byte value) {
        bb.put((int) index, value);
    }

    @Override
    public void writeIntAt(@Range(from = 0, to = Integer.MAX_VALUE) long index, int value) {
        bb.putInt((int) index, value);
    }

    @Override
    @Range(from = 0, to = Integer.MAX_VALUE)
    public long getCapacity() {
        return bb.capacity();
    }

    @Override
    public void setByteOrder(@NotNull ByteOrder byteOrder) {
        bb.order(byteOrder);
        if (byteOrder == ByteOrder.BIG_ENDIAN) {
            intHandle = INT_HANDLE_BIG_ENDIAN;
            longHandle = LONG_HANDLE_BIG_ENDIAN;
        } else {
            intHandle = INT_HANDLE_LITTLE_ENDIAN;
            longHandle = LONG_HANDLE_LITTLE_ENDIAN;
        }
    }

    /**
     * Close is no op because {@link ByteBuffer} does not provide close
     */
    @Override
    public void close() {
    }
}
