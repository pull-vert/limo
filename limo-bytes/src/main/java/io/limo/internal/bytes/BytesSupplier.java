/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.bytes;

import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * A pool of {@link Bytes}, that allows to recycle and supply {@link Bytes} when needed
 */
public interface BytesSupplier extends Supplier<Bytes> {

    /**
     * @return a {@link Bytes} from the pool or create new one
     */
    @Override
    @NotNull Bytes get();

    /**
     * Returns a {@link Bytes} to the pool.
     *
     * @param bytes This must be a byte sequence previously obtained
     *               by calling MemorySupplier::get. The caller must
     *               not touch the buffer after returning it to
     *               the pool.
     */
    void recycle(@NotNull Bytes bytes);
}
