/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.bytes;

import io.limo.bytes.Data;
import io.limo.bytes.Reader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.io.EOFException;
import java.nio.ByteOrder;
import java.util.Objects;

/**
 * Implementation of the immutable {@code Data} interface based on a single byte sequence
 *
 * @see Data
 */
public class ByBuData implements Data {

    /**
     * The byte sequence into which the elements of the SingleData are stored
     */
    @NotNull
    final ByBu byBu;

    /**
     * The limit of byte sequence
     */
    final int limit;

    boolean isBigEndian = true;

    /**
     * The data reader
     */
    @NotNull
    Reader reader;

    public ByBuData(@NotNull ByBu byBu, int limit) {
        this.byBu = Objects.requireNonNull(byBu);
        this.limit = limit;
        this.reader = new ReaderImpl();
    }

    @Override
    public @Range(from = 1, to = Long.MAX_VALUE) long getByteSize() {
        return byBu.getByteSize();
    }

    @NotNull
    @Override
    public Reader getReader() {
        return reader;
    }

    @NotNull
    @Override
    public ByteOrder getByteOrder() {
        return isBigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
    }

    @Override
    public void setByteOrder(@NotNull ByteOrder byteOrder) {
        isBigEndian = (byteOrder == ByteOrder.BIG_ENDIAN);
        // affect this byte order to memory
        byBu.setByteOrder(byteOrder);
    }

    @Override
    public void close() {
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
        public byte readByte() throws EOFException {
            final var currentIndex = index;
            final var byteSize = 1;
            final var targetLimit = currentIndex + byteSize;

            // 1) at least 1 byte left to read a byte in memory
            if (limit >= targetLimit) {
                index = targetLimit;
                return byBu.readByteAt(currentIndex);
            }

            // 2) memory is exhausted
            throw new EOFException("End of file while reading memory");
        }

        @Override
        public int readInt() throws EOFException {
            final var currentIndex = index;
            final var intSize = 4;
            final var targetLimit = currentIndex + intSize;

            // 1) at least 4 bytes left to read an int in memory
            if (limit >= targetLimit) {
                index = targetLimit;
                return byBu.readIntAt(currentIndex);
            }

            // 2) memory is exhausted
            throw new EOFException("End of file while reading memory");
        }

        @Override
        public void close() {
            ByBuData.this.close();
        }
    }
}
