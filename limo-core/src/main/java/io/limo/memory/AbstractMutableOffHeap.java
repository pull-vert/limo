/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.memory;

public abstract class AbstractMutableOffHeap extends AbstractOffHeap implements MutableOffHeap {

    private long writeIndex;

    protected AbstractMutableOffHeap(long baseAddress) {
        super(baseAddress);
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
    public MutableOffHeap writeByte(byte value) {
        if (this.writeIndex >= getByteSize()) {
            throw new IndexOutOfBoundsException(
                    String.format("writeIndex=%d is greater or equal than byteSize=%d, there is no room left to " +
                                    "write a byte", this.writeIndex, getByteSize()));
        }
        final var index = this.writeIndex;
        writeByteAtNoIndexCheck(index, value);
        this.writeIndex = index + 1;
        return this;
    }

    /**
     * {@inheritDoc}
     * @implSpec {@inheritDoc}
     */
    @Override
    public MutableOffHeap writeInt(int value) {
        if (this.writeIndex > getByteSize() - 4) {
            throw new IndexOutOfBoundsException(
                    String.format("writeIndex=%d is greater than (byteSize=%d - 4), there is no room left to write a " +
                            "4-byte int", this.writeIndex, getByteSize()));
        }
        final var index = this.writeIndex;
        writeIntAtNoIndexCheck(index, value);
        this.writeIndex = index + 4;
        return this;
    }

    /**
     * {@inheritDoc}
     * @implSpec {@inheritDoc}
     */
    @Override
    public MutableOffHeap writeByteAt(long index, byte value) {
        if (index < 0 || index > getWriteIndex() || index >= getByteSize()) {
            throw new IndexOutOfBoundsException(
                    String.format("requested index=%d is less than 0 or greater than writeIndex=%d or greater or equals " +
                                    "than byteSize=%d, it is out of the writable bounds",
                            index, getWriteIndex(), getByteSize()));
        }
        writeByteAtNoIndexCheck(index, value);
        return this;
    }

    /**
     * {@inheritDoc}
     * @implSpec {@inheritDoc}
     */
    @Override
    public MutableOffHeap writeIntAt(long index, int value) {
        if (index < 0 || index > getWriteIndex() || index > getByteSize() - 4) {
            throw new IndexOutOfBoundsException(
                    String.format("requested index=%d is less than 0 or greater than writeIndex=%d or greater than " +
                                    "(byteSize=%d - 4), it is out of the writable bounds",
                            index, getWriteIndex(), getByteSize()));
        }
        writeIntAtNoIndexCheck(index, value);
        return this;
    }

    protected abstract void writeByteAtNoIndexCheck(long index, byte value);

    protected abstract void writeIntAtNoIndexCheck(long index, int value);
}
