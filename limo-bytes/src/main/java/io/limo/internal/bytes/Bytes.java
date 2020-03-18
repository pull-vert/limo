/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.bytes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.nio.ByteOrder;

/**
 * A byte sequence that store all or a part of a complete binary content
 */
public interface Bytes extends AutoCloseable {

    byte readByteAt(@Range(from = 0, to = Integer.MAX_VALUE - 1) int index);

    int readIntAt(@Range(from = 0, to = Integer.MAX_VALUE - 1) int index);

    void writeByteAt(@Range(from = 0, to = Integer.MAX_VALUE - 1) int index, byte value);

    void writeIntAt(@Range(from = 0, to = Integer.MAX_VALUE - 1) int index, int value);

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
     * Closes this byte sequence.
     * <p>Default close is NOP
     */
    @Override
    default void close() {
    }
}
