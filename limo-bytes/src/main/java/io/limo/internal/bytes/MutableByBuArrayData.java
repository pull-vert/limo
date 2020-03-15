/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.bytes;

import io.limo.bytes.Data;
import io.limo.bytes.MutableData;
import io.limo.bytes.Writer;
import org.jetbrains.annotations.NotNull;

import java.io.EOFException;
import java.util.Arrays;
import java.util.Objects;

/**
 * Implementation of the mutable {@code MutableData} interface based on a resizable array of byte sequences
 *
 * @implNote Inspired by ArrayList
 * @see ByBuArrayData
 * @see MutableData
 */
public final class MutableByBuArrayData extends ByBuArrayData implements MutableData {

    /**
     * The memory supplier, can act as a pool
     */
    private final @NotNull ByBuSupplier byBuSupplier;

    /**
     * The data writer
     */
    private final @NotNull Writer writer;

    public MutableByBuArrayData(@NotNull ByBuSupplier byBuSupplier) {
        this.byBuSupplier = Objects.requireNonNull(byBuSupplier);
        // First element in data = initialMemory
        this.byBus[0] = byBuSupplier.get();
        this.writer = new WriterImpl();
    }

    public MutableByBuArrayData(@NotNull Data data, @NotNull ByBuSupplier byBuSupplier) {
        // todo use instanceof pattern matching of java 14 https://openjdk.java.net/jeps/305
        if (Objects.requireNonNull(data) instanceof ByBuArrayData) {
            final var arrayData = (ByBuArrayData) data;
            this.byBus = arrayData.byBus;
            this.limits = arrayData.limits;
            this.readIndex = arrayData.readIndex;
            this.writeIndex = arrayData.writeIndex;
            this.reader = arrayData.reader;
        } else {
            throw new IllegalArgumentException("data type " + data.getClass().getTypeName() + " is unsupported");
        }
        this.byBuSupplier = Objects.requireNonNull(byBuSupplier);
        this.writer = new WriterImpl();
    }

    /**
     * Get a new byte sequence from {@link #byBuSupplier}
     *
     * @return newly obtained byte sequence
     */
    private @NotNull ByBu supplyNewByteSequence() {
        // no room left in array
        this.writeIndex += 1;
        if (this.writeIndex == this.byBus.length) {
            // increase array size by 2 times
            final var newLength = this.byBus.length * 2;
            this.byBus = Arrays.copyOf(byBus, newLength);
            this.limits = Arrays.copyOf(limits, newLength);
        }
        final var newMemory = this.byBuSupplier.get();
        this.byBus[this.writeIndex] = newMemory;
        return newMemory;
    }

    @Override
    public @NotNull Writer getWriter() {
        return this.writer;
    }

    /**
     * Implementation of the {@code Writer} interface that writes in data array of {@link ByBuArrayData}
     */
    private final class WriterImpl implements Writer {

        /**
         * Current byte sequence to write in
         */
        private @NotNull ByBu byBu;

        /**
         * Writing index in the current {@link #byBu}
         */
        private int limit = 0;

        /**
         * Capacity of the current {@link #byBu}
         */
        private int capacity;

        /**
         * Current byte sequence is the last in the data array of {@code ArrayData}
         */
        private WriterImpl() {
            this.byBu = Objects.requireNonNull(byBus[byBus.length - 1]);
            this.capacity = this.byBu.getByteSize();
        }

        @Override
        public void writeByte(byte value) throws EOFException {
            final var currentLimit = this.limit;
            final var byteSize = 1;
            final var targetLimit = currentLimit + byteSize;

            // 1) at least 1 byte left to write a byte in current memory
            if (this.capacity >= targetLimit) {
                this.limit = targetLimit;
                this.byBu.writeByteAt(currentLimit, value);
                return;
            }

            // 2) current memory is exactly full
            // let's add a new byte sequence from supplier
            addNewByteSequence();

            // we are at 0 index in newly obtained byte sequence

            if (this.capacity < byteSize) {
                throw new EOFException("Empty byte sequence, no room for writing a byte");
            }
            this.limit = byteSize;
            this.byBu.writeByteAt(currentLimit, value);
        }

        @Override
        public void writeInt(int value) throws EOFException {
            final var currentLimit = this.limit;
            final var intSize = 4;
            final var targetLimit = currentLimit + intSize;

            // 1) at least 4 bytes left to write an int in current byte sequence
            if (this.capacity >= targetLimit) {
                this.limit = targetLimit;
                this.byBu.writeIntAt(currentLimit, value);
                return;
            }

            // 2) current byte sequence is exactly full
            if (currentLimit == this.capacity) {
                // let's add a new byte sequence from supplier
                addNewByteSequence();

                // we are at 0 index in newly obtained byte sequence
                if (this.capacity >= intSize) {
                    this.limit = intSize;
                    this.byBu.writeIntAt(currentLimit, value);
                    return;
                }
                throw new EOFException("Byte sequence too small, no room for writing an int");
            }

            // 3) must write some bytes in current byte sequence, some others in next one
            for (final var b : BytesOps.intToBytes(value, isBigEndian)) {
                writeByte(b);
            }
        }

        @Override
        public void close() {
            MutableByBuArrayData.this.close();
        }

        /**
         * Current byte sequence is full, add a new ByteSequence in data array
         */
        private void addNewByteSequence() {
            this.byBu = supplyNewByteSequence();
            this.capacity = this.byBu.getByteSize();
            this.limit = 0;
        }
    }
}
