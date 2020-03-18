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
 * A read-only (immutable) byte sequence based on a {@link ByteBuffer}
 */
public class ByteBufferBytes implements Bytes {

    private static final VarHandle INT_HANDLE_BE = MethodHandles.byteBufferViewVarHandle(int[].class, ByteOrder.BIG_ENDIAN);
    private static final VarHandle LONG_HANDLE_BE = MethodHandles.byteBufferViewVarHandle(long[].class, ByteOrder.BIG_ENDIAN);

    private static final VarHandle INT_HANDLE_LITTLE_ENDIAN = MethodHandles.byteBufferViewVarHandle(int[].class, ByteOrder.LITTLE_ENDIAN);
    private static final VarHandle LONG_HANDLE_LITTLE_ENDIAN = MethodHandles.byteBufferViewVarHandle(long[].class, ByteOrder.LITTLE_ENDIAN);

    @NotNull ByteBuffer bb;
    @Range(from = 1, to = Integer.MAX_VALUE) int capacity;
    @NotNull VarHandle intHandle = INT_HANDLE_BE;
    @NotNull VarHandle longHandle = LONG_HANDLE_BE;

    ByteBufferBytes() {
    }

    /**
     * Build a read-only (immutable) byte sequence from an existing {@link ByteBuffer}
     * <p>The byte order of a newly-created Bytes is always {@link ByteOrder#BIG_ENDIAN BIG_ENDIAN}
     *
     * @param bb the ByteBuffer
     */
    public ByteBufferBytes(@NotNull ByteBuffer bb) {
        if (Objects.requireNonNull(bb).isReadOnly()) {
            this.bb = bb;
        } else {
            this.bb = bb.asReadOnlyBuffer();
        }
        this.capacity = bb.capacity();
    }

    /**
     * Build a read-only (immutable) byte sequence from a {@link ByteBuffer} built from a byte array
     * <p>The byte order of a newly-created Bytes is always {@link ByteOrder#BIG_ENDIAN BIG_ENDIAN}
     *
     * @param byteArray the byte array
     */
    public ByteBufferBytes(byte @NotNull [] byteArray) {
        this.bb = ByteBuffer.wrap(Objects.requireNonNull(byteArray)).asReadOnlyBuffer();
        this.capacity = byteArray.length;
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
