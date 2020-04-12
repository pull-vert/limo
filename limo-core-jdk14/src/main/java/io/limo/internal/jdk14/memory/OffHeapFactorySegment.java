/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.jdk14.memory;

import io.limo.memory.ByteBufferOffHeap;
import io.limo.memory.OffHeap;
import io.limo.memory.OffHeapFactory;
import jdk.incubator.foreign.MemorySegment;
import org.jetbrains.annotations.NotNull;

public final class OffHeapFactorySegment implements OffHeapFactory {

    @Override
    public final @NotNull OffHeap newOffHeap(long byteSize) {
        if (byteSize > Integer.MAX_VALUE) {
            throw new IndexOutOfBoundsException("byteSize must be a positive Integer");
        }
        return newByteBufferOffHeap((int) byteSize);
    }

    @Override
    public final @NotNull ByteBufferOffHeap newByteBufferOffHeap(int byteSize) {
        final var segment = MemorySegment.allocateNative(byteSize);
        return new ByteBufferOffHeapSegment(segment, segment.asByteBuffer());
    }

    @Override
    public @NotNull ByteBufferOffHeap newByteBufferOffHeap(byte @NotNull [] bytes) {
        // create a new native MemorySegment with capacity equals to bytes length,
        // then extract its ByteBuffer and fill it with all bytes
        final var segment = MemorySegment.allocateNative(bytes.length);
        return new ByteBufferOffHeapSegment(segment, segment.asByteBuffer(), bytes);
    }

    @Override
    public final int getLoadPriority() {
        return 14;
    }
}
