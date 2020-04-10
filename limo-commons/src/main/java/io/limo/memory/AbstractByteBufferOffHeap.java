/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.memory;

import io.limo.internal.utils.ByteBuffers;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.util.Objects;

public abstract class AbstractByteBufferOffHeap implements ByteBufferOffHeap {

    private final @NotNull ByteBuffer bb;

    protected AbstractByteBufferOffHeap(@NotNull ByteBuffer bb) {
        if (!Objects.requireNonNull(bb).isDirect()) {
            throw new IllegalArgumentException("buffer is non-direct");
        }
        this.bb = bb;
    }

    @Override
    public final long getByteSize() {
        return bb.capacity();
    }

    @Override
    public final @NotNull ByteBufferOffHeap asSlice(long offset, int length) {
        if ((offset | length) < 0 || offset > Integer.MAX_VALUE || offset + length > this.bb.limit()) {
            throw new ArrayIndexOutOfBoundsException(
                    String.format("Incorrect parameters to slice : offset=%d, length=%d, limit=%d", offset, length, this.bb.limit()));
        }
        return asSliceImpl((int) offset, length);
    }

    @Override
    public final @NotNull ByteBuffer getByteBuffer() {
        return this.bb;
    }

    @Override
    public final byte @NotNull [] toByteArray() {
        final var bytes = new byte[this.bb.capacity()];
        ByteBuffers.fillTargetByteArray(this.bb, 0, bytes, 0, bytes.length);
        return bytes;
    }

    protected abstract ByteBufferOffHeap asSliceImpl(int offset, int length);
}
