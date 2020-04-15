/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.memory;

import io.limo.internal.utils.UnsafeByteBufferOps;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.util.Objects;

public abstract class AbstractMutableByBuOffHeap extends AbstractMutableOffHeap<AbstractMutableByBuOffHeap> implements MutableByBuOffHeap {

    /**
     * Instantiate a readonly AbstractByteBufferOffHeap from a ByteBuffer
     */
    protected AbstractMutableByBuOffHeap(@NotNull ByteBuffer bb) {
        super(Objects.requireNonNull(bb));
    }

    @Override
    public final @NotNull MutableByBuOffHeap asByBuOffHeap() {
        return this;
    }

    @Override
    public final @NotNull ByteBuffer getByteBuffer() {
        return this.baseByBu;
    }

    @Override
    protected final byte @NotNull [] toByteArrayNoIndexCheck() {
        final var bytes = new byte[this.baseByBu.capacity()];
        UnsafeByteBufferOps.fillTargetByteArray(this.baseByBu, 0, bytes, 0, bytes.length);
        return bytes;
    }
}
