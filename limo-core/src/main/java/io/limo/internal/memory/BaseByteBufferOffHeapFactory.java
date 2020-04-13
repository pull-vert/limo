/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.memory;

import io.limo.memory.ByBuOffHeap;
import io.limo.memory.OffHeap;
import io.limo.memory.OffHeapFactory;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

import static io.limo.internal.memory.BaseByBuOffHeap.cleanByteBuffer;

final class BaseByteBufferOffHeapFactory implements OffHeapFactory {

    @Override
    public final @NotNull OffHeap newOffHeap(long byteSize) {
        if (byteSize > Integer.MAX_VALUE) {
            throw new IndexOutOfBoundsException("byteSize must be a positive Integer");
        }
        return newByteBufferOffHeap((int) byteSize);
    }

    @Override
    public final @NotNull ByBuOffHeap newByteBufferOffHeap(int byteSize) {
        final var bb = ByteBuffer.allocateDirect(byteSize);
        return new BaseByBuOffHeap(bb, cleanByteBuffer(bb));
    }

    @Override
    public @NotNull ByBuOffHeap newByteBufferOffHeap(byte @NotNull [] bytes) {
        return new BaseByBuOffHeap(ByteBuffer.allocateDirect(bytes.length), bytes);
    }

    /**
     * @return Integer.MIN_VALUE because this is the default implementation
     */
    @Override
    public final int getLoadPriority() {
        return Integer.MIN_VALUE;
    }
}
