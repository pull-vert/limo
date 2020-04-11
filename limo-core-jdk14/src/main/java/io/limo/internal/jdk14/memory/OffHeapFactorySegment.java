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
        return new ByteBufferOffHeapSegment(MemorySegment.allocateNative(byteSize));
    }

    /**
     * @return Integer.MIN_VALUE because this is the default implementation
     */
    @Override
    public final int getLoadPriority() {
        return 1;
    }
}
