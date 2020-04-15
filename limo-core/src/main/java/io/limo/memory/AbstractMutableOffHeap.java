/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.memory;

import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.util.Objects;

@SuppressWarnings("unchecked")
public abstract class AbstractMutableOffHeap<T extends AbstractMutableOffHeap<T>> extends AbstractOffHeap<T> implements MutableOffHeap {

    private long writeIndex;

    protected AbstractMutableOffHeap(@NotNull ByteBuffer baseByBu) {
        super(Objects.requireNonNull(baseByBu), false);
    }

    @Override
    public long getWriteIndex() {
        return this.writeIndex;
    }

    /**
     * {@inheritDoc}
     * @implSpec {@inheritDoc}
     */
    @Override
    public T writeByte(byte value) {
        if (this.writeIndex >= getByteSize()) {
            throw new IndexOutOfBoundsException(
                    String.format("writeIndex=%d is greater or equal than byteSize=%d, there is no room left to " +
                                    "write a byte", this.writeIndex, getByteSize()));
        }
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
        if (this.writeIndex > getByteSize() - 4) {
            throw new IndexOutOfBoundsException(
                    String.format("writeIndex=%d is greater than (byteSize=%d - 4), there is no room left to write a " +
                            "4-byte int", this.writeIndex, getByteSize()));
        }
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
        if (index < 0 || index > getWriteIndex() || index >= getByteSize()) {
            throw new IndexOutOfBoundsException(
                    String.format("requested index=%d is less than 0 or greater than writeIndex=%d or greater or equals " +
                                    "than byteSize=%d, it is out of the writable bounds",
                            index, getWriteIndex(), getByteSize()));
        }
        writeByteAtNoIndexCheck(index, value);
        return (T) this;
    }

    /**
     * {@inheritDoc}
     * @implSpec {@inheritDoc}
     */
    @Override
    public T writeIntAt(long index, int value) {
        if (index < 0 || index > getWriteIndex() || index > getByteSize() - 4) {
            throw new IndexOutOfBoundsException(
                    String.format("requested index=%d is less than 0 or greater than writeIndex=%d or greater than " +
                                    "(byteSize=%d - 4), it is out of the writable bounds",
                            index, getWriteIndex(), getByteSize()));
        }
        writeIntAtNoIndexCheck(index, value);
        return (T) this;
    }

    protected abstract void writeByteAtNoIndexCheck(long index, byte value);

    protected abstract void writeIntAtNoIndexCheck(long index, int value);
}
