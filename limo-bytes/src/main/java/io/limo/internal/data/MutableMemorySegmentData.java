/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.data;

import io.limo.bytes.*;
import io.limo.utils.MemorySegmentOps;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.nio.ByteOrder;

/**
 * Implementation of the immutable {@code Data} interface based on a {@link MemorySegment}
 *
 * @see Data
 * @see MutableBytesArrayData
 */
public final class MutableMemorySegmentData implements MutableData {

    final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final @NotNull MemorySegment segment;

    private final @NotNull MemoryAddress base;

    /**
     * Capacity of the {@link #segment}
     */
    private final @Range(from = 1, to = Long.MAX_VALUE) long capacity;

    /**
     * The limit of the {@link #segment}
     */
    private @Range(from = 0, to = Long.MAX_VALUE - 1) long limit;

    private boolean isBigEndian = true;

    /**
     * The data reader
     */
    private final @NotNull Reader reader;

    /**
     * The data writer
     */
    private final @NotNull Writer writer;

    /**
     * Build a Data from a new off-heap {@link MemorySegment}
     * <p> The byte order of a newly-created Bytes is always {@link ByteOrder#BIG_ENDIAN BIG_ENDIAN}. </p>
     *
     * @param capacity total capacity of the MemorySegment
     */
    public MutableMemorySegmentData(@Range(from = 1, to = Long.MAX_VALUE) long capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be > 0");
        }
        this.capacity = capacity;
        this.segment = MemorySegment.allocateNative(capacity);
        this.base = segment.baseAddress();
        this.limit = capacity;
        this.reader = new ReaderImpl();
        this.writer = new WriterImpl();
    }

    @Override
    public @Range(from = 1, to = Long.MAX_VALUE) long getByteSize() {
        return this.capacity;
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
    }

    @Override
    public @Range(from = 0, to = Long.MAX_VALUE - 1) long getLimit() {
        return this.limit;
    }

    /**
     * Closes the {@link MemorySegment}
     */
    @Override
    public void close() {
        this.segment.close();
        logger.debug("Closed MemorySegment");
    }

    @Override
    public @NotNull Writer getWriter() {
        return this.writer;
    }

    /**
     * Implementation of the {@code Reader} interface that reads in {@link #segment}
     */
    private final class ReaderImpl implements Reader {

        /**
         * Reading index in the memory segment
         */
        private long index = 0L;


        @Override
        public byte readByte() {
            final var currentIndex = this.index;
            final var byteSize = 1;
            final var targetLimit = currentIndex + byteSize;

            // 1) at least 1 byte left to read a byte in memory
            if (limit >= targetLimit) {
                this.index = targetLimit;
                return MemorySegmentOps.readByte(base.addOffset(currentIndex));
            }

            // 2) memory is exhausted
            throw new ReaderUnderflowException();
        }

        @Override
        public int readInt() {
            final var currentIndex = this.index;
            final var intSize = 4;
            final var targetLimit = currentIndex + intSize;

            // 1) at least 4 bytes left to read an int in memory
            if (limit >= targetLimit) {
                this.index = targetLimit;
                return MemorySegmentOps.readInt(base.addOffset(currentIndex), isBigEndian);
            }

            // 2) memory is exhausted
            throw new ReaderUnderflowException();
        }

        @Override
        public void close() {
            MutableMemorySegmentData.this.close();
        }
    }

    /**
     * Implementation of the {@code Writer} interface that writes in in {@link #segment}
     */
    private final class WriterImpl implements Writer {

        @Override
        public void writeByte(byte value) {
            final var currentLimit = limit;
            final var byteSize = 1;
            final var targetLimit = currentLimit + byteSize;

            // 1) at least 1 byte left to write a byte in current memory segment
            if (capacity >= targetLimit) {
                limit = targetLimit;
                MemorySegmentOps.writeByte(base.addOffset(currentLimit), value);
                return;
            }

            // 2) memory is exhausted
            throw new WriterOverflowException();
        }

        @Override
        public void writeInt(int value) {
            final var currentLimit = limit;
            final var intSize = 4;
            final var targetLimit = currentLimit + intSize;

            // 1) at least 4 bytes left to write an int in current memory segment
            if (capacity >= targetLimit) {
                limit = targetLimit;
                MemorySegmentOps.writeInt(base.addOffset(currentLimit), value, isBigEndian);
                return;
            }

            // 2) memory is exhausted
            throw new WriterOverflowException();
        }

        @Override
        public void close() {
            MutableMemorySegmentData.this.close();
        }
    }
}
