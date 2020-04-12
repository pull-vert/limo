/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.transfer;

import io.limo.transfer.MutableData;
import io.limo.Writer;
import io.limo.internal.bytes.MutableBytes;
import io.limo.utils.BytesOps;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * Implementation of the mutable {@link MutableData} interface based on a resizable array of {@link MutableBytes}
 *
 * @implNote Inspired by ArrayList
 * @see BytesArrayData
 */
public final class MutableBytesArrayData extends AbstractBytesArrayData<MutableBytes> implements MutableData {

    /**
     * The bytes supplier, can act as a pool
     */
    private final @NotNull MutableBytesSupplier mutableBytesSupplier;

    /**
     * The data writer
     */
    private final @NotNull Writer writer;

    public MutableBytesArrayData(@NotNull MutableBytesSupplier mutableBytesSupplier) {
        this.mutableBytesSupplier = Objects.requireNonNull(mutableBytesSupplier);

        // init memories and limits with DEFAULT_CAPACITY size
        this.bytesArray = new MutableBytes[DEFAULT_CAPACITY];
        this.limits = new int[DEFAULT_CAPACITY];
        this.byteSize = 0;
        this.bytesArray[0] = mutableBytesSupplier.get();
        this.reader = new ReaderImpl();
        this.writer = new WriterImpl();
    }

    @Override
    public @NotNull Writer getWriter() {
        return this.writer;
    }

    /**
     * Get a new byte sequence from {@link #mutableBytesSupplier}
     *
     * @return newly obtained byte sequence
     */
    private @NotNull MutableBytes supplyNewBytes() {
        // no room left in array
        this.writeIndex += 1;
        if (this.writeIndex == this.bytesArray.length) {
            // increase array size by 2 times
            final var newLength = this.bytesArray.length * 2;
            this.bytesArray = Arrays.copyOf(bytesArray, newLength);
            this.limits = Arrays.copyOf(limits, newLength);
        }
        final var bytes = this.mutableBytesSupplier.get();
        this.bytesArray[this.writeIndex] = bytes;
        return bytes;
    }

    /**
     * Implementation of the {@code Writer} interface that writes in data array of {@link BytesArrayData}
     */
    private final class WriterImpl implements Writer {

        /**
         * Current byte sequence to write in
         */
        private @NotNull MutableBytes bytes;

        /**
         * Writing index in the current {@link #bytes}
         */
        private int limit = 0;

        /**
         * Capacity of the current {@link #bytes}
         */
        private int capacity;

        boolean isBigEndian = true;

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
            for (final var b : BytesOps.intToBytes(value, this.isBigEndian)) {
                writeByte(b);
            }
        }

        @Override
        public final @NotNull ByteOrder getByteOrder() {
            return byteOrder;
        }

        @Override
        public final void setByteOrder(@NotNull ByteOrder byteOrder) {
            this.isBigEndian = (byteOrder == ByteOrder.BIG_ENDIAN);
            MutableBytesArrayData.this.setByteOrder(byteOrder);
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
