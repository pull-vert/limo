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
 *  A byte sequence based on a {@link ByteBuffer}
 */
public final class ByteBufferBytes implements Bytes {

    private static final VarHandle INT_HANDLE_BE = MethodHandles.byteBufferViewVarHandle(int[].class, ByteOrder.BIG_ENDIAN);
    private static final VarHandle LONG_HANDLE_BE = MethodHandles.byteBufferViewVarHandle(long[].class, ByteOrder.BIG_ENDIAN);

    private static final VarHandle INT_HANDLE_LITTLE_ENDIAN = MethodHandles.byteBufferViewVarHandle(int[].class, ByteOrder.LITTLE_ENDIAN);
    private static final VarHandle LONG_HANDLE_LITTLE_ENDIAN = MethodHandles.byteBufferViewVarHandle(long[].class, ByteOrder.LITTLE_ENDIAN);

    private final @NotNull ByteBuffer bb;
    private final @Range(from = 1, to = Integer.MAX_VALUE) int capacity;
    private @NotNull VarHandle intHandle = INT_HANDLE_BE;
    private @NotNull VarHandle longHandle = LONG_HANDLE_BE;

    /**
     * Build a byte sequence from an existing {@link ByteBuffer}
     * <p>The byte order of a newly-created Bytes is always {@link ByteOrder#BIG_ENDIAN BIG_ENDIAN}
     *
     * @param bb the ByteBuffer
     */
    public ByteBufferBytes(@NotNull ByteBuffer bb) {
        this.bb = Objects.requireNonNull(bb);
        this.capacity = bb.capacity();
    }

    /**
     * Build a byte sequence from a {@link ByteBuffer} built from a byte array
     * <p>The byte order of a newly-created Bytes is always {@link ByteOrder#BIG_ENDIAN BIG_ENDIAN}
     *
     * @param byteArray the byte array
     */
    public ByteBufferBytes(byte @NotNull [] byteArray) {
        this.bb = ByteBuffer.wrap(Objects.requireNonNull(byteArray));
        this.capacity = byteArray.length;
    }

    /**
     * Build a byte sequence from a fresh {@link ByteBuffer}
     * <p>The byte order of a newly-created Bytes is always {@link ByteOrder#BIG_ENDIAN BIG_ENDIAN}
     *
     * @param direct true for a direct ByteBuffer
     * @param capacity total capacity of the ByteBuffer
     */
    public ByteBufferBytes(boolean direct, @Range(from = 1, to = Integer.MAX_VALUE) int capacity) {
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
    public byte readByteAt(@Range(from = 0, to = Integer.MAX_VALUE - 1) int index) {
        return this.bb.get(index);
    }

    @Override
    public int readIntAt(@Range(from = 0, to = Integer.MAX_VALUE - 1) int index) {
        return (int) this.intHandle.get(bb, index);
    }

    @Override
    public void writeByteAt(@Range(from = 0, to = Integer.MAX_VALUE - 1) int index, byte value) {
        this.bb.put(index, value);
    }

    @Override
    public void writeIntAt(@Range(from = 0, to = Integer.MAX_VALUE - 1) int index, int value) {
        this.bb.putInt(index, value);
    }

    @Override
    public @Range(from = 1, to = Integer.MAX_VALUE) int getByteSize() {
        return this.capacity;
    }

    @Override
    public void setByteOrder(@NotNull ByteOrder byteOrder) {
        this.bb.order(byteOrder);
        if (byteOrder == ByteOrder.BIG_ENDIAN) {
            this.intHandle = INT_HANDLE_BE;
            this.longHandle = LONG_HANDLE_BE;
        } else {
            this.intHandle = INT_HANDLE_LITTLE_ENDIAN;
            this.longHandle = LONG_HANDLE_LITTLE_ENDIAN;
        }
    }

    @Override
    public byte[] toByteArray() {
        final var capacity = this.bb.capacity();
        final var byteArray = new byte[capacity];
        this.bb.get(byteArray, 0, capacity);
        return byteArray;
    }

    @Override
    public @NotNull ByteBuffer toByteBuffer() {
        return this.bb;
    }
}
