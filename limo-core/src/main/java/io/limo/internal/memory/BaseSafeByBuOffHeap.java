/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.memory;

import io.limo.internal.utils.BaseOffHeapOps;
import io.limo.memory.ByBuOffHeap;
import io.limo.memory.SafeByBuOffHeap;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

final class BaseSafeByBuOffHeap extends SafeByBuOffHeap {

    private final Runnable doOnClose;

    BaseSafeByBuOffHeap(final ByteBuffer bb, Runnable doOnClose) {
        super(bb);
        this.doOnClose = doOnClose;
    }

    /**
     * The ByteBuffer passed as parameter will be cleaned when close method will be invoked
     */
    BaseSafeByBuOffHeap(final ByteBuffer bb, byte[] bytes) {
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
        final var slice = new BaseSafeByBuOffHeap(getByteBuffer().slice(), () -> {});

        // re-affect previous values
        getByteBuffer().limit(limit);
        getByteBuffer().position(position);

        return slice;
    }

    @Override
    public final void close() {
        doOnClose.run();
    }

    @Override
    protected final void checkState() {
        // todo
    }
}
