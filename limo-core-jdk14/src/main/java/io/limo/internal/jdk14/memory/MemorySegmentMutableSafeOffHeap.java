/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.jdk14.memory;

import io.limo.jdk14.utils.MemorySegmentOps;
import io.limo.memory.AbstractOffHeap;
import io.limo.memory.MutableByBuOffHeap;
import io.limo.memory.MutableOffHeap;
import io.limo.memory.OffHeap;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import org.jetbrains.annotations.NotNull;

/**
 * This class contains a native {@link MemorySegment}.
 */
final class MemorySegmentMutableSafeOffHeap extends AbstractOffHeap implements MutableOffHeap {

    private final MemorySegment segment;
    private final MemoryAddress baseAddress;

    MemorySegmentMutableSafeOffHeap(MemorySegment segment) {
        this.segment = segment;
        this.baseAddress = segment.baseAddress();
    }

    @Override
    public final long getByteSize() {
        return this.segment.byteSize();
    }

    @Override
    public @NotNull OffHeap asReadOnly() {
        return null; // todo
    }

    @Override
    public final @NotNull MutableOffHeap slice(long offset, long length) {
        sliceIndexCheck(offset, length, getByteSize());
        return new MemorySegmentMutableSafeOffHeap(this.segment.asSlice(offset, length));
    }

    @Override
    public final @NotNull MutableByBuOffHeap asByBuOffHeap() {
        if (getByteSize() > Integer.MAX_VALUE) {
            throw new UnsupportedOperationException(
                    String.format("ByteSize=%d of this memory is too big to export as a ByBuOffHeap", getByteSize()));
        }
        return new MemorySegmentMutableSafeByBuOffHeap(this.segment);
    }

    @Override
    public final void close() {
        this.segment.close();
    }

    @Override
    public byte readByteAt(long index) {
        return MemorySegmentOps.readByte(this.baseAddress.addOffset(index));
    }

    @Override
    public int readIntAt(long index) {
        return MemorySegmentOps.readInt(this.baseAddress.addOffset(index));
    }

    @Override
    public int readIntAtLE(long index) {
        return MemorySegmentOps.readIntLE(this.baseAddress.addOffset(index));
    }

    @Override
    public void writeByteAt(long index, byte value) {
        MemorySegmentOps.writeByte(this.baseAddress.addOffset(index), value);
    }

    @Override
    public void writeIntAt(long index, int value) {
        MemorySegmentOps.writeInt(this.baseAddress.addOffset(index), value);
    }

    @Override
    public void writeIntAtLE(long index, int value) {
        MemorySegmentOps.writeIntLE(this.baseAddress.addOffset(index), value);
    }

    @Override
    protected byte[] toByteArrayNoIndexCheck() {
        return this.segment.toByteArray();
    }
}
