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
 * @see ArrayData
 * @see MutableData
 */
public final class MutableArrayData extends ArrayData implements MutableData {

    /**
     * The memory supplier, can act as a pool
     */
    @NotNull
    private ByBuSupplier byBuSupplier;

    /**
     * The data writer
     */
    @NotNull
    private final Writer writer;

    public MutableArrayData(@NotNull ByBuSupplier byBuSupplier) {
        this.byBuSupplier = Objects.requireNonNull(byBuSupplier);
        final var initialMemory = byBuSupplier.get();
        // First element in data = initialMemory
        byBus[0] = initialMemory;
        writer = new WriterImpl();
    }

    public MutableArrayData(@NotNull Data data, @NotNull ByBuSupplier byBuSupplier) {
        // todo use instanceof pattern matching of java 14 https://openjdk.java.net/jeps/305
        if (Objects.requireNonNull(data) instanceof ArrayData) {
            final var arrayData = (ArrayData) data;
            byBus = arrayData.byBus;
            limits = arrayData.limits;
            readIndex = arrayData.readIndex;
            writeIndex = arrayData.writeIndex;
            reader = arrayData.reader;
        } else {
            throw new IllegalArgumentException("data type " + data.getClass().getTypeName() + " is unsupported");
        }
        this.byBuSupplier = Objects.requireNonNull(byBuSupplier);
        writer = new WriterImpl();
    }

    /**
     * Get a new byte sequence from {@link #byBuSupplier}
     *
     * @return newly obtained byte sequence
     */
    @NotNull
    private ByBu supplyNewByteSequence() {
        // no room left in array
        writeIndex += 1;
        if (writeIndex == byBus.length) {
            // increase array size by 2 times
            final var newLength = byBus.length * 2;
            byBus = Arrays.copyOf(byBus, newLength);
            limits = Arrays.copyOf(limits, newLength);
        }
        final var newMemory = byBuSupplier.get();
        byBus[writeIndex] = newMemory;
        return newMemory;
    }

    @NotNull
    @Override
    public Writer getWriter() {
        return writer;
    }

    /**
     * Implementation of the {@code Writer} interface that writes in data array of {@code ArrayData}
     */
    private final class WriterImpl implements Writer {

        /**
         * Current byte sequence to write in
         */
        @NotNull
        private ByBu byBu;

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
            byBu = Objects.requireNonNull(byBus[byBus.length - 1]);
            capacity = byBu.getByteSize();
        }

        @Override
        public void writeByte(byte value) throws EOFException {
            final var currentLimit = limit;
            final var byteSize = 1;
            final var targetLimit = currentLimit + byteSize;

            // 1) at least 1 byte left to write a byte in current memory
            if (capacity >= targetLimit) {
                limit = targetLimit;
                byBu.writeByteAt(currentLimit, value);
                return;
            }

            // 2) current memory is exactly full
            // let's add a new byte sequence from supplier
            addNewByteSequence();

            // we are at 0 index in newly obtained byte sequence

            if (capacity < byteSize) {
                throw new EOFException("Empty byte sequence, no room for writing a byte");
            }
            limit = byteSize;
            byBu.writeByteAt(currentLimit, value);
        }

        @Override
        public void writeInt(int value) throws EOFException {
            final var currentLimit = limit;
            final var intSize = 4;
            final var targetLimit = currentLimit + intSize;

            // 1) at least 4 bytes left to write an int in current byte sequence
            if (capacity >= targetLimit) {
                limit = targetLimit;
                byBu.writeIntAt(currentLimit, value);
                return;
            }

            // 2) current byte sequence is exactly full
            if (currentLimit == capacity) {
                // let's add a new byte sequence from supplier
                addNewByteSequence();

                // we are at 0 index in newly obtained byte sequence
                if (capacity >= intSize) {
                    limit = intSize;
                    byBu.writeIntAt(currentLimit, value);
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
            MutableArrayData.this.close();
        }

        /**
         * Current byte sequence is full, add a new ByteSequence in data array
         */
        private void addNewByteSequence() {
            byBu = supplyNewByteSequence();
            capacity = byBu.getByteSize();
            limit = 0;
        }
    }
}
