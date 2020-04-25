/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.memory.impl;

import io.limo.internal.utils.UnsafeByteBufferOps;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.util.Objects;

public abstract class MutableUnsafeOffHeap extends AbstractMutableByteBufferOffHeap {

    /**
     * Instantiate a readonly UnsafeOffHeap from a ByteBuffer
     */
    protected MutableUnsafeOffHeap(@NotNull ByteBuffer bb) {
        super(Objects.requireNonNull(bb), UnsafeByteBufferOps.UNSAFE_READER_WRITER);
    }
}
