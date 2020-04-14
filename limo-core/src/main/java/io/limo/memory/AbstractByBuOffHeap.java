/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.memory;

import io.limo.internal.utils.UnsafeByteBufferOps;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

/**
 * Base abstract implementation of {@link ByBuOffHeap} memory
 */
public abstract class AbstractByBuOffHeap extends AbstractOffHeap implements ByBuOffHeap {

    protected AbstractByBuOffHeap(ByteBuffer bb, byte[] bytes) {
        this(UnsafeByteBufferOps.fillWithByteArray(bb, 0, bytes, 0, bytes.length));
    }

    /**
     * Instantiate a readonly AbstractByBuOffHeap from a ByteBuffer
     */
    protected AbstractByBuOffHeap(@NotNull ByteBuffer bb) {
        super(bb, true);
    }

    @Override
    public final long getByteSize() {
        return baseByBu.capacity();
    }

    @Override
    public final @NotNull ByBuOffHeap asByBuOffHeap() {
        return this;
    }

    @Override
    public final @NotNull ByteBuffer getByteBuffer() {
        return this.baseByBu;
    }

    @Override
    public long getWriteIndex() {
        return baseByBu.limit();
    }

    @Override
    protected final byte @NotNull [] toByteArrayNoIndexCheck() {
        final var bytes = new byte[this.baseByBu.capacity()];
        UnsafeByteBufferOps.fillTargetByteArray(this.baseByBu, 0, bytes, 0, bytes.length);
        return bytes;
    }
}
