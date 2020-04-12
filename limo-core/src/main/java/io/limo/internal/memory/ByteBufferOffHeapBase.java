/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.memory;

import io.limo.internal.utils.UnsafeByteBufferOps;
import io.limo.memory.AbstractByteBufferOffHeap;
import io.limo.memory.ByteBufferOffHeap;

import java.nio.ByteBuffer;

final class ByteBufferOffHeapBase extends AbstractByteBufferOffHeap {

    private final Runnable clean;

    ByteBufferOffHeapBase(final ByteBuffer bb) {
        super(bb);
        this.clean = cleanByteBuffer(bb);
    }

    ByteBufferOffHeapBase(final ByteBuffer bb, byte[] bytes) {
        super(bb, bytes);
        this.clean = cleanByteBuffer(bb);
    }

    private static Runnable cleanByteBuffer(final ByteBuffer bb) {
        return () -> {
            // do not clean if ByteBuffer is readonly (would throw an Exception)
            if (!bb.isReadOnly()) {
                UnsafeByteBufferOps.invokeCleaner(bb);
            }
        };
    }

    @Override
    public void close() {
        clean.run();
    }

    @Override
    protected ByteBufferOffHeap sliceNoIndexCheck(long offset, int length) {
        // save previous values
        final var limit = getByteBuffer().limit();
        final var position = getByteBuffer().position();

        // change values so slice respect required offset and length
        getByteBuffer().limit((int) (offset + length));
        getByteBuffer().position((int) offset);
        final var slice = new ByteBufferOffHeapBase(getByteBuffer().slice());

        // re-affect previous values
        getByteBuffer().limit(limit);
        getByteBuffer().position(position);

        return slice;
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
