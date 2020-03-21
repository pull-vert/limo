/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.data;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteOrder;

/**
 * This interface allows to read binary data
 */
public interface Reader extends AutoCloseable {

    /**
     * @return a byte that was read from the data, read index increases by 1
     * @throws ReaderUnderflowException if there is no byte left to read in data
     */
    byte readByte();

    /**
     * @return a 4 bytes int that was read from the data, read index increases by 4
     * @throws ReaderUnderflowException if there is less than 4 bytes left to read in data
     */
    int readInt();

    /**
     * Retrieves this reader's byte order.
     *
     * <p>The byte order is used when reading multibyte values.
     *
     * @return This reader's byte order
     */
    @NotNull ByteOrder getByteOrder();

    /**
     * Modifies this reader's byte order.
     *
     * @param byteOrder The new byte order,
     *                  either {@link ByteOrder#BIG_ENDIAN BIG_ENDIAN}
     *                  or {@link ByteOrder#LITTLE_ENDIAN LITTLE_ENDIAN}
     */
    void setByteOrder(@NotNull ByteOrder byteOrder);

    /**
     * Closes associated resources
     *
     * <p>This API is experimental, maybe a Reader should not expose the close behavior
     */
    @ApiStatus.Experimental
    @Override
    void close();
}
