/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.bytes;

import io.limo.bytes.Data;
import io.limo.bytes.MutableData;
import io.limo.bytes.Writer;
import io.limo.bytes.WriterOverflowException;
import io.limo.utils.BytesOps;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * Implementation of the mutable {@link MutableData} interface based on a resizable array of {@link Bytes}
 *
 * @implNote Inspired by ArrayList
 * @see BytesArrayData
 */
public final class MutableBytesArrayData extends BytesArrayData implements MutableData {

    /**
     * The bytes supplier, can act as a pool
     */
    private final @NotNull BytesSupplier bytesSupplier;

    /**
     * The data writer
     */
    private final @NotNull Writer writer;

    public MutableBytesArrayData(@NotNull BytesSupplier bytesSupplier) {
        this.bytesSupplier = Objects.requireNonNull(bytesSupplier);
        this.bytesArray[0] = bytesSupplier.get();
        this.writer = new WriterImpl();
    }

    public MutableBytesArrayData(@NotNull Data data, @NotNull BytesSupplier bytesSupplier) {
        Objects.requireNonNull(data);
        Objects.requireNonNull(bytesSupplier);

        // todo use instanceof pattern matching of java 14 https://openjdk.java.net/jeps/305
        if (data instanceof BytesArrayData) {
            final var arrayData = (BytesArrayData) data;
            this.bytesArray = arrayData.bytesArray;
            this.limits = arrayData.limits;
            this.readIndex = arrayData.readIndex;
            this.writeIndex = arrayData.writeIndex;
            this.reader = arrayData.reader;
        } else if (data instanceof BytesData) {
            final var byBuData = (BytesData) data;
            this.bytesArray = new Bytes[DEFAULT_CAPACITY];
            this.bytesArray[0] = byBuData.bytes;
            this.limits = new int[DEFAULT_CAPACITY];
            this.limits[0] = byBuData.limit;
            // read and write indexes both have an initial value of 0
            this.reader = new ReaderImpl();
        } else {
            throw new IllegalArgumentException("data type " + data.getClass().getTypeName() + " is not supported");
        }
        this.bytesSupplier = bytesSupplier;
        this.writer = new WriterImpl();
    }

    /**
     * Get a new byte sequence from {@link #bytesSupplier}
     *
     * @return newly obtained byte sequence
     */
    private @NotNull Bytes supplyNewBytes() {
        // no room left in array
        this.writeIndex += 1;
        if (this.writeIndex == this.bytesArray.length) {
            // increase array size by 2 times
            final var newLength = this.bytesArray.length * 2;
            this.bytesArray = Arrays.copyOf(bytesArray, newLength);
            this.limits = Arrays.copyOf(limits, newLength);
        }
        final var bytes = this.bytesSupplier.get();
        this.bytesArray[this.writeIndex] = bytes;
        return bytes;
    }

    @Override
    public @NotNull Writer getWriter() {
        return this.writer;
    }

    /**
     * Implementation of the {@code Writer} interface that writes in data array of {@link BytesArrayData}
     */
    private final class WriterImpl implements Writer {

        /**
         * Current byte sequence to write in
         */
        private @NotNull Bytes bytes;

        /**
         * Writing index in the current {@link #bytes}
         */
        private @Range(from = 0, to = Integer.MAX_VALUE - 1) int limit = 0;

        /**
         * Capacity of the current {@link #bytes}
         */
        private @Range(from = 1, to = Integer.MAX_VALUE) int capacity;

        /**
         * Current byte sequence is the last not null in the data array of {@code ArrayData}
         */
        private WriterImpl() {
            this.bytes = IntStream.rangeClosed(1, bytesArray.length)
                    .mapToObj(i -> bytesArray[bytesArray.length - i])
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElseThrow();
            this.capacity = this.bytes.getByteSize();
        }

        @Override
        public void writeByte(byte value) {
            final var currentLimit = this.limit;
            final var byteSize = 1;
            final var targetLimit = currentLimit + byteSize;

            // 1) at least 1 byte left to write a byte in current byte sequence
            if (this.capacity >= targetLimit) {
                this.limit = targetLimit;
                this.bytes.writeByteAt(currentLimit, value);
                return;
            }

            // 2) current byte sequence is exactly full
            // let's add a new byte sequence from supplier
            addNewBytes();

            // we are at 0 index in newly obtained byte sequence

            if (this.capacity < byteSize) {
                throw new WriterOverflowException();
            }
            this.limit = byteSize;
            this.bytes.writeByteAt(currentLimit, value);
        }

        @Override
        public void writeInt(int value) {
            final var currentLimit = this.limit;
            final var intSize = 4;
            final var targetLimit = currentLimit + intSize;

            // 1) at least 4 bytes left to write an int in current byte sequence
            if (this.capacity >= targetLimit) {
                this.limit = targetLimit;
                this.bytes.writeIntAt(currentLimit, value);
                return;
            }

            // 2) current byte sequence is exactly full
            if (currentLimit == this.capacity) {
                // let's add a new byte sequence from supplier
                addNewBytes();

                // we are at 0 index in newly obtained byte sequence
                if (this.capacity >= intSize) {
                    this.limit = intSize;
                    this.bytes.writeIntAt(currentLimit, value);
                    return;
                }
                throw new WriterOverflowException();
            }

            // 3) must write some bytes in current byte sequence, some others in next one
            for (final var b : BytesOps.intToBytes(value, isBigEndian)) {
                writeByte(b);
            }
        }

        @Override
        public void close() {
            MutableBytesArrayData.this.close();
        }

        /**
         * Current byte sequence is full, add a new Bytes in data array
         */
        private void addNewBytes() {
            this.bytes = supplyNewBytes();
            this.capacity = this.bytes.getByteSize();
            this.limit = 0;
        }
    }
}
