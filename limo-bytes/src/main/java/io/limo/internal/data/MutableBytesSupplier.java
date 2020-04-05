/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.data;

import io.limo.internal.bytes.Bytes;
import io.limo.internal.bytes.MutableBytes;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * A pool of {@link MutableBytes}, that allows to recycle and supply {@link Bytes} when needed
 */
public interface MutableBytesSupplier extends Supplier<MutableBytes> {

    /**
     * @return a {@link MutableBytes} from the pool or create new one
     */
    @Override
    @NotNull MutableBytes get();

    /**
     * Returns a {@link MutableBytes} to the pool.
     *
     * @param bytes This must be a byte sequence previously obtained
     *               by calling MemorySupplier::get. The caller must
     *               not touch the bytes after returning it to
     *               the pool.
     */
    @ApiStatus.Experimental
    void recycle(@NotNull MutableBytes bytes);
}
