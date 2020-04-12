/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.memory;

import io.limo.internal.utils.ByteBuffers;
import io.limo.memory.ByteBufferOffHeap;
import io.limo.memory.OffHeap;
import io.limo.memory.OffHeapFactory;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

final class OffHeapFactoryBase implements OffHeapFactory {

    @Override
    public final @NotNull OffHeap newOffHeap(long byteSize) {
        if (byteSize > Integer.MAX_VALUE) {
            throw new IndexOutOfBoundsException("byteSize must be a positive Integer");
        }
        return newByteBufferOffHeap((int) byteSize);
    }

    @Override
    public final @NotNull ByteBufferOffHeap newByteBufferOffHeap(int byteSize) {
        return new ByteBufferOffHeapBase(ByteBuffer.allocateDirect(byteSize));
    }

    @Override
    public @NotNull ByteBufferOffHeap newByteBufferOffHeap(byte @NotNull [] bytes) {
        // create a new direct ByteBuffer with capacity equals to bytes length, then fill it with all bytes
        final var bb = ByteBuffer.allocateDirect(bytes.length);
        ByteBuffers.fillWithByteArray(bb, 0, bytes, 0, bytes.length);
        // set limit (position is still at 0)
        bb.limit(bytes.length);
        return new ByteBufferOffHeapBase(bb);
    }

    /**
     * @return Integer.MIN_VALUE because this is the default implementation
     */
    @Override
    public final int getLoadPriority() {
        return Integer.MIN_VALUE;
    }
}
