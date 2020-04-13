/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.memory;

import io.limo.internal.utils.UnsafeByteBufferOps;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.util.Objects;

public abstract class AbstractMutableByBuOffHeap extends AbstractMutableOffHeap implements MutableByBuOffHeap {

    private final @NotNull ByteBuffer bb;

    /**
     * Instantiate a readonly AbstractByteBufferOffHeap from a ByteBuffer
     */
    protected AbstractMutableByBuOffHeap(@NotNull ByteBuffer bb) {
        super(UnsafeByteBufferOps.getBaseAddress(Objects.requireNonNull(bb)));
        if (!bb.isDirect()) {
            throw new IllegalArgumentException("Provided ByteBuffer must be direct");
        }
        if (bb.isReadOnly()) {
            throw new IllegalArgumentException("Provided ByteBuffer must not be readOnly");
        }
        this.bb = bb;
    }

    @Override
    public @NotNull ByteBuffer getByteBuffer() {
        return this.bb;
    }

    @Override
    protected final byte @NotNull [] toByteArrayNoIndexCheck() {
        final var bytes = new byte[this.bb.capacity()];
        UnsafeByteBufferOps.fillTargetByteArray(this.bb, 0, bytes, 0, bytes.length);
        return bytes;
    }
}
