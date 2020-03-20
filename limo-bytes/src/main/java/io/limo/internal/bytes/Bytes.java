/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.bytes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.nio.ByteBuffer;

/**
 * A read-only (immutable) byte sequence that store all or a part of a complete binary content
 */
public interface Bytes extends AutoCloseable {

    byte readByteAt(@Range(from = 0, to = Integer.MAX_VALUE - 1) int index);

    int readIntAt(@Range(from = 0, to = Integer.MAX_VALUE - 1) int index, boolean isBigEndian);

    /**
     * @return The size (in bytes) of this byte sequence
     */
    @Range(from = 1, to = Integer.MAX_VALUE) int getByteSize();

    /**
     * Export the content of this byte sequence into a {@code byte[]} (could be a fresh new one or an existing one)
     */
    byte[] toByteArray();

    /**
     * Export the content of this byte sequence into a {@link ByteBuffer} (could be a fresh new one or an existing one)
     */
    @NotNull ByteBuffer toByteBuffer();

    /**
     * Closes this byte sequence.
     * <p>Default close is NOP
     */
    @Override
    default void close() {
    }
}
