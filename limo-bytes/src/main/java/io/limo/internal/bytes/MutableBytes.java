/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.bytes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

/**
 * A read-write (mutable) byte sequence that store all or a part of a complete binary content
 */
public interface MutableBytes extends Bytes {

    void writeByteAt(@Range(from = 0, to = Integer.MAX_VALUE - 1) int index, byte value);

    void writeIntAt(@Range(from = 0, to = Integer.MAX_VALUE - 1) int index, int value);

    /**
     * Obtains a read-only view of this byte sequence. An attempt to write in a read-only byte sequence
     * will fail with {@link UnsupportedOperationException}.
     * @return a read-only view of this byte sequence.
     */
    @NotNull Bytes asReadOnly();

    @Override
    @NotNull MutableBytes acquire();
}
