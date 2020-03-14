/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.bytes;

import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemoryHandles;
import jdk.incubator.foreign.MemorySegment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.nio.ByteOrder;

/**
 * A byte sequence based on a {@code ByteBuffer}
 */
public final class MemorySegmentByteSequence implements ByteSequence {

    final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    private static final VarHandle BYTE_HANDLE = MemoryHandles.varHandle(byte.class, ByteOrder.BIG_ENDIAN);
    private static final VarHandle INT_HANDLE_BIG_ENDIAN = MemoryHandles.varHandle(int.class, ByteOrder.BIG_ENDIAN);
    private static final VarHandle LONG_HANDLE_BIG_ENDIAN = MemoryHandles.varHandle(long.class, ByteOrder.BIG_ENDIAN);

    private static final VarHandle INT_HANDLE_LITTLE_ENDIAN = MemoryHandles.varHandle(int.class, ByteOrder.LITTLE_ENDIAN);
    private static final VarHandle LONG_HANDLE_LITTLE_ENDIAN = MemoryHandles.varHandle(long.class, ByteOrder.LITTLE_ENDIAN);

    @NotNull
    private final MemorySegment segment;
    @NotNull
    private final MemoryAddress base;

    private VarHandle byteHandle;
    private VarHandle intHandle;
    private VarHandle longHandle;

    /**
     * Build a byte sequence from a fresh {@link MemorySegment}
     * <p> The byte order of a newly-created ByteSequence is always {@link ByteOrder#BIG_ENDIAN BIG_ENDIAN}. </p>
     *
     * @param capacity total capacity of the MemorySegment
     */
    public MemorySegmentByteSequence(@Range(from = 1, to = Long.MAX_VALUE) long capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be > 0");
        }
        this.segment = MemorySegment.allocateNative(capacity);
        this.base = segment.baseAddress();
        byteHandle = BYTE_HANDLE;
        intHandle = INT_HANDLE_BIG_ENDIAN;
        longHandle = LONG_HANDLE_BIG_ENDIAN;
    }

    @Override
    public byte readByteAt(@Range(from = 0, to = Long.MAX_VALUE) long index) {
        return (byte) byteHandle.get(base.addOffset(index));
    }

    @Override
    public int readIntAt(@Range(from = 0, to = Long.MAX_VALUE) long index) {
        return (int) intHandle.get(base.addOffset(index));
    }

    @Override
    public void writeByteAt(@Range(from = 0, to = Long.MAX_VALUE) long index, byte value) {
        byteHandle.set(base.addOffset(index), value);
    }

    @Override
    public void writeIntAt(@Range(from = 0, to = Long.MAX_VALUE) long index, int value) {
        final var mem = base.addOffset(index);
        logger.atInfo().log("write int, previous address {}, new address {}", base.offset(), mem.offset());
        intHandle.set(mem, value);
    }

    @Override
    @Range(from = 0, to = Long.MAX_VALUE)
    public long getCapacity() {
        return segment.byteSize();
    }

    @Override
    public void setByteOrder(@NotNull ByteOrder byteOrder) {
        if (byteOrder == ByteOrder.BIG_ENDIAN) {
            intHandle = INT_HANDLE_BIG_ENDIAN;
            longHandle = LONG_HANDLE_BIG_ENDIAN;
        } else {
            intHandle = INT_HANDLE_LITTLE_ENDIAN;
            longHandle = LONG_HANDLE_LITTLE_ENDIAN;
        }
    }

    /**
     * Closes the {@link MemorySegment}
     */
    @Override
    public void close() {
        segment.close();
    }
}
