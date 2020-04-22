/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.memory;

import io.limo.memory.*;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

import static io.limo.internal.memory.BaseMutableSafeByBuOffHeap.cleanByteBuffer;

final class BaseByteBufferOffHeapFactory implements OffHeapFactory {

    @Override
    public final @NotNull OffHeap newOffHeap(long byteSize) {
        if (byteSize > Integer.MAX_VALUE) {
            throw new IndexOutOfBoundsException("byteSize must be a positive Integer");
        }
        return newMutableSafeByBuOffHeap((int) byteSize);
    }

    @Override
    public @NotNull MutableSafeByBuOffHeap newMutableSafeByBuOffHeap(int byteSize) {
        final var bb = ByteBuffer.allocateDirect(byteSize);
        return new BaseMutableSafeByBuOffHeap(bb, cleanByteBuffer(bb));
    }

    @Override
    public @NotNull MutableUnsafeByBuOffHeap newMutableUnsafeByBuOffHeap(int byteSize) {
        final var bb = ByteBuffer.allocateDirect(byteSize);
        return new BaseMutableUnsafeByBuOffHeap(bb, cleanByteBuffer(bb));
    }

    @Override
    public @NotNull ByBuOffHeap newByteBufferOffHeap(byte @NotNull [] bytes) {
        return null;
        //return new BaseMutableSafeByBuOffHeap(ByteBuffer.allocateDirect(bytes.length), bytes);
    }

    /**
     * @return Integer.MIN_VALUE because this is the default implementation
     */
    @Override
    public final int getLoadPriority() {
        return Integer.MIN_VALUE;
    }
}
