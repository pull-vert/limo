/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.jdk14.memory;

import io.limo.memory.AbstractByteBufferOffHeap;
import io.limo.memory.ByteBufferOffHeap;
import jdk.incubator.foreign.MemorySegment;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

/**
 * This class contains a native {@link MemorySegment} and the direct {@link ByteBuffer} linked to it
 * that both point to the same off-heap memory region.
 */
class ByteBufferOffHeapSegment extends AbstractByteBufferOffHeap {

    private final @NotNull MemorySegment segment;

    ByteBufferOffHeapSegment(@NotNull MemorySegment segment) {
        super(segment.asByteBuffer());
        this.segment = segment;
    }

    @Override
    public void close() {
        this.segment.close();
    }

    @Override
    protected ByteBufferOffHeap asSliceImpl(int offset, int length) {
        return new ByteBufferOffHeapSegment(this.segment.asSlice(offset, length));
    }
}
