/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.bytes;

import io.limo.bytes.Data;
import io.limo.bytes.Reader;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemoryHandles;
import jdk.incubator.foreign.MemoryLayout;
import jdk.incubator.foreign.MemoryLayouts;
import jdk.incubator.foreign.MemorySegment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.EOFException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.nio.ByteOrder;
import java.util.Objects;

/**
 * Implementation of the immutable {@code Data} interface based on a {@link MemorySegment}
 *
 * @see Data
 * @see MutableArrayData
 */
public final class MemorySegmentData implements Data {

    final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final VarHandle BYTE_HANDLE = MemoryHandles.varHandle(byte.class, ByteOrder.BIG_ENDIAN);

    private static final VarHandle INT_AS_BYTE_SEQ_HANDLE = MemoryLayout.ofSequence(4, MemoryLayouts.BITS_8_BE)
        .varHandle(byte.class, MemoryLayout.PathElement.sequenceElement());

    @NotNull
    private final MemorySegment segment;

    @NotNull
    private final MemoryAddress base;

    /**
     * The limit of memory
     */
    private final long limit;

    private boolean isBigEndian;

    /**
     * The data reader
     */
    @NotNull
    private final Reader reader;

    /**
     * Build a byte sequence from a fresh {@link MemorySegment}
     * <p> The byte order of a newly-created ByteSequence is always {@link ByteOrder#BIG_ENDIAN BIG_ENDIAN}. </p>
     *
     * @param capacity total capacity of the MemorySegment
     */
    public MemorySegmentData(@Range(from = 1, to = Long.MAX_VALUE) long capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be > 0");
        }
        this.segment = MemorySegment.allocateNative(capacity);
        this.base = segment.baseAddress();
        this.limit = capacity;
        this.isBigEndian = true;
        this.reader = new ReaderImpl();
    }

    /**
     * Build a byte sequence from a fresh {@link MemorySegment} built from a byte array
     * <p> The byte order of a newly-created ByteSequence is always {@link ByteOrder#BIG_ENDIAN BIG_ENDIAN}. </p>
     *
     * @param bytes the by array
     */
    public MemorySegmentData(byte @NotNull [] bytes) {
        this.segment = MemorySegment.ofArray(Objects.requireNonNull(bytes));
        this.base = segment.baseAddress();
        this.limit = bytes.length;
        this.isBigEndian = true;
        this.reader = new ReaderImpl();
    }

//    @Override
//    public void writeByteAt(@Range(from = 0, to = Long.MAX_VALUE - 1) long index, byte value) {
//        BYTE_HANDLE.set(base.addOffset(index), value);
//    }
//
//    @Override
//    public void writeIntAt(@Range(from = 0, to = Long.MAX_VALUE - 1) long index, int value) {
//        final var bytes = BytesOps.intToBytes(value, isBigEndian);
//        final var address = base.addOffset(index);
//        for (var i = 0; i < 4; i++) {
//            INT_AS_BYTE_SEQ_HANDLE.set(address, (long) i, bytes[i]);
//        }
//    }

    @Range(from = 1, to = Long.MAX_VALUE)
    @Override
    public long getByteSize() {
        return segment.byteSize();
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

    /**
     * Closes the {@link MemorySegment}
     */
    @Override
    public void close() {
        segment.close();
        logger.atDebug().log("Closed MemorySegment");
    }

    /**
     * Implementation of the {@code Reader} interface that reads in {@link #segment}
     */
    private final class ReaderImpl implements Reader {

        /**
         * Reading index in the memory
         */
        private long index = 0L;


        @Override
        public byte readByte() throws EOFException {
            final var currentIndex = index;
            final var byteSize = 1;
            final var targetLimit = currentIndex + byteSize;

            // 1) at least 1 byte left to read a byte in memory
            if (limit >= targetLimit) {
                index = targetLimit;
                return (byte) BYTE_HANDLE.get(base.addOffset(currentIndex));
            }

            // 2) memory is exhausted
            throw new EOFException("End of file while reading memory");
        }

        @Override
        public int readInt() throws EOFException {
            final var currentIndex = index;
            final var intSize = 4;
            final var targetLimit = currentIndex + intSize;

            // 1) at least 4 bytes left to read an int in memory
            if (limit >= targetLimit) {
                index = targetLimit;
                final var address = base.addOffset(currentIndex);
                return BytesOps.bytesToInt(
                    (byte) INT_AS_BYTE_SEQ_HANDLE.get(address, 0L),
                    (byte) INT_AS_BYTE_SEQ_HANDLE.get(address, 1L),
                    (byte) INT_AS_BYTE_SEQ_HANDLE.get(address, 2L),
                    (byte) INT_AS_BYTE_SEQ_HANDLE.get(address, 3L),
                    isBigEndian
                );
            }

            // 2) memory is exhausted
            throw new EOFException("End of file while reading memory");
        }

        @Override
        public void close() {
            MemorySegmentData.this.close();
        }
    }
}
