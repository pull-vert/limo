/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.jdk14.memory;

import io.limo.memory.*;
import jdk.incubator.foreign.MemorySegment;
import org.jetbrains.annotations.NotNull;

public final class MemorySegmentOffHeapFactory implements OffHeapFactory {

    @Override
    public final @NotNull OffHeap newOffHeap(long byteSize) {
        return new MemorySegmentUnsafeOffHeap(MemorySegment.allocateNative(byteSize));
    }

    @Override
    public final @NotNull MutableSafeByBuOffHeap newMutableSafeByBuOffHeap(int byteSize) {
        return new MemorySegmentMutableSafeByBuOffHeap(MemorySegment.allocateNative(byteSize));
    }

    @Override
    public final @NotNull MutableUnsafeByBuOffHeap newMutableUnsafeByBuOffHeap(int byteSize) {
        return new MemorySegmentMutableUnsafeByBuOffHeap(MemorySegment.allocateNative(byteSize));
    }

    @Override
    public final @NotNull SafeByBuOffHeap newSafeByteBufferOffHeap(byte @NotNull [] bytes) {
        // create a new native MemorySegment with capacity equals to bytes length,
        // then extract its ByteBuffer and fill it with all bytes
        final var segment = MemorySegment.allocateNative(bytes.length);
        return new MemorySegmentSafeByBuOffHeap(segment, segment.asByteBuffer(), bytes);
    }

    @Override
    public final @NotNull UnsafeByBuOffHeap newUnsafeByteBufferOffHeap(byte @NotNull [] bytes) {
        // create a new native MemorySegment with capacity equals to bytes length,
        // then extract its ByteBuffer and fill it with all bytes
        final var segment = MemorySegment.allocateNative(bytes.length);
        return new MemorySegmentUnsafeByBuOffHeap(segment, segment.asByteBuffer(), bytes);
    }

    @Override
    public final int getLoadPriority() {
        return 14;
    }
}
