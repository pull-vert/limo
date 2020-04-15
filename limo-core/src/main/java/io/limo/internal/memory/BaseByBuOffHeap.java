/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.memory;

import io.limo.internal.utils.UnsafeByteBufferOps;
import io.limo.memory.AbstractByBuOffHeap;
import io.limo.memory.ByBuOffHeap;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

final class BaseByBuOffHeap extends AbstractByBuOffHeap {

    private final Runnable doOnClose;

    BaseByBuOffHeap(final ByteBuffer bb, Runnable doOnClose) {
        super(bb);
        this.doOnClose = doOnClose;
    }

    /**
     * The ByteBuffer passed as parameter will be cleaned when close method will be invoked
     */
    BaseByBuOffHeap(final ByteBuffer bb, byte[] bytes) {
        super(bb, bytes);
        this.doOnClose = cleanByteBuffer(bb);
    }

    static Runnable cleanByteBuffer(final ByteBuffer bb) {
        return () -> {
            // do not clean if ByteBuffer is readonly (would throw an Exception)
            if (!bb.isReadOnly()) {
                UnsafeByteBufferOps.invokeCleaner(bb);
            }
        };
    }

    @Override
    public @NotNull ByBuOffHeap slice(long offset, long length) {
        sliceIndexCheck(offset, length, getByteSize());

        // save previous values
        final var limit = getByteBuffer().limit();
        final var position = getByteBuffer().position();

        // change values so slice respect required offset and length
        getByteBuffer().limit((int) (offset + length));
        getByteBuffer().position((int) offset);
        // call constructor to do nothing on close, because invoke cleaner on a sliced ByteBuffer throws an Exception
        final var slice = new BaseByBuOffHeap(getByteBuffer().slice(), () -> {});

        // re-affect previous values
        getByteBuffer().limit(limit);
        getByteBuffer().position(position);

        return slice;
    }

    @Override
    public void close() {
        doOnClose.run();
    }

    @Override
    protected void checkState() {
        // todo
    }

    @Override
    protected byte readByteAtNoIndexCheck(long index) {
        return getByteBuffer().get((int) index);
    }

    @Override
    protected int readIntAtNoIndexCheck(long index) {
        return getByteBuffer().getInt((int) index);
    }
}
