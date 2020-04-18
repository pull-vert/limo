/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.memory;

import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.util.Objects;

@SuppressWarnings("unchecked")
public abstract class AbstractMutableOffHeap<T extends MutableOffHeap> extends AbstractOffHeap<T> implements MutableOffHeap {

    protected AbstractMutableOffHeap(@NotNull ByteBuffer baseByBu) {
        super(Objects.requireNonNull(baseByBu), 0, false);
    }

    /**
     * {@inheritDoc}
     * @implSpec {@inheritDoc}
     */
    @Override
    public T writeByte(byte value) {
        existingIndexCheck(this.writeIndex, getByteSize(), 1);
        final var index = this.writeIndex;
        writeByteAtNoIndexCheck(index, value);
        this.writeIndex = index + 1;
        return (T) this;
    }

    /**
     * {@inheritDoc}
     * @implSpec {@inheritDoc}
     */
    @Override
    public T writeInt(int value) {
        existingIndexCheck(this.writeIndex, getByteSize(), 4);
        final var index = this.writeIndex;
        writeIntAtNoIndexCheck(index, value);
        this.writeIndex = index + 4;
        return (T) this;
    }

    /**
     * {@inheritDoc}
     * @implSpec {@inheritDoc}
     */
    @Override
    public T writeByteAt(long index, byte value) {
        indexCheck(index, getByteSize(), 1);
        writeByteAtNoIndexCheck(index, value);
        return (T) this;
    }

    /**
     * {@inheritDoc}
     * @implSpec {@inheritDoc}
     */
    @Override
    public T writeIntAt(long index, int value) {
        indexCheck(index, getByteSize(), 4);
        writeIntAtNoIndexCheck(index, value);
        return (T) this;
    }

    protected abstract void writeByteAtNoIndexCheck(long index, byte value);

    protected abstract void writeIntAtNoIndexCheck(long index, int value);
}
