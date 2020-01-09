/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.bytes.memory;

import io.limo.common.NotNull;

import java.util.function.Supplier;

/**
 * A pool of {@link Memory}, that allows to recycle and supply {@link Memory} when needed
 */
public interface MemorySupplier extends Supplier<Memory> {

    /**
     * @return a {@link Memory} from the pool or create new one
     */
    @Override
    @NotNull
    Memory get();

    /**
     * Returns a {@link Memory} to the pool.
     *
     * @param memory This must be a memory previously obtained
     *               by calling MemorySupplier::get. The caller must
     *               not touch the buffer after returning it to
     *               the pool.
     */
    void recycle(@NotNull Memory memory);
}
