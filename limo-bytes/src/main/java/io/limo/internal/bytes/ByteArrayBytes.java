/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.bytes;

import jdk.incubator.foreign.MemorySegment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;

/**
 * A read-only (immutable) byte sequence based on a {@code byte[]}
 */
public class ByteArrayBytes implements Bytes {

    static final VarHandle INT_HANDLE_BE = MethodHandles.byteArrayViewVarHandle(int[].class, ByteOrder.BIG_ENDIAN);
    static final VarHandle LONG_HANDLE_BE = MethodHandles.byteArrayViewVarHandle(long[].class, ByteOrder.BIG_ENDIAN);

    static final VarHandle INT_HANDLE_LE = MethodHandles.byteArrayViewVarHandle(int[].class, ByteOrder.LITTLE_ENDIAN);
    static final VarHandle LONG_HANDLE_LE = MethodHandles.byteArrayViewVarHandle(long[].class, ByteOrder.LITTLE_ENDIAN);

    byte @NotNull [] byteArray;
    @Range(from = 1, to = Integer.MAX_VALUE) int capacity;
    boolean isReadOnly = true;
    @Nullable MemorySegment segment = null;

    ByteArrayBytes() {
    }

    /**
     * Build a read-only (immutable) byte sequence from an existing {@code byte[]}
     *
     * @param byteArray the byte array
     */
    public ByteArrayBytes(byte @NotNull [] byteArray) {
        this.byteArray = Objects.requireNonNull(byteArray);
        this.capacity = byteArray.length;
    }

    @Override
    public byte readByteAt(@Range(from = 0, to = Integer.MAX_VALUE - 1) int index) {
        return this.byteArray[index];
    }

    @Override
    public int readIntAt(@Range(from = 0, to = Integer.MAX_VALUE - 1) int index, boolean isBigEndian) {
        if (isBigEndian) {
            return (int)  INT_HANDLE_BE.get(byteArray, index);
        }
        return (int) INT_HANDLE_LE.get(byteArray, index);
    }

    @Override
    public @Range(from = 1, to = Integer.MAX_VALUE) int getByteSize() {
        return this.capacity;
    }

    @Override
    public @NotNull Bytes acquire() {
        if (this.segment != null) {
            this.segment = this.segment.acquire();
        }
        return this;
    }

    @Override
    public boolean isReadOnly() {
        return this.isReadOnly;
    }

    @Override
    public byte @NotNull [] toByteArray() {
        return this.byteArray;
    }

    @Override
    public @NotNull ByteBuffer toByteBuffer() {
        if (this.segment != null) {
            return this.segment.asByteBuffer();
        }
        return ByteBuffer.wrap(this.byteArray);
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
