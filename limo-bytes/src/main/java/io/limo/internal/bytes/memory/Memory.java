/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.bytes.memory;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.nio.ByteOrder;

/**
 * A memory chunk that can store all or a part of a binary content
 */
public interface Memory extends AutoCloseable {

    byte readByteAt(@Range(from = 0, to = Long.MAX_VALUE) long index);

    int readIntAt(@Range(from = 0, to = Long.MAX_VALUE) long index);

    void writeByteAt(@Range(from = 0, to = Long.MAX_VALUE) long index, byte value);

    void writeIntAt(@Range(from = 0, to = Long.MAX_VALUE) long index, int value);

    /**
     * @return Total capacity, in bytes
     */
    @Range(from = 0, to = Long.MAX_VALUE)
    long getCapacity();

    /**
     * Modifies this memory's byte order.
     *
     * @param  byteOrder The new byte order,
     *         either {@link ByteOrder#BIG_ENDIAN BIG_ENDIAN}
     *         or {@link ByteOrder#LITTLE_ENDIAN LITTLE_ENDIAN}
     */
    void setByteOrder(@NotNull ByteOrder byteOrder);

    /**
     * Closes this memory chunk
     */
    @Override
    void close();
}
