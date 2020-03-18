/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.bytes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.nio.ByteOrder;
import java.util.Objects;

/**
 *  A byte sequence based on a {@code byte[]}
 */
public final class ByteArrayBytes implements Bytes {

    private static final VarHandle INT_HANDLE_BE = MethodHandles.byteArrayViewVarHandle(int[].class, ByteOrder.BIG_ENDIAN);
    private static final VarHandle LONG_HANDLE_BE = MethodHandles.byteArrayViewVarHandle(long[].class, ByteOrder.BIG_ENDIAN);

    private static final VarHandle INT_HANDLE_LITTLE_ENDIAN = MethodHandles.byteArrayViewVarHandle(int[].class, ByteOrder.LITTLE_ENDIAN);
    private static final VarHandle LONG_HANDLE_LITTLE_ENDIAN = MethodHandles.byteArrayViewVarHandle(long[].class, ByteOrder.LITTLE_ENDIAN);

    private final byte @NotNull [] byteArray;
    private final @Range(from = 1, to = Integer.MAX_VALUE) int capacity;
    private @NotNull VarHandle intHandle = INT_HANDLE_BE;
    private @NotNull VarHandle longHandle = LONG_HANDLE_BE;

    /**
     * Build a byte sequence from an existing {@code byte[]}
     * <p>The byte order of a newly-created Bytes is always {@link ByteOrder#BIG_ENDIAN BIG_ENDIAN}
     *
     * @param byteArray the byte array
     */
    public ByteArrayBytes(byte @NotNull [] byteArray) {
        this.byteArray = Objects.requireNonNull(byteArray);
        this.capacity = byteArray.length;
    }

    /**
     * Build a byte sequence from a fresh {@code byte[]}
     * <p>The byte order of a newly-created Bytes is always {@link ByteOrder#BIG_ENDIAN BIG_ENDIAN}
     *
     * @param capacity total capacity of the ByteBuffer
     */
    public ByteArrayBytes(@Range(from = 1, to = Integer.MAX_VALUE) int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be > 0");
        }
        this.byteArray = new byte[capacity];
        this.capacity = capacity;
    }

    @Override
    public byte readByteAt(@Range(from = 0, to = Integer.MAX_VALUE - 1) int index) {
        return this.byteArray[index];
    }

    @Override
    public int readIntAt(@Range(from = 0, to = Integer.MAX_VALUE - 1) int index) {
        return (int) this.intHandle.get(byteArray, index);
    }

    @Override
    public void writeByteAt(@Range(from = 0, to = Integer.MAX_VALUE - 1) int index, byte value) {
        this.byteArray[index] = value;
    }

    @Override
    public void writeIntAt(@Range(from = 0, to = Integer.MAX_VALUE - 1) int index, int value) {
        this.intHandle.set(byteArray, index, value);
    }

    @Override
    public @Range(from = 1, to = Integer.MAX_VALUE) int getByteSize() {
        return this.capacity;
    }

    @Override
    public void setByteOrder(@NotNull ByteOrder byteOrder) {
        if (byteOrder == ByteOrder.BIG_ENDIAN) {
            this.intHandle = INT_HANDLE_BE;
            this.longHandle = LONG_HANDLE_BE;
        } else {
            this.intHandle = INT_HANDLE_LITTLE_ENDIAN;
            this.longHandle = LONG_HANDLE_LITTLE_ENDIAN;
        }
    }
}
