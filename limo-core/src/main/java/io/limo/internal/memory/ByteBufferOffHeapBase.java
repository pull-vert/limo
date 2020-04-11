/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.memory;

import io.limo.internal.utils.ByteBuffers;
import io.limo.memory.AbstractByteBufferOffHeap;
import io.limo.memory.ByteBufferOffHeap;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

final class ByteBufferOffHeapBase extends AbstractByteBufferOffHeap {

    private final @NotNull ByteBuffer bb;

    ByteBufferOffHeapBase(@NotNull ByteBuffer bb) {
        super(bb);
        this.bb = bb;
    }

    @Override
    public void close() {
        ByteBuffers.invokeCleaner(this.bb);
    }

    @Override
    protected ByteBufferOffHeap asSliceImpl(int offset, int length) {
        // save previous values
        final var limit = this.bb.limit();
        final var position = this.bb.position();

        // change values so slice respect required offset and length
        this.bb.limit(offset + length);
        this.bb.position(offset);
        final var slice = new ByteBufferOffHeapBase(this.bb.slice());

        // re-affect previous values
        this.bb.limit(limit);
        this.bb.position(position);

        return slice;
    }
}
