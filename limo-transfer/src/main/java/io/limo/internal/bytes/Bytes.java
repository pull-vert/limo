/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.bytes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * A read-only (immutable) byte sequence that store all or a part of a complete binary content
 */
public interface Bytes extends AutoCloseable {

    byte readByteAt(@Range(from = 0, to = Integer.MAX_VALUE - 1) int index);

    int readIntAt(@Range(from = 0, to = Integer.MAX_VALUE - 1) int index);

    /**
     * @return The size (in bytes) of this byte sequence
     */
    @Range(from = 1, to = Integer.MAX_VALUE) int getByteSize();

    /**
     * Modifies this byte sequence's byte order.
     *
     * @param byteOrder The new byte order,
     *                  either {@link ByteOrder#BIG_ENDIAN BIG_ENDIAN}
     *                  or {@link ByteOrder#LITTLE_ENDIAN LITTLE_ENDIAN}
     */
    void setByteOrder(@NotNull ByteOrder byteOrder);

    /**
     * Obtains an acquired byte sequence which can be used to
     * access memory associated with this byte sequence from the current thread.
     *
     * <p>Some implementations do not support thread-confinement, in this case current byte sequence is returned
     *
     * @return an acquired byte sequence which can be used to access memory associated
     * with this byte sequence from the current thread.
     * @throws IllegalStateException can be thrown in some implementations, if this byte sequence has been closed
     */
    @NotNull Bytes acquire();

    /**
     * @return true, if the byte sequence is read-only.
     * @see MutableBytes#asReadOnly()
     */
    boolean isReadOnly();

    /**
     * Closes this byte sequence.
     */
    @Override
    void close();

    /**
     * Export the content of this byte sequence into a {@code byte[]} (could be a fresh new one or an existing one)
     */
    byte @NotNull [] toByteArray();

    /**
     * Export the content of this byte sequence into a {@link ByteBuffer} (could be a fresh new one or an existing one)
     */
    @NotNull ByteBuffer toByteBuffer();
}