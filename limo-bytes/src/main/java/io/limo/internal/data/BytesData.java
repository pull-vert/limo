/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.data;

import io.limo.data.Data;
import io.limo.data.Reader;
import io.limo.data.ReaderUnderflowException;
import io.limo.internal.bytes.ByteBufferBytes;
import io.limo.internal.bytes.Bytes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;

/**
 * Implementation of the immutable {@link Data} interface based on a single {@link Bytes}
 */
public class BytesData implements Data {

    /**
     * The byte sequence into which the elements of this BytesData are stored
     */
    final @NotNull Bytes bytes;

    /**
     * The capacity of byte sequence
     */
    final int capacity;

    /**
     * The limit of byte sequence
     */
    final int limit;

    /**
     * The data reader
     */
    private @NotNull Reader reader;

    private @NotNull ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;

    public BytesData(@NotNull ByteBuffer bb) {
        this.bytes = new ByteBufferBytes(Objects.requireNonNull(bb));
        this.capacity = bb.capacity();
        this.limit = bb.limit();
        this.reader = new ReaderImpl();
    }

    @Override
    public final @Range(from = 1, to = Integer.MAX_VALUE) long getByteSize() {
        return this.capacity;
    }

    @Override
    public final @NotNull Reader getReader() {
        return this.reader;
    }

    @Override
    public final @Range(from = 0, to = Long.MAX_VALUE - 1) long getLimit() {
        return this.limit;
    }

    final void setByteOrder(@NotNull ByteOrder byteOrder) {
        this.byteOrder = byteOrder;
    }

    /**
     * Closes associated {@link #bytes}
     */
    @Override
    public final void close() {
        this.bytes.close();
    }

    /**
     * Implementation of the {@code Reader} interface that reads in {@link #bytes}
     */
    private final class ReaderImpl implements Reader {

        /**
         * Reading index in the memory
         */
        private int index = 0;
        private boolean isBigEndian = true;


        @Override
        public byte readByte() {
            final var currentIndex = this.index;
            final var byteSize = 1;
            final var targetLimit = currentIndex + byteSize;

            // 1) at least 1 byte left to read a byte in memory
            if (limit >= targetLimit) {
                this.index = targetLimit;
                return bytes.readByteAt(currentIndex);
            }

            // 2) memory is exhausted
            throw new ReaderUnderflowException();
        }

        @Override
        public int readInt() {
            final var currentIndex = this.index;
            final var intSize = 4;
            final var targetLimit = currentIndex + intSize;

            // 1) at least 4 bytes left to read an int in memory
            if (limit >= targetLimit) {
                this.index = targetLimit;
                return bytes.readIntAt(currentIndex);
            }

            // 2) memory is exhausted
            throw new ReaderUnderflowException();
        }

        @Override
        public final @NotNull ByteOrder getByteOrder() {
            return byteOrder;
        }

        @Override
        public final void setByteOrder(@NotNull ByteOrder byteOrder) {
            BytesData.this.setByteOrder(byteOrder);
        }

        @Override
        public void close() {
            BytesData.this.close();
        }
    }
}
