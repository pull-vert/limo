/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.memory;

import io.limo.internal.utils.BaseOffHeapOps;
import io.limo.memory.*;
import io.limo.memory.impl.*;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

final class BaseByteBufferOffHeapFactory implements OffHeapFactory {

    @Override
    public @NotNull MutableOffHeap newSafeMutableOffHeap(long byteSize) {
        if (byteSize > Integer.MAX_VALUE) {
            throw new IndexOutOfBoundsException("byteSize must be a positive Integer");
        }
        return newMutableSafeByBuOffHeap((int) byteSize);
    }

    @Override
    public @NotNull MutableUnsafeOffHeap newUnsafeMutableOffHeap(long byteSize) {
        if (byteSize > Integer.MAX_VALUE) {
            throw new IndexOutOfBoundsException("byteSize must be a positive Integer");
        }
        return newMutableUnsafeByBuOffHeap((int) byteSize);
    }

    @Override
    public final @NotNull MutableSafeByBuOffHeap newMutableSafeByBuOffHeap(int byteSize) {
        final var bb = ByteBuffer.allocateDirect(byteSize);
        return new BaseMutableSafeByBuOffHeap(bb, BaseOffHeapOps.cleanByteBuffer(bb));
    }

    @Override
    public final @NotNull MutableUnsafeByBuOffHeap newMutableUnsafeByBuOffHeap(int byteSize) {
        final var bb = ByteBuffer.allocateDirect(byteSize);
        return new BaseMutableUnsafeByBuOffHeap(bb, BaseOffHeapOps.cleanByteBuffer(bb));
    }

    @Override
    public final @NotNull SafeByBuOffHeap newSafeByteBufferOffHeap(byte @NotNull [] bytes) {
        return new BaseSafeByBuOffHeap(ByteBuffer.allocateDirect(bytes.length), bytes);
    }

    @Override
    public final @NotNull UnsafeByBuOffHeap newUnsafeByteBufferOffHeap(byte @NotNull [] bytes) {
        return new BaseUnsafeByBuOffHeap(ByteBuffer.allocateDirect(bytes.length), bytes);
    }

    /**
     * @return Integer.MIN_VALUE because this is the default implementation
     */
    @Override
    public final int getLoadPriority() {
        return Integer.MIN_VALUE;
    }
}
