/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.jdk14.memory;

import io.limo.memory.ByBuOffHeap;
import io.limo.memory.impl.SafeByBuOffHeap;
import jdk.incubator.foreign.MemorySegment;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

/**
 * This class contains a native {@link MemorySegment} of size < Integer.MAX_VALUE and the direct {@link ByteBuffer}
 * linked to it. They both point to the same off-heap memory region.
 */
final class MemorySegmentSafeByBuOffHeap extends SafeByBuOffHeap {

    private final MemorySegment segment;

    MemorySegmentSafeByBuOffHeap(MemorySegment segment) {
        this(segment, segment.asByteBuffer());
    }

    MemorySegmentSafeByBuOffHeap(MemorySegment segment, ByteBuffer bb) {
        super(bb);
        this.segment = segment;
    }

    MemorySegmentSafeByBuOffHeap(MemorySegment segment, ByteBuffer bb, byte[] bytes) {
        super(bb, bytes);
        this.segment = segment;
    }

    @Override
    public final @NotNull ByBuOffHeap slice(long offset, long length) {
        sliceIndexCheck(offset, length, getByteSize());
        return new MemorySegmentSafeByBuOffHeap(this.segment.asSlice(offset, length));
    }

    @Override
    public final void close() {
        this.segment.close();
    }
}
