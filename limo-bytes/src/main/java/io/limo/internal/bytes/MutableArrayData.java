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
    private ByteSequenceSupplier byteSequenceSupplier;

    /**
     * The data writer
     */
    @NotNull
    private final Writer writer;

    public MutableArrayData(@NotNull ByteSequenceSupplier byteSequenceSupplier) {
        this.byteSequenceSupplier = Objects.requireNonNull(byteSequenceSupplier);
        final var initialMemory = byteSequenceSupplier.get();
        // First element in data = initialMemory
        byteSequences[0] = initialMemory;
        writer = new WriterImpl();
    }

    public MutableArrayData(@NotNull Data data, @NotNull ByteSequenceSupplier byteSequenceSupplier) {
        // todo use instanceof pattern matching of java 14 https://openjdk.java.net/jeps/305
        if (Objects.requireNonNull(data) instanceof ArrayData) {
            final var arrayData = (ArrayData) data;
            byteSequences = arrayData.byteSequences;
            limits = arrayData.limits;
            readIndex = arrayData.readIndex;
            writeIndex = arrayData.writeIndex;
            reader = arrayData.reader;
        } else {
            throw new IllegalArgumentException("data type " + data.getClass().getTypeName() + " is unsupported");
        }
        this.byteSequenceSupplier = Objects.requireNonNull(byteSequenceSupplier);
        writer = new WriterImpl();
    }

    /**
     * Get a new byte sequence from {@link #byteSequenceSupplier}
     *
     * @return newly obtained byte sequence
     */
    @NotNull
    private ByteSequence supplyNewByteSequence() {
        // no room left in array
        writeIndex += 1;
        if (writeIndex == byteSequences.length) {
            // increase array size by 2 times
            final var newLength = byteSequences.length * 2;
            byteSequences = Arrays.copyOf(byteSequences, newLength);
            limits = Arrays.copyOf(limits, newLength);
        }
        final var newMemory = byteSequenceSupplier.get();
        byteSequences[writeIndex] = newMemory;
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
        private ByteSequence byteSequence;

        /**
         * Writing index in the current {@link #byteSequence}
         */
        private long limit = 0L;

        /**
         * Capacity of the current {@link #byteSequence}
         */
        private long capacity;

        /**
         * Current byte sequence is the last in the data array of {@code ArrayData}
         */
        private WriterImpl() {
            byteSequence = Objects.requireNonNull(byteSequences[byteSequences.length - 1]);
            capacity = byteSequence.getCapacity();
        }

        @Override
        public void writeByte(byte value) throws EOFException {
            final var currentLimit = limit;
            final var byteSize = 1;
            final var targetLimit = currentLimit + byteSize;

            // 1) at least 1 byte left to write a byte in current memory
            if (capacity >= targetLimit) {
                limit = targetLimit;
                byteSequence.writeByteAt(currentLimit, value);
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
            byteSequence.writeByteAt(currentLimit, value);
        }

        @Override
        public void writeInt(int value) throws EOFException {
            final var currentLimit = limit;
            final var intSize = 4;
            final var targetLimit = currentLimit + intSize;

            // 1) at least 4 bytes left to write an int in current byte sequence
            if (capacity >= targetLimit) {
                limit = targetLimit;
                byteSequence.writeIntAt(currentLimit, value);
                return;
            }

            // 2) current byte sequence is exactly full
            if (currentLimit == capacity) {
                // let's add a new byte sequence from supplier
                addNewByteSequence();

                // we are at 0 index in newly obtained byte sequence
                if (capacity >= intSize) {
                    limit = intSize;
                    byteSequence.writeIntAt(currentLimit, value);
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
            byteSequence = supplyNewByteSequence();
            capacity = byteSequence.getCapacity();
            limit = 0L;
        }
    }
}
