/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.jdk14.memory;

import io.limo.memory.ByBuOffHeap;
import io.limo.memory.MutableByBuOffHeap;
import io.limo.memory.impl.MutableSafeByBuOffHeap;
import jdk.incubator.foreign.MemorySegment;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

/**
 * This class contains a native {@link MemorySegment} of size < Integer.MAX_VALUE and the direct {@link ByteBuffer}
 * linked to it. They both point to the same off-heap memory region.
 */
final class MemorySegmentMutableSafeByBuOffHeap extends MutableSafeByBuOffHeap {

    private final MemorySegment segment;

    MemorySegmentMutableSafeByBuOffHeap(MemorySegment segment) {
        this(segment, segment.asByteBuffer());
    }

    MemorySegmentMutableSafeByBuOffHeap(MemorySegment segment, ByteBuffer bb) {
        super(bb);
        this.segment = segment;
    }

    @Override
    public final @NotNull ByBuOffHeap asReadOnly() {
        return new MemorySegmentSafeByBuOffHeap(this.segment, this.getByteBuffer());
    }

    @Override
    public final @NotNull MutableByBuOffHeap slice(long offset, long length) {
        sliceIndexCheck(offset, length, getByteSize());
        return new MemorySegmentMutableSafeByBuOffHeap(this.segment.asSlice(offset, length));
    }

    @Override
    public final void close() {
        this.segment.close();
    }
}
