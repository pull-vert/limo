/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.bytes;

import io.limo.bytes.Data;
import io.limo.bytes.Reader;
import io.limo.bytes.ReaderUnderflowException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;

/**
 * Implementation of the immutable {@link Data} interface based on a single {@link ByBu}
 */
public class ByBuData implements Data {

    /**
     * The byte sequence into which the elements of the SingleData are stored
     */
    final @NotNull ByBu byBu;

    /**
     * The limit of byte sequence
     */
    final int limit;

    private boolean isBigEndian = true;

    /**
     * The data reader
     */
    private @NotNull Reader reader;

    public ByBuData(@NotNull ByteBuffer bb) {
        this.byBu = new ByteBufferByBu(Objects.requireNonNull(bb));
        this.limit = bb.capacity();
        this.reader = new ReaderImpl();
    }

    @Override
    public final @Range(from = 1, to = Integer.MAX_VALUE) long getByteSize() {
        return byBu.getByteSize();
    }

    @Override
    public final @NotNull Reader getReader() {
        return reader;
    }

    @Override
    public final @NotNull ByteOrder getByteOrder() {
        return isBigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
    }

    @Override
    public final void setByteOrder(@NotNull ByteOrder byteOrder) {
        isBigEndian = (byteOrder == ByteOrder.BIG_ENDIAN);
        // affect this byte order to memory
        byBu.setByteOrder(byteOrder);
    }

    @Override
    public final @Range(from = 1, to = Long.MAX_VALUE) long getLimit() {
        return this.limit;
    }

    @Override
    public final void close() {
    }

    /**
     * Implementation of the {@code Reader} interface that reads in data array of {@code ArrayData}
     */
    private final class ReaderImpl implements Reader {

        /**
         * Reading index in the memory
         */
        private int index = 0;


        @Override
        public byte readByte() {
            final var currentIndex = this.index;
            final var byteSize = 1;
            final var targetLimit = currentIndex + byteSize;

            // 1) at least 1 byte left to read a byte in memory
            if (limit >= targetLimit) {
                this.index = targetLimit;
                return byBu.readByteAt(currentIndex);
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
                return byBu.readIntAt(currentIndex);
            }

            // 2) memory is exhausted
            throw new ReaderUnderflowException();
        }

        @Override
        public void close() {
            ByBuData.this.close();
        }
    }
}
