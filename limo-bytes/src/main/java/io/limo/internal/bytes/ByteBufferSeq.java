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
public final class ByteBufferSeq implements ByteSequence {

    private static final VarHandle INT_HANDLE_BIG_ENDIAN = MethodHandles.byteBufferViewVarHandle(int[].class, ByteOrder.BIG_ENDIAN);
    private static final VarHandle LONG_HANDLE_BIG_ENDIAN = MethodHandles.byteBufferViewVarHandle(long[].class, ByteOrder.BIG_ENDIAN);

    private static final VarHandle INT_HANDLE_LITTLE_ENDIAN = MethodHandles.byteBufferViewVarHandle(int[].class, ByteOrder.LITTLE_ENDIAN);
    private static final VarHandle LONG_HANDLE_LITTLE_ENDIAN = MethodHandles.byteBufferViewVarHandle(long[].class, ByteOrder.LITTLE_ENDIAN);

    @NotNull
    private ByteBuffer bb;

    /**
     * Build a byte sequence from a {@link ByteBuffer}
     *
     * @param bb the ByteBuffer
     */
    public ByteBufferSeq(@NotNull ByteBuffer bb) {
        this.bb = Objects.requireNonNull(bb);
    }

    @Override
    public byte readByteAt(@Range(from = 0, to = Integer.MAX_VALUE) long index) {
        return bb.get((int) index);
    }

    @Override
    public int readIntAt(@Range(from = 0, to = Integer.MAX_VALUE) long index) {
        return bb.getInt((int) index);
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
    }

    /**
     * Close is no op because {@link ByteBuffer} does not provide close
     */
    @Override
    public void close() {
    }
}
