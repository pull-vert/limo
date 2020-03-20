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
     * @return The data reader
     */
    @NotNull Reader getReader();

    /**
     * @return  The limit of this binary data
     */
    @Range(from = 0, to = Long.MAX_VALUE - 1) long getLimit();

    /**
     * Closes all resources that store binary data
     */
    @Override
    void close();
}
