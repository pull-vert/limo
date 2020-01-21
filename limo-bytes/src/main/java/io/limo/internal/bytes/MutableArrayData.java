/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.bytes;

import io.limo.bytes.MutableData;
import io.limo.bytes.Reader;
import io.limo.bytes.Writer;
import io.limo.internal.bytes.memory.Memory;
import io.limo.internal.bytes.memory.MemorySupplier;
import org.jetbrains.annotations.NotNull;

import java.io.EOFException;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Objects;
import java.util.OptionalInt;

/**
 * Resizable-array implementation of the {@code Data} interface.
 *
 * @implNote Inspired by ArrayList
 * @see MutableData
 */
public final class MutableArrayData implements MutableData {

    /**
     * Default initial capacity.
     */
    private static final int DEFAULT_CAPACITY = 4;

    /**
     * The array buffer into which the elements of the ArrayData are stored
     */
    @NotNull
    private Memory[] data;

    /**
     * The array of limits for each memory
     */
    private long[] limits;

    /**
     * Index of the memory in data that is currently read
     */
    private int readIndex = 0;

    /**
     * Max index of the memory in data that has been written
     *
     * @implNote It has a 0 initial value, even if first memory was not written
     */
    private int writeIndex = 0;

    /**
     * The memory supplier, can act as a pool
     */
    @NotNull
    private MemorySupplier memorySupplier;

    boolean isBigEndian = true;

    /**
     * The data reader
     */
    @NotNull
    private final Reader reader;

    /**
     * The data writer
     */
    @NotNull
    private final Writer writer;

    public MutableArrayData(@NotNull MemorySupplier memorySupplier) {
        this.memorySupplier = Objects.requireNonNull(memorySupplier);
        final var initialMemory = memorySupplier.get();
        // init data with DEFAULT_CAPACITY size and first element in data = initialMemory
        data = new Memory[DEFAULT_CAPACITY];
        data[0] = initialMemory;
        limits = new long[DEFAULT_CAPACITY];
        reader = new ReaderImpl();
        writer = new WriterImpl();
    }

    public MutableArrayData(@NotNull MutableData data, @NotNull MemorySupplier memorySupplier) {
        Objects.requireNonNull(data);
        // todo use instanceof pattern matching of java 14 https://openjdk.java.net/jeps/305
        if (data instanceof MutableArrayData) {
            final var arrayData = (MutableArrayData) data;
            Objects.requireNonNull(arrayData.data);
            if (arrayData.data.length == 0) {
                throw new IllegalArgumentException("data array must not be empty");
            }
            this.data = arrayData.data;
            limits = Objects.requireNonNull(arrayData.limits);
        } else {
            throw new IllegalArgumentException("data type " + data.getClass().getTypeName() + " is unsupported");
        }
        this.memorySupplier = Objects.requireNonNull(memorySupplier);
        reader = new ReaderImpl();
        writer = new WriterImpl();
    }

    /**
     * @return next not empty chunk of memory, or empty if none exists
     */
    @NotNull
    private OptionalInt getNextReadIndex() {
        if (readIndex < writeIndex) {
            return OptionalInt.of(++readIndex);
        }
        return OptionalInt.empty();
    }

    /**
     * Get a new Memory from {@link #memorySupplier}
     *
     * @return new Memory
     */
    @NotNull
    private Memory supplyNewMemory() {
        // no room left in array
        if (writeIndex == data.length) {
            // increase array size by 2 times
            final var newLength = data.length * 2;
            data = Arrays.copyOf(data, newLength);
            limits = Arrays.copyOf(limits, newLength);
        }
        final var newMemory = memorySupplier.get();
        data[++writeIndex] = newMemory;
        return newMemory;
    }

    @NotNull
    @Override
    public Reader getReader() {
        return reader;
    }

    @NotNull
    @Override
    public Writer getWriter() {
        return writer;
    }

    @NotNull
    @Override
    public ByteOrder getByteOrder() {
        return isBigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
    }

    @Override
    public void close() {
        for (final var memory : data) {
            memory.close();
        }
    }

    /**
     * Implementation of the {@code Reader} interface that reads in data array of {@code ArrayData}
     */
    private final class ReaderImpl implements Reader {

        /**
         * Current Memory chunk to read from
         */
        @NotNull
        private Memory memory;

        /**
         * Reading index in the current {@link #memory}
         */
        private long index = 0L;

        /**
         * Number of bytes loaded in the current {@link #memory}
         */
        private long limit;

        /**
         * Current memory is the first in the data array of {@code ArrayData}
         */
        private ReaderImpl() {
            memory = Objects.requireNonNull(data[0]);
            limit = limits[0];
        }

