/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.memory;

import io.limo.internal.utils.BaseOffHeapOps;
import io.limo.memory.ByBuOffHeap;
import io.limo.memory.impl.UnsafeByBuOffHeap;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

final class BaseUnsafeByBuOffHeap extends UnsafeByBuOffHeap {

    private final Runnable doOnClose;
    private boolean closed = false;

    BaseUnsafeByBuOffHeap(final ByteBuffer bb, Runnable doOnClose) {
        super(bb);
        this.doOnClose = doOnClose;
    }

    /**
     * The ByteBuffer passed as parameter will be cleaned when close method will be invoked
     */
    BaseUnsafeByBuOffHeap(final ByteBuffer bb, byte[] bytes) {
        super(bb, bytes);
        this.doOnClose = BaseOffHeapOps.cleanByteBuffer(bb);
    }

    @Override
    public final @NotNull ByBuOffHeap slice(long offset, long length) {
        sliceIndexCheck(offset, length, getByteSize());

        // save previous values
        final var limit = getByteBuffer().limit();
        final var position = getByteBuffer().position();

        // change values so slice respect required offset and length
        getByteBuffer().limit((int) (offset + length));
        getByteBuffer().position((int) offset);
        // call constructor to do nothing on close, because invoke cleaner on a sliced ByteBuffer throws an Exception
        final var slice = new BaseUnsafeByBuOffHeap(getByteBuffer().slice(), () -> {});

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
