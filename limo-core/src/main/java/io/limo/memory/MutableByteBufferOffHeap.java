/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.memory;

import org.jetbrains.annotations.NotNull;

public interface MutableByteBufferOffHeap extends ByteBufferOffHeap, MutableOffHeap {

    /**
     * @return a new immutable ByteBufferOffHeap view of this MutableByteBufferOffHeap.
     */
    @Override
    @NotNull ByteBufferOffHeap asReadOnly();
}
