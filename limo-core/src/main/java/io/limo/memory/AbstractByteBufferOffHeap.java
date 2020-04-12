/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.memory;

import io.limo.internal.utils.UnsafeByteBufferOps;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.util.Objects;

/**
 * Base abstract implementation of {@link ByteBufferOffHeap} memory
 */
public abstract class AbstractByteBufferOffHeap extends AbstractOffHeap implements ByteBufferOffHeap {

    private final @NotNull ByteBuffer bb;

    /**
     * Instantiate a readonly AbstractByteBufferOffHeap from a ByteBuffer
     */
    protected AbstractByteBufferOffHeap(@NotNull ByteBuffer bb) {
        this(bb, true);
    }

    AbstractByteBufferOffHeap(@NotNull ByteBuffer bb, boolean isReadonly) {
        super(UnsafeByteBufferOps.getBaseAddress(Objects.requireNonNull(bb)));
        if (!bb.isDirect()) {
            throw new IllegalArgumentException("Provided ByteBuffer must be Direct");
        }
        if (isReadonly && !bb.isReadOnly()) {
            this.bb = bb.asReadOnlyBuffer();
        } else {
            this.bb = bb;
        }
    }

    protected AbstractByteBufferOffHeap(ByteBuffer bb, byte[] bytes) {
        this(UnsafeByteBufferOps.fillWithByteArray(bb, 0, bytes, 0, bytes.length));
    }

    @Override
    public final long getByteSize() {
        return bb.capacity();
    }

    @Override
    public final @NotNull ByteBuffer getByteBuffer() {
        return this.bb;
    }

    @Override
    public long getWriteIndex() {
        return bb.limit();
    }

    @Override
    protected final byte @NotNull [] toByteArrayNoIndexCheck() {
        final var bytes = new byte[this.bb.capacity()];
        UnsafeByteBufferOps.fillTargetByteArray(this.bb, 0, bytes, 0, bytes.length);
        return bytes;
    }
}
