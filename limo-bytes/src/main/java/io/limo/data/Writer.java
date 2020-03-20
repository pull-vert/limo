/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.data;

import org.jetbrains.annotations.NotNull;

import java.nio.ByteOrder;

/**
 * This interface allows to write binary data
 */
public interface Writer extends AutoCloseable {

    /**
     * writes a byte in the data, write index increases by 1
     * @throws WriterOverflowException if there is no room in data to write a byte
     */
    void writeByte(byte value);

    /**
     * writes an int in the data, write index increases by 4
     * @throws WriterOverflowException if there is no room in data to write an int (4 bytes)
     */
    void writeInt(int value);

    /**
     * Retrieves this writer's byte order.
     *
     * <p>The byte order is used when writing multibyte values.
     *
     * @return This reader's byte order
     */
    @NotNull ByteOrder getByteOrder();

    /**
     * Modifies this writer's byte order.
     *
     * @param byteOrder The new byte order,
     *                  either {@link ByteOrder#BIG_ENDIAN BIG_ENDIAN}
     *                  or {@link ByteOrder#LITTLE_ENDIAN LITTLE_ENDIAN}
     */
    void setByteOrder(@NotNull ByteOrder byteOrder);

    /**
     * Closes all resources that store binary data
     */
    @Override
    void close();
}
