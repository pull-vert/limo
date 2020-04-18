/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.jdk14.memory;

import io.limo.jdk14.utils.MemorySegmentOps;
import io.limo.memory.AbstractOffHeap;
import io.limo.memory.ByBuOffHeap;
import io.limo.memory.OffHeap;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

import static io.limo.jdk14.utils.MemorySegmentOps.*;

/**
 * This class contains a native {@link MemorySegment}.
 */
final class MemorySegmentOffHeap extends AbstractOffHeap<OffHeap> {

    private final MemorySegment segment;
    private final MemoryAddress baseAddress;
    private final ByteBuffer baseByBu;

    MemorySegmentOffHeap(MemorySegment segment) {
        this(segment, getBaseByteBuffer(segment));
    }

    private MemorySegmentOffHeap(MemorySegment segment, ByteBuffer baseByBu) {
        super(baseByBu, segment.byteSize(), true);
        this.segment = segment;
        this.baseAddress = segment.baseAddress();
        this.baseByBu = baseByBu;
    }

    @Override
    protected byte[] toByteArrayNoIndexCheck() {
        return this.segment.toByteArray();
    }

    @Override
    protected byte readByteAtNoIndexCheck(long index) {
        return MemorySegmentOps.readByte(this.baseAddress.addOffset(index));
    }

    @Override
    protected int readIntAtNoIndexCheck(long index) {
        return MemorySegmentOps.readInt(this.baseAddress.addOffset(index));
    }

    @Override
    public long getByteSize() {
        return this.segment.byteSize();
    }

    @Override
    public @NotNull OffHeap slice(long offset, long length) {
        sliceIndexCheck(offset, length, getByteSize());
        return new MemorySegmentOffHeap(this.segment.asSlice(offset, length));
    }

    @Override
    public @NotNull ByBuOffHeap asByBuOffHeap() {
        if (getByteSize() > Integer.MAX_VALUE) {
            throw new UnsupportedOperationException(
                    String.format("ByteSize=%d of this memory is too big to export as a ByBuOffHeap", getByteSize()));
        }
        return new MemorySegmentByBuOffHeap(this.segment, this.baseByBu);
    }

    @Override
    public void close() {
        this.segment.close();
    }

    @Override
    protected void checkState() {
        checkStateForSegment(this.segment);
    }
}
