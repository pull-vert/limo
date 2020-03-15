/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.bytes;

import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * A pool of {@link ByBu}, that allows to recycle and supply {@link ByBu} when needed
 */
public interface ByBuSupplier extends Supplier<ByBu> {

    /**
     * @return a {@link ByBu} from the pool or create new one
     */
    @Override
    @NotNull ByBu get();

    /**
     * Returns a {@link ByBu} to the pool.
     *
     * @param byBu This must be a byte sequence previously obtained
     *               by calling MemorySupplier::get. The caller must
     *               not touch the buffer after returning it to
     *               the pool.
     */
    void recycle(@NotNull ByBu byBu);
}
