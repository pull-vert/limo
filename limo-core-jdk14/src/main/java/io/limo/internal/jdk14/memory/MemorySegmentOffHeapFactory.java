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
        return new MemorySegmentOffHeap(MemorySegment.allocateNative(byteSize));
    }

    @Override
    public final @NotNull ByBuOffHeap newByteBufferOffHeap(int byteSize) {
        return new MemorySegmentByBuOffHeap(MemorySegment.allocateNative(byteSize));
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
