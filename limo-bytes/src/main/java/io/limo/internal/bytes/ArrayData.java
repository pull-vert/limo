/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.bytes;

import io.limo.bytes.Data;
import io.limo.bytes.Reader;
import org.jetbrains.annotations.NotNull;

import java.io.EOFException;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;

/**
 * Implementation of the immutable {@code Data} interface based on a fixed size array of byte sequences
 *
 * @see Data
 * @see MutableArrayData
 */
public class ArrayData implements Data {

    /**
     * Default initial capacity.
     */
    private static final int DEFAULT_CAPACITY = 4;

    /**
     * The array of memories into which the elements of the ArrayData are stored
     */
    ByteSequence @NotNull [] byteSequences;

    /**
     * The array of limits for each memory
     */
    long @NotNull [] limits;

    /**
     * Index of the memory in data array that is currently read
     */
    int readIndex = 0;

    /**
     * Last index of the memory in data array that has been written
     *
     * @implNote It has a 0 initial value, even if first memory was not written
     */
    int writeIndex = 0;

    boolean isBigEndian = true;

    /**
     * The data reader
     */
    @NotNull
    Reader reader;

    public ArrayData() {
        // init memories and limits with DEFAULT_CAPACITY size
        byteSequences = new ByteSequence[DEFAULT_CAPACITY];
        limits = new long[DEFAULT_CAPACITY];
        reader = new ReaderImpl();
    }

    public ArrayData(ByteSequence @NotNull [] byteSequences, long @NotNull [] limits) {
        this.byteSequences = Objects.requireNonNull(byteSequences);
        this.limits = Objects.requireNonNull(limits);
        writeIndex = limits.length;

        reader = new ReaderImpl();
    }

    public ArrayData(@NotNull Data first, Data @NotNull ... rest) {
        Objects.requireNonNull(first);
        Objects.requireNonNull(rest);

        // must get total length
        var totalLength = 0;
        // todo use instanceof pattern matching of java 14 https://openjdk.java.net/jeps/305
        for (final var data : rest) {
            if (data instanceof ArrayData) {
                totalLength += ((ArrayData) data).writeIndex + 1;
            }
        }

        // initiate arrays
        var offset = 0;
        if (first instanceof ArrayData) {
            final var firstArrayData = (ArrayData) first;
            offset = firstArrayData.writeIndex + 1;
            totalLength += offset;
            byteSequences = Arrays.copyOf(firstArrayData.byteSequences, totalLength);
            limits = Arrays.copyOf(firstArrayData.limits, totalLength);
        } else {
            throw new IllegalArgumentException("data type " + first.getClass().getTypeName() + " is unsupported");
        }
        writeIndex = totalLength;

        int dataLength;
        for (final var data : rest) {
            if (data instanceof ArrayData) {
                final var arrayData = (ArrayData) data;
                dataLength = arrayData.writeIndex + 1;
                System.arraycopy(arrayData.byteSequences, 0, byteSequences, offset, dataLength);
                System.arraycopy(arrayData.limits, 0, limits, offset, dataLength);
                offset += dataLength;
            }
        }

        reader = new ReaderImpl();
    }

    /**
     * @return next not empty byte sequence index from array, or empty if none exists
     */
    @NotNull
    private OptionalInt getNextReadIndex() {
        if (readIndex < writeIndex) {
            return OptionalInt.of(++readIndex);
        }
        return OptionalInt.empty();
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
        // affect this byte order to all memories
        for (final var memory : byteSequences) {
            Optional.ofNullable(memory).ifPresent(mem -> mem.setByteOrder(byteOrder));
        }
    }

    @Override
    public void close() {
        for (final var memory : byteSequences) {
            Optional.ofNullable(memory).ifPresent(ByteSequence::close);
        }
    }

    /**
     * Implementation of the {@code Reader} interface that reads in data array of {@code ArrayData}
     */
    private final class ReaderImpl implements Reader {

        /**
         * Current byte sequence to read from
         */
        @NotNull
        private ByteSequence byteSequence;

        /**
         * Reading index in the current {@link #byteSequence}
         */
        private long index = 0L;

        /**
         * Number of bytes loaded in the current {@link #byteSequence}
         */
        private long limit;

        /**
         * Current byte sequence is the first in the data array of {@code ArrayData}
         */
        private ReaderImpl() {
            byteSequence = Objects.requireNonNull(byteSequences[0]);
            limit = limits[0];
        }

        @Override
        public byte readByte() throws EOFException {
            final var currentIndex = index;
            final var currentLimit = limit;
            final var byteSize = 1;
            final var targetLimit = currentIndex + byteSize;

            // 1) at least 1 byte left to read a byte in current byte sequence
            if (currentLimit >= targetLimit) {
                index = targetLimit;
                return byteSequence.readByteAt(currentIndex);
            }

            // 2) current byte sequence is exactly exhausted
            // let's get next byte sequence and if present read it
            nextByteSequence();

            // we are at 0 index in newly obtained byte sequence

            if (limit >= byteSize) {
                index = byteSize;
                return byteSequence.readByteAt(0);
            }
            throw new EOFException("End of file while reading byte sequence");
        }

        @Override
        public int readInt() throws EOFException {
            final var currentIndex = index;
            final var currentLimit = limit;
            final var intSize = 4;
            final var targetLimit = currentIndex + intSize;

            // 1) at least 4 bytes left to read an int in current byte sequence
            if (currentLimit >= targetLimit) {
                index = targetLimit;
                return byteSequence.readIntAt(currentIndex);
            }

            // 2) current byte sequence is exactly exhausted
            if (currentLimit == currentIndex) {
                // let's get next byte sequence and if present read it
                nextByteSequence();

                // we are at 0 index in newly obtained byte sequence

                if (limit >= intSize) {
                    index = intSize;
                    return byteSequence.readIntAt(0);
                }
                throw new EOFException("End of file while reading byte sequence");
            }

            // 3) must read some bytes in current byte sequence, some others from next one
            return BytesOps.bytesToInt(readByte(), readByte(), readByte(), readByte(), isBigEndian);
        }

        /**
         * Switch to next byte sequence because current one is exhausted
         *
         * @throws EOFException if no readable next byte sequence
         */
        private void nextByteSequence() throws EOFException {
            final var nextReadIndex = getNextReadIndex().orElseThrow(() -> new EOFException("End of file while reading byte sequence"));
            byteSequence = Objects.requireNonNull(byteSequences[nextReadIndex]);
            limit = limits[nextReadIndex];
        }

        @Override
        public void close() {
            ArrayData.this.close();
        }
    }
}
