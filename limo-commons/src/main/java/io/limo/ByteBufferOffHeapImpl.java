/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo;

import io.limo.memory.AbstractByteBufferOffHeap;
import io.limo.memory.ByteBufferOffHeap;
import jdk.incubator.foreign.MemorySegment;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

/**
 * This class contains a native {@link MemorySegment} and the direct {@link ByteBuffer} linked to it
 * that both point to the same off-heap memory region.
 */
public class ByteBufferOffHeapImpl extends AbstractByteBufferOffHeap {

    private final @NotNull MemorySegment segment;

    private ByteBufferOffHeapImpl(@NotNull MemorySegment segment) {
        super(segment.asByteBuffer());
        this.segment = segment;
    }

    @Override
    public void close() {
        this.segment.close();
    }

    @Override
    protected ByteBufferOffHeap asSliceImpl(int offset, int length) {
        return new ByteBufferOffHeapImpl(this.segment.asSlice(offset, length));
    }
}
