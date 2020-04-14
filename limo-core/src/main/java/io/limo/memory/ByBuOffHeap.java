/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.memory;

import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

/**
 * This interface contains a direct {@link ByteBuffer} that point to a contiguous off-heap memory region of
 * size < Integer.MAX_VALUE.
 * <p>ByBuOffHeap can be allocated using one of the factory methods : see {@link OffHeapFactory#of(byte[])}
 * <p>See {@link OffHeap} for a complete behavior description of a OffHeap
 */
public interface ByBuOffHeap extends OffHeap {

    @NotNull ByteBuffer getByteBuffer();

    /**
     * {@inheritDoc}
     * @return a new ByBuOffHeap (off-heap memory represented by a direct ByteBuffer) view with updated base position
     * and limit addresses.
     */
    @Override
    @NotNull ByBuOffHeap slice(long offset, long length);
}
