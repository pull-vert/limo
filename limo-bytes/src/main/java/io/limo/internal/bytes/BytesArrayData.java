/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.bytes;

import io.limo.bytes.Data;
import io.limo.bytes.Reader;
import io.limo.bytes.ReaderUnderflowException;
import io.limo.utils.BytesOps;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Stream;

/**
 * Implementation of the immutable {@link Data} interface based on a fixed size array of {@link Bytes}
 *
 * @see MutableBytesArrayData
 */
public class BytesArrayData implements Data {

    /**
     * Default initial capacity of the array of byte sequences
     */
    protected static final int DEFAULT_CAPACITY = 4;

    /**
     * The array of byte sequence into which the elements of the ByBuArrayData are stored
     */
    protected Bytes @NotNull [] bytesArray;

    /**
     * The array of limits : one for each byte sequence
     */
    protected int @NotNull [] limits;

    /**
     * Index of the byte sequence in array that is currently read
     */
    protected @Range(from = 0, to = Integer.MAX_VALUE - 1) int readIndex = 0;

    /**
     * Last index of the byte sequence in array that has been written
     *
     * @implNote It has a 0 initial value, even if first memory was not written
     */
    protected @Range(from = 0, to = Integer.MAX_VALUE - 1) int writeIndex = 0;

    protected boolean isBigEndian = true;

    /**
     * The data reader
     */
    protected @NotNull Reader reader;

    private final @Range(from = 0, to = Integer.MAX_VALUE - 1) long byteSize;

    protected BytesArrayData() {
        // init memories and limits with DEFAULT_CAPACITY size
        this.bytesArray = new Bytes[DEFAULT_CAPACITY];
        this.limits = new int[DEFAULT_CAPACITY];
        this.byteSize = 0;
        this.reader = new ReaderImpl();
    }

    public BytesArrayData(@NotNull Data first, Data @NotNull ... rest) {
        Objects.requireNonNull(first);
        Objects.requireNonNull(rest);

        // to get total capacity we start with a loop on rest array
        var totalCapacity = 0;
        // todo use instanceof pattern matching of java 14 https://openjdk.java.net/jeps/305
        for (final var data : rest) {
            if (data instanceof BytesArrayData) {
                totalCapacity += ((BytesArrayData) data).writeIndex + 1;
            } else if (data instanceof BytesData) {
                totalCapacity++;
            } else {
                throw new IllegalArgumentException("data type " + data.getClass().getTypeName() + " is not supported");
            }
        }

        var byteSizesSum = first.getByteSize();
        // initiate arrays
        var offset = 0;
        if (first instanceof BytesArrayData) {
            final var firstArrayData = (BytesArrayData) first;
            offset = firstArrayData.writeIndex + 1;
            totalCapacity += offset;
            if (totalCapacity < DEFAULT_CAPACITY) {
                totalCapacity = DEFAULT_CAPACITY;
            }
            this.bytesArray = Arrays.copyOf(firstArrayData.bytesArray, totalCapacity);
            this.limits = Arrays.copyOf(firstArrayData.limits, totalCapacity);
        } else if (first instanceof BytesData) {
            final var firstData = (BytesData) first;
            offset = 1;
            totalCapacity++;
            if (totalCapacity < DEFAULT_CAPACITY) {
                totalCapacity = DEFAULT_CAPACITY;
            }
            this.bytesArray = new Bytes[totalCapacity];
            this.bytesArray[0] = firstData.bytes;
            this.limits = new int[totalCapacity];
            this.limits[0] = firstData.limit;
        } else {
            throw new IllegalArgumentException("data type " + first.getClass().getTypeName() + " is not supported");
        }
        this.writeIndex = totalCapacity;

        int dataLength;
        for (final var data : rest) {
            byteSizesSum += data.getByteSize();
            if (data instanceof BytesArrayData) {
                final var arrayData = (BytesArrayData) data;
                dataLength = arrayData.writeIndex + 1;
                System.arraycopy(arrayData.bytesArray, 0, this.bytesArray, offset, dataLength);
                System.arraycopy(arrayData.limits, 0, this.limits, offset, dataLength);
                offset += dataLength;
            } else if (first instanceof BytesData) {
                final var byBuData = (BytesData) data;
                this.bytesArray[offset] = byBuData.bytes;
                this.limits[offset] = byBuData.limit;
                offset++;
            }
        }
        this.byteSize = byteSizesSum;

        this.reader = new ReaderImpl();
    }

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
    public final @NotNull ByteOrder getByteOrder() {
        return this.isBigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
    }

    @Override
    public final void setByteOrder(@NotNull ByteOrder byteOrder) {
        this.isBigEndian = (byteOrder == ByteOrder.BIG_ENDIAN);
        // set this byte order to all memories
        for (final var byBu : this.bytesArray) {
            Optional.ofNullable(byBu).ifPresent(mem -> mem.setByteOrder(byteOrder));
        }
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
            return BytesOps.bytesToInt(readByte(), readByte(), readByte(), readByte(), isBigEndian);
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
        public void close() {
            BytesArrayData.this.close();
        }
    }
}
