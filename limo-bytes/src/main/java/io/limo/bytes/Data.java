/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.bytes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.nio.ByteOrder;

/**
 * A complete read-only (immutable) binary data
 */
public interface Data extends AutoCloseable {

    /**
     * @return The size (in bytes) of this binary data
     */
    @Range(from = 1, to = Long.MAX_VALUE) long getByteSize();

    /**
     * @return the data reader
     */
    @NotNull Reader getReader();

    /**
     * Retrieves this data's byte order.
     *
     * <p> The byte order is used when reading or writing multibyte values.
     * The order of a newly-created data is always {@link ByteOrder#BIG_ENDIAN
     * BIG_ENDIAN}.  </p>
     *
     * @return This data's byte order
     */
    @NotNull ByteOrder getByteOrder();

    /**
     * Modifies this data's byte order.
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
