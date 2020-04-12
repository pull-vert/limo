/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.transfer;

import io.limo.transfer.Data;
import io.limo.Reader;
import io.limo.internal.bytes.Bytes;
import io.limo.utils.BytesOps;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.nio.ByteOrder;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.stream.Stream;

abstract class AbstractBytesArrayData<T extends Bytes> implements Data {

    /**
     * The array of byte sequence into which the elements of this BytesArrayData are stored
     */
    protected T @NotNull [] bytesArray;

    /**
     * Default initial capacity of the array of byte sequences
     */
    static final int DEFAULT_CAPACITY = 4;

    /**
     * The array of limits : one for each byte sequence
     */
    int @NotNull [] limits;

    /**
     * Index of the byte sequence in array that is currently read
     */
    @Range(from = 0, to = Integer.MAX_VALUE - 1) int readIndex = 0;

    /**
     * Last index of the byte sequence in array that has been written
     *
     * @implNote It has a 0 initial value, even if first memory was not written
     */
    @Range(from = 0, to = Integer.MAX_VALUE - 1) int writeIndex = 0;

    /**
     * The data reader
     */
    @NotNull Reader reader;

    @Range(from = 0, to = Integer.MAX_VALUE - 1) long byteSize;

    @NotNull ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;

    /**
     * @return next not empty byte sequence index from array, or empty if none exists
     */
    private @NotNull OptionalInt getNextReadIndex() {
        if (this.readIndex < this.writeIndex) {
            return OptionalInt.of(++this.readIndex);
        }
        return OptionalInt.empty();
    }

    @Override
    public final @Range(from = 1, to = Long.MAX_VALUE) long getByteSize() {
        return this.byteSize;
    }

    @Override
    public final @NotNull Reader getReader() {
        return this.reader;
    }

    @Override
    public final @Range(from = 0, to = Long.MAX_VALUE - 1) long getLimit() {
        // sum of all limits
        var totalLimit = 0L;
        for (final var limit : this.limits) {
            totalLimit += limit;
        }
        return totalLimit;
    }

    final void setByteOrder(@NotNull ByteOrder byteOrder) {
        this.byteOrder = byteOrder;
        Stream.of(this.bytesArray)
                .filter(Objects::nonNull)
                .forEach(bytes -> bytes.setByteOrder(byteOrder));
    }

    /**
     * closes all not null bytes in bytes array
     */
    @Override
    public final void close() {
        Stream.of(this.bytesArray)
                .filter(Objects::nonNull)
                .forEach(Bytes::close);
    }

    /**
     * Implementation of the {@code Reader} interface that reads in data array of {@code ArrayData}
     */
    protected final class ReaderImpl implements Reader {

        /**
         * Current byte sequence to read from
         */
        private @NotNull Bytes bytes;

        /**
         * Reading index in the current {@link #bytes}
         */
        private int index = 0;

        /**
         * Number of bytes loaded in the current {@link #bytes}
         */
        private int limit;

        boolean isBigEndian = true;

        /**
         * Current byte sequence is the first in the data array of {@code ArrayData}
         */
        protected ReaderImpl() {
            this.bytes = Objects.requireNonNull(bytesArray[0]);
            this.limit = limits[0];
        }

        @Override
        public byte readByte() {
            final var currentIndex = this.index;
            final var currentLimit = this.limit;
            final var byteSize = 1;
            final var targetLimit = currentIndex + byteSize;

            // 1) at least 1 byte left to read a byte in current byte sequence
            if (currentLimit >= targetLimit) {
                this.index = targetLimit;
                return this.bytes.readByteAt(currentIndex);
            }

            // 2) current byte sequence is exactly exhausted
            // let's get next byte sequence and if present read it
            nextBytes();

            // we are at 0 index in newly obtained byte sequence

            if (this.limit >= byteSize) {
                this.index = byteSize;
                return this.bytes.readByteAt(0);
            }

            // 3) memory is exhausted
            throw new ReaderUnderflowException();
        }

        @Override
        public int readInt() {
            final var currentIndex = this.index;
            final var currentLimit = this.limit;
            final var intSize = 4;
            final var targetLimit = currentIndex + intSize;

            // 1) at least 4 bytes left to read an int in current byte sequence
            if (currentLimit >= targetLimit) {
                this.index = targetLimit;
                return this.bytes.readIntAt(currentIndex);
            }

            // 2) current byte sequence is exactly exhausted
            if (currentLimit == currentIndex) {
                // let's get next byte sequence and if present read it
                nextBytes();

                // we are at 0 index in newly obtained byte sequence
                if (this.limit >= intSize) {
                    this.index = intSize;
                    return this.bytes.readIntAt(0);
                }

                // 3) memory is exhausted
                throw new ReaderUnderflowException();
            }

            // 3) must read some bytes in current byte sequence, some others from next one
            return BytesOps.bytesToInt(readByte(), readByte(), readByte(), readByte(), this.isBigEndian);
        }

        /**
         * Switch to next byte sequence because current one is exhausted
         *
         * @throws ReaderUnderflowException if no readable next byte sequence
         */
        private void nextBytes() {
            final var nextReadIndex = getNextReadIndex().orElseThrow(ReaderUnderflowException::new);
            this.bytes = Objects.requireNonNull(bytesArray[nextReadIndex]);
            this.limit = limits[nextReadIndex];
        }

        @Override
        public final @NotNull ByteOrder getByteOrder() {
            return byteOrder;
        }

        @Override
        public final void setByteOrder(@NotNull ByteOrder byteOrder) {
            this.isBigEndian = (byteOrder == ByteOrder.BIG_ENDIAN);
            AbstractBytesArrayData.this.setByteOrder(byteOrder);
        }
    }
}
