/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.bytes;

import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * A pool of {@link ByteSequence}, that allows to recycle and supply {@link ByteSequence} when needed
 */
public interface ByteSequenceSupplier extends Supplier<ByteSequence> {

    /**
     * @return a {@link ByteSequence} from the pool or create new one
     */
    @Override
    @NotNull
    ByteSequence get();

    /**
     * Returns a {@link ByteSequence} to the pool.
     *
     * @param byteSequence This must be a byte sequence previously obtained
     *               by calling MemorySupplier::get. The caller must
     *               not touch the buffer after returning it to
     *               the pool.
     */
    void recycle(@NotNull ByteSequence byteSequence);
}
