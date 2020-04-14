/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.memory;

import org.jetbrains.annotations.NotNull;

public interface MutableByBuOffHeap extends ByBuOffHeap, MutableOffHeap {

    /**
     * @return a new immutable ByteBufferOffHeap view of this MutableByteBufferOffHeap.
     */
    @Override
    @NotNull ByBuOffHeap asReadOnly();

    /**
     * {@inheritDoc}
     *
     * @return a new MutableByBuOffHeap (off-heap memory represented by a direct ByteBuffer) view with updated base
     * position and limit addresses.
     */
    @Override
    @NotNull MutableByBuOffHeap slice(long offset, long length);
}