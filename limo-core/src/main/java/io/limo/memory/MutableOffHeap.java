/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.memory;

import org.jetbrains.annotations.NotNull;

public interface MutableOffHeap extends OffHeap, IndexedWriter {

    /**
     * @return a new immutable OffHeap view of this MutableOffHeap.
     */
    @NotNull OffHeap asReadOnly();

    /**
     * {@inheritDoc}
     * @return a new MutableByteBufferOffHeap (off-heap memory represented by a direct ByteBuffer) view with updated base position and limit addresses.
     */
    @Override
    @NotNull MutableByBuOffHeap slice(long offset, int length);

    /**
     * {@inheritDoc}
     * @implSpec {@inheritDoc}
     */
    @Override
    MutableOffHeap writeByte(byte value);

    /**
     * {@inheritDoc}
     * @implSpec {@inheritDoc}
     */
    @Override
    MutableOffHeap writeInt(int value);

    /**
     * {@inheritDoc}
     * @implSpec {@inheritDoc}
     */
    @Override
    MutableOffHeap writeByteAt(long index, byte value);

    /**
     * {@inheritDoc}
     * @implSpec {@inheritDoc}
     */
    @Override
    MutableOffHeap writeIntAt(long index, int value);
}