        @Override
        public byte readByte() throws EOFException {
            final var currentIndex = index;
            final var currentLimit = limit;
            final var byteSize = 1;
            final var targetLimit = currentIndex + byteSize;

            // 1) at least 1 byte left to read a byte in current memory
            if (currentLimit >= targetLimit) {
                index = targetLimit;
                return memory.readByteAt(currentIndex);
            }

            // 2) current memory is exactly exhausted
            // let's get next chunk of data and if present read it
            nextMemory();

            // we are at 0 index in newly obtained memory

            if (limit >= byteSize) {
                index = byteSize;
                return memory.readByteAt(0);
            }
            throw new EOFException("End of file while reading memory");
        }

        @Override
        public int readInt() throws EOFException {
            final var currentIndex = index;
            final var currentLimit = limit;
            final var intSize = 4;
            final var targetLimit = currentIndex + intSize;

            // 1) at least 4 bytes left to read an int in current memory
            if (currentLimit >= targetLimit) {
                index = targetLimit;
                return memory.readIntAt(currentIndex);
            }

            // 2) current memory is exactly exhausted
            if (currentLimit == currentIndex) {
                // let's get next chunk of data and if present read it
                nextMemory();

                // we are at 0 index in newly obtained memory

                if (limit >= intSize) {
                    index = intSize;
                    return memory.readIntAt(0);
                }
                throw new EOFException("End of file while reading memory");
            }

            // 3) must read some bytes in current chunk, some others from next one
            return BytesOps.bytesToInt(readByte(), readByte(), readByte(), readByte(), isBigEndian);
        }

        /**
         * Switch to next memory chunk because current memory is exhausted
         *
         * @throws EOFException if no readable next memory
         */
        private void nextMemory() throws EOFException {
            final var nextReadIndex = getNextReadIndex().orElseThrow(() -> new EOFException("End of file while reading memory"));
            memory = Objects.requireNonNull(data[nextReadIndex]);
            limit = limits[nextReadIndex];
        }

        @Override
        public void close() {
            MutableArrayData.this.close();
        }
    }

    /**
     * Implementation of the {@code Writer} interface that writes in data array of {@code ArrayData}
     */
    private final class WriterImpl implements Writer {

        /**
         * Current Memory chunk to write in
         */
        @NotNull
        private Memory memory;

        /**
         * Writing index in the current {@link #memory}
         */
        private long limit = 0L;

        /**
         * Capacity of the current {@link #memory}
         */
        private long capacity;

        /**
         * Current memory is the last in the data array of {@code ArrayData}
         */
        private WriterImpl() {
            memory = Objects.requireNonNull(data[data.length - 1]);
            capacity = memory.getCapacity();
        }

        @Override
        public void writeByte(byte value) throws EOFException {
            final var currentLimit = limit;
            final var byteSize = 1;
            final var targetLimit = currentLimit + byteSize;

            // 1) at least 1 byte left to write a byte in current memory
            if (capacity >= targetLimit) {
                limit = targetLimit;
                memory.writeByteAt(currentLimit, value);
                return;
            }

            // 2) current memory is exactly full
            // let's add a new chunk of data from supplier
            addNewMemory();

            // we are at 0 index in newly obtained memory

            if (capacity < byteSize) {
                throw new EOFException("Empty memory, no room for writing a byte");
            }
            limit = byteSize;
            memory.writeByteAt(currentLimit, value);
        }

        @Override
        public void writeInt(int value) throws EOFException {
            final var currentLimit = limit;
            final var intSize = 4;
            final var targetLimit = currentLimit + intSize;

            // 1) at least 4 bytes left to write an int in current memory
            if (capacity >= targetLimit) {
                limit = targetLimit;
                memory.writeIntAt(currentLimit, value);
                return;
            }

            // 2) current memory is exactly full
            if (currentLimit == capacity) {
                // let's add a new chunk of data from supplier
                addNewMemory();

                // we are at 0 index in newly obtained memory

                if (capacity >= intSize) {
                    limit = intSize;
                    memory.writeIntAt(currentLimit, value);
                    return;
                }
                throw new EOFException("Memory too small, no room for writing an int");
            }

            // 3) must write some bytes in current chunk, some others in next one
            for (final var b : BytesOps.intToBytes(value, isBigEndian)) {
                writeByte(b);
            }
        }

        @Override
        public void close() {
            MutableArrayData.this.close();
        }

        /**
         * Current memory is full, add a new Memory in data array
         */
        private void addNewMemory() {
            memory = supplyNewMemory();
            capacity = memory.getCapacity();
            limit = 0L;
        }
    }
}
