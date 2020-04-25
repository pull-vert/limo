/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.memory;

import io.limo.memory.ByBuOffHeap;
import io.limo.memory.MutableByBuOffHeap;
import io.limo.memory.impl.MutableUnsafeByBuOffHeap;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

final class BaseMutableUnsafeByBuOffHeap extends MutableUnsafeByBuOffHeap {

    private final Runnable doOnClose;
    private boolean closed = false;

    BaseMutableUnsafeByBuOffHeap(final ByteBuffer bb, Runnable doOnClose) {
        super(bb);
        this.doOnClose = doOnClose;
    }

    @Override
    public final @NotNull ByBuOffHeap asReadOnly() {
        // call constructor to do nothing on close, because cleaner is already associated to this ByteBuffer
        return new BaseUnsafeByBuOffHeap(getByteBuffer(), () -> {});
    }

    @Override
    public final @NotNull MutableByBuOffHeap slice(long offset, long length) {
        sliceIndexCheck(offset, length, getByteSize());

        // save previous values
        final var limit = getByteBuffer().limit();
        final var position = getByteBuffer().position();

        // change values so slice respect required offset and length
        getByteBuffer().limit((int) (offset + length));
        getByteBuffer().position((int) offset);
        // call constructor to do nothing on close, because invoke cleaner on a sliced ByteBuffer throws an Exception
        final var slice = new BaseMutableUnsafeByBuOffHeap(getByteBuffer().slice(), () -> {});

        // re-affect previous values
        getByteBuffer().limit(limit);
        getByteBuffer().position(position);

        return slice;
    }

    @Override
    public final void close() {
        doOnClose.run();
        this.closed = true;
    }

    @Override
    protected final void checkState() {
        if (this.closed) {
            throw new IllegalStateException("OffHeap is not alive");
        }
    }
}
