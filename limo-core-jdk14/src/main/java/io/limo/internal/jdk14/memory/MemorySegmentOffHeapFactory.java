/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.jdk14.memory;

import io.limo.memory.ByBuOffHeap;
import io.limo.memory.OffHeap;
import io.limo.memory.OffHeapFactory;
import jdk.incubator.foreign.MemorySegment;
import org.jetbrains.annotations.NotNull;

public final class MemorySegmentOffHeapFactory implements OffHeapFactory {

    @Override
    public final @NotNull OffHeap newOffHeap(long byteSize) {
        if (byteSize > Integer.MAX_VALUE) {
            throw new IndexOutOfBoundsException("byteSize must be a positive Integer");
        }
        return newByteBufferOffHeap((int) byteSize);
    }

    @Override
    public final @NotNull ByBuOffHeap newByteBufferOffHeap(int byteSize) {
        final var segment = MemorySegment.allocateNative(byteSize);
        return new MemorySegmentByBuOffHeap(segment, segment.asByteBuffer());
    }

    @Override
    public @NotNull ByBuOffHeap newByteBufferOffHeap(byte @NotNull [] bytes) {
        // create a new native MemorySegment with capacity equals to bytes length,
        // then extract its ByteBuffer and fill it with all bytes
        final var segment = MemorySegment.allocateNative(bytes.length);
        return new MemorySegmentByBuOffHeap(segment, segment.asByteBuffer(), bytes);
    }

    @Override
    public final int getLoadPriority() {
        return 14;
    }
}
