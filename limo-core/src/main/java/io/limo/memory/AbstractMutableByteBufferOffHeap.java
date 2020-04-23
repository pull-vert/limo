/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.memory;

import io.limo.internal.utils.IndexedByBuReaderWriter;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.util.function.Function;

abstract class AbstractMutableByteBufferOffHeap extends AbstractByteBufferOffHeap implements MutableOffHeap {

    protected AbstractMutableByteBufferOffHeap(@NotNull ByteBuffer baseByBu,
                                               @NotNull Function<ByteBuffer, IndexedByBuReaderWriter> readerWriter) {
        super(baseByBu, false, readerWriter);
    }

    @Override
    public void writeByteAt(long index, byte value) {
        checkState();
        this.readerWriter.writeByteAt(index, value);
    }

    @Override
    public void writeIntAt(long index, int value) {
        checkState();
        this.readerWriter.writeIntAt(index, value);
    }
}
