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

/**
 * Implementation of the immutable {@link Data} interface based on a fixed size array of {@link ByBu}
 *
 * @see MutableByBuArrayData
 */
public class ByBuArrayData implements Data {

    /**
     * Default initial capacity of the array of byte sequences
     */
    private static final int DEFAULT_CAPACITY = 4;

    /**
     * The array of byte sequence into which the elements of the ArrayData are stored
     */
    protected ByBu @NotNull [] byBus;

    /**
     * The array of limits : one for each byte sequence
     */
    protected int @NotNull [] limits;

    /**
     * Index of the byte sequence in array that is currently read
     */
    protected int readIndex = 0;

    /**
     * Last index of the memory in data array that has been written
     *
     * @implNote It has a 0 initial value, even if first memory was not written
     */
    protected int writeIndex = 0;

    protected boolean isBigEndian = true;

    /**
     * The data reader
     */
    protected @NotNull Reader reader;

    private final long byteSize;

    public ByBuArrayData() {
        // init memories and limits with DEFAULT_CAPACITY size
        this.byBus = new ByBu[DEFAULT_CAPACITY];
        this.limits = new int[DEFAULT_CAPACITY];
        this.byteSize = 0;
        this.reader = new ReaderImpl();
    }

    public ByBuArrayData(ByBu @NotNull [] byBus, int @NotNull [] limits) {
        this.byBus = Objects.requireNonNull(byBus);
        this.limits = Objects.requireNonNull(limits);
        this.writeIndex = limits.length;
        var byteSizesSum = 0;
        for (ByBu byBu : byBus) {
            byteSizesSum += byBu.getByteSize();
        }
        this.byteSize = byteSizesSum;
        this.reader = new ReaderImpl();
    }

    public ByBuArrayData(@NotNull Data first, Data @NotNull ... rest) {
        Objects.requireNonNull(first);
        Objects.requireNonNull(rest);

        // must get total length
        var totalLength = 0;
        // todo use instanceof pattern matching of java 14 https://openjdk.java.net/jeps/305
        for (final var data : rest) {
            if (data instanceof ByBuArrayData) {
                totalLength += ((ByBuArrayData) data).writeIndex + 1;
            }
        }

        var byteSizesSum = first.getByteSize();
        // initiate arrays
        var offset = 0;
        if (first instanceof ByBuArrayData) {
            final var firstArrayData = (ByBuArrayData) first;
            offset = firstArrayData.writeIndex + 1;
            totalLength += offset;
            this.byBus = Arrays.copyOf(firstArrayData.byBus, totalLength);
            this.limits = Arrays.copyOf(firstArrayData.limits, totalLength);
        } else {
            throw new IllegalArgumentException("data type " + first.getClass().getTypeName() + " is unsupported");
        }
        this.writeIndex = totalLength;

        int dataLength;
        for (final var data : rest) {
            byteSizesSum += data.getByteSize();
            if (data instanceof ByBuArrayData) {
                final var arrayData = (ByBuArrayData) data;
                dataLength = arrayData.writeIndex + 1;
                System.arraycopy(arrayData.byBus, 0, this.byBus, offset, dataLength);
                System.arraycopy(arrayData.limits, 0, this.limits, offset, dataLength);
                offset += dataLength;
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
    public @Range(from = 1, to = Long.MAX_VALUE) long getByteSize() {
        return this.byteSize;
    }

    @Override
    public @NotNull Reader getReader() {
        return this.reader;
    }

    @Override
    public @NotNull ByteOrder getByteOrder() {
        return this.isBigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
    }

    @Override
    public void setByteOrder(@NotNull ByteOrder byteOrder) {
        this.isBigEndian = (byteOrder == ByteOrder.BIG_ENDIAN);
        // affect this byte order to all memories
        for (final var byBu : this.byBus) {
            Optional.ofNullable(byBu).ifPresent(mem -> mem.setByteOrder(byteOrder));
        }
    }

    @Override
    public void close() {
    }

    /**
     * Implementation of the {@code Reader} interface that reads in data array of {@code ArrayData}
     */
    private final class ReaderImpl implements Reader {

        /**
         * Current byte sequence to read from
         */
        private @NotNull ByBu byBu;

        /**
         * Reading index in the current {@link #byBu}
         */
        private int index = 0;

        /**
         * Number of bytes loaded in the current {@link #byBu}
         */
        private int limit;

        /**
         * Current byte sequence is the first in the data array of {@code ArrayData}
         */
        private ReaderImpl() {
            this.byBu = Objects.requireNonNull(byBus[0]);
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
                return this.byBu.readByteAt(currentIndex);
            }

            // 2) current byte sequence is exactly exhausted
            // let's get next byte sequence and if present read it
            nextByteSequence();

            // we are at 0 index in newly obtained byte sequence

            if (this.limit >= byteSize) {
                this.index = byteSize;
                return this.byBu.readByteAt(0);
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
                return this.byBu.readIntAt(currentIndex);
            }

            // 2) current byte sequence is exactly exhausted
            if (currentLimit == currentIndex) {
                // let's get next byte sequence and if present read it
                nextByteSequence();

                // we are at 0 index in newly obtained byte sequence

                if (this.limit >= intSize) {
                    this.index = intSize;
                    return this.byBu.readIntAt(0);
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
        private void nextByteSequence() {
            final var nextReadIndex = getNextReadIndex().orElseThrow(ReaderUnderflowException::new);
            this.byBu = Objects.requireNonNull(byBus[nextReadIndex]);
            this.limit = limits[nextReadIndex];
        }

        @Override
        public void close() {
            ByBuArrayData.this.close();
        }
    }
}
