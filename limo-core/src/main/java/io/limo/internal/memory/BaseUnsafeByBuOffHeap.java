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

    private final Runnable cleanupAction;
    private final Thread owner;
    private boolean closed = false;

    BaseUnsafeByBuOffHeap(final ByteBuffer bb, Runnable cleanupAction, Thread owner) {
        super(bb);
        this.cleanupAction = cleanupAction;
        this.owner = owner;
    }

    /**
     * The ByteBuffer passed as parameter will be cleaned when close method will be invoked
     */
    BaseUnsafeByBuOffHeap(final ByteBuffer bb, byte[] bytes, Thread owner) {
        super(bb, bytes);
        this.cleanupAction = BaseOffHeapOps.cleanByteBuffer(bb);
        this.owner = owner;
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
        final var slice = new BaseUnsafeByBuOffHeap(getByteBuffer().slice(), () -> {}, this.owner);

        // re-affect previous values
        getByteBuffer().limit(limit);
        getByteBuffer().position(position);

        return slice;
    }

    @Override
    public final @NotNull ByBuOffHeap acquire() {
        return new BaseUnsafeByBuOffHeap(getByteBuffer(), () -> {}, Thread.currentThread());
    }

    @Override
    protected final void closeAfterCheckState() {
        this.cleanupAction.run();
        this.closed = true;
    }

    @Override
    protected final void checkState() {
        if (this.owner != Thread.currentThread()) {
            throw new IllegalStateException("Attempt to access OffHeap outside owning thread");
        }
        if (this.closed) {
            throw new IllegalStateException("OffHeap is not alive");
        }
    }
}
