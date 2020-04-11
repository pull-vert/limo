/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.memory;

import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

/**
 * This interface contains a direct {@link ByteBuffer} that point to a off-heap memory region
 */
public interface ByteBufferOffHeap extends OffHeap {

    @NotNull ByteBuffer getByteBuffer();
}
