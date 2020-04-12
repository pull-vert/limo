/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.jdk14.memory;

import io.limo.memory.AbstractByteBufferOffHeap;
import io.limo.memory.ByteBufferOffHeap;
import jdk.incubator.foreign.MemorySegment;

import java.nio.ByteBuffer;

/**
 * This class contains a native {@link MemorySegment} and the direct {@link ByteBuffer} linked to it
 * that both point to the same off-heap memory region.
 */
class ByteBufferOffHeapSegment extends AbstractByteBufferOffHeap {

    private final MemorySegment segment;

    ByteBufferOffHeapSegment(MemorySegment segment, ByteBuffer bb) {
        super(bb);
        this.segment = segment;
    }

    @Override
    public void close() {
        this.segment.close();
    }

    @Override
    protected ByteBufferOffHeap sliceNoIndexCheck(long offset, int length) {
        final var segment = this.segment.asSlice(offset, length);
        return new ByteBufferOffHeapSegment(segment, segment.asByteBuffer());
    }

    @Override
    protected byte readByteAtNoIndexCheck(long index) {
        return getByteBuffer().get((int) index);
    }

    @Override
    protected int readIntAtNoIndexCheck(long index) {
        return getByteBuffer().getInt((int) index);
    }
}
