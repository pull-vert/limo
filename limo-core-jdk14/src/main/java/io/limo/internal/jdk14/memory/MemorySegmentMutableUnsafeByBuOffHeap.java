/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.jdk14.memory;

import io.limo.memory.ByBuOffHeap;
import io.limo.memory.MutableByBuOffHeap;
import io.limo.memory.MutableUnsafeByBuOffHeap;
import jdk.incubator.foreign.MemorySegment;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

import static io.limo.jdk14.utils.MemorySegmentOps.checkStateForSegment;

/**
 * This class contains a native {@link MemorySegment} of size < Integer.MAX_VALUE and the direct {@link ByteBuffer}
 * linked to it. They both point to the same off-heap memory region.
 */
final class MemorySegmentMutableUnsafeByBuOffHeap extends MutableUnsafeByBuOffHeap {

    private final MemorySegment segment;

    MemorySegmentMutableUnsafeByBuOffHeap(MemorySegment segment) {
        this(segment, segment.asByteBuffer());
    }

    MemorySegmentMutableUnsafeByBuOffHeap(MemorySegment segment, ByteBuffer bb) {
        super(bb);
        this.segment = segment;
    }

    /*MemorySegmentMutableUnsafeByBuOffHeap(MemorySegment segment, ByteBuffer bb, byte[] bytes) {
        super(bb, bytes);
        this.segment = segment;
    }*/

    @Override
    public @NotNull ByBuOffHeap asReadOnly() {
        return null; // todo
    }

    @Override
    public @NotNull MutableByBuOffHeap slice(long offset, long length) {
        sliceIndexCheck(offset, length, getByteSize());
        return new MemorySegmentMutableUnsafeByBuOffHeap(this.segment.asSlice(offset, length));
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
