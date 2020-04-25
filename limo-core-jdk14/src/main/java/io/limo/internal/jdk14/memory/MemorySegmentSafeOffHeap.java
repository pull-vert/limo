/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.jdk14.memory;

import io.limo.jdk14.utils.MemorySegmentOps;
import io.limo.memory.ByBuOffHeap;
import io.limo.memory.OffHeap;
import io.limo.memory.impl.AbstractOffHeap;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import org.jetbrains.annotations.NotNull;

/**
 * This class contains a native {@link MemorySegment}.
 */
class MemorySegmentSafeOffHeap extends AbstractOffHeap {

    final MemorySegment segment;
    final MemoryAddress baseAddress;

    MemorySegmentSafeOffHeap(MemorySegment segment) {
        this.segment = segment;
        this.baseAddress = segment.baseAddress();
    }

    @Override
    public final long getByteSize() {
        return this.segment.byteSize();
    }

    @Override
    public @NotNull OffHeap slice(long offset, long length) {
        sliceIndexCheck(offset, length, getByteSize());
        return new MemorySegmentSafeOffHeap(this.segment.asSlice(offset, length));
    }

    @Override
    public @NotNull ByBuOffHeap asByBuOffHeap() {
        if (getByteSize() > Integer.MAX_VALUE) {
            throw new UnsupportedOperationException(
                    String.format("ByteSize=%d of this memory is too big to export as a ByBuOffHeap", getByteSize()));
        }
        return new MemorySegmentSafeByBuOffHeap(this.segment);
    }

    @Override
    public final void close() {
        this.segment.close();
    }

    @Override
    public final byte readByteAt(long index) {
        return MemorySegmentOps.readByte(this.baseAddress.addOffset(index));
    }

    @Override
    public final int readIntAt(long index) {
        return MemorySegmentOps.readInt(this.baseAddress.addOffset(index));
    }

    @Override
    public final int readIntAtLE(long index) {
        return MemorySegmentOps.readIntLE(this.baseAddress.addOffset(index));
    }

    @Override
    protected final byte[] toByteArrayNoIndexCheck() {
        return this.segment.toByteArray();
    }
}
