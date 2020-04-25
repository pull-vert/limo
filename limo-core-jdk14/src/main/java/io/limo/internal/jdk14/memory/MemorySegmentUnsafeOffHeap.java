/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.jdk14.memory;

import io.limo.jdk14.utils.MemorySegmentOps;
import io.limo.memory.ByBuOffHeap;
import io.limo.memory.OffHeap;
import io.limo.memory.impl.UnsafeOffHeap;
import jdk.incubator.foreign.MemorySegment;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

/**
 * This class contains a native {@link MemorySegment}.
 */
final class MemorySegmentUnsafeOffHeap extends UnsafeOffHeap {

    private final MemorySegment segment;
    private final ByteBuffer baseByBu;

    MemorySegmentUnsafeOffHeap(MemorySegment segment) {
        this(segment, MemorySegmentOps.getBaseByteBuffer(segment));
    }

    private MemorySegmentUnsafeOffHeap(MemorySegment segment, ByteBuffer baseByBu) {
        super(baseByBu);
        this.segment = segment;
        this.baseByBu = baseByBu;
    }

    @Override
    public final long getByteSize() {
        return this.segment.byteSize();
    }

    @Override
    public final @NotNull OffHeap slice(long offset, long length) {
        sliceIndexCheck(offset, length, getByteSize());
        return new MemorySegmentUnsafeOffHeap(this.segment.asSlice(offset, length));
    }

    @Override
    public final @NotNull ByBuOffHeap asByBuOffHeap() {
        if (getByteSize() > Integer.MAX_VALUE) {
            throw new UnsupportedOperationException(
                    String.format("ByteSize=%d of this memory is too big to export as a ByBuOffHeap", getByteSize()));
        }
        return new MemorySegmentUnsafeByBuOffHeap(this.segment, this.baseByBu);
    }

    @Override
    public final void close() {
        this.segment.close();
    }

    @Override
    protected final void checkState() {
        MemorySegmentOps.checkStateForSegment(this.segment);
    }
}
