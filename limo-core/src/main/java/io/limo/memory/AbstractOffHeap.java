/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.memory;

import org.jetbrains.annotations.NotNull;

/**
 * Base abstract implementation of {@link OffHeap} memory
 */
public abstract class AbstractOffHeap implements OffHeap {

    private long readIndex;

    // this field is present for unsafe operations
    @SuppressWarnings("unused")
    private final long baseAddress;

    protected AbstractOffHeap(long baseAddress) {
        this.baseAddress = baseAddress;
    }

    @Override
    public @NotNull ByteBufferOffHeap slice(long offset, int length) {
        if ((offset | length) < 0 || offset > getByteSize() || length > (getByteSize() - offset)) {
            throw new IndexOutOfBoundsException(
                    String.format("Incorrect parameters to slice : offset=%d, length=%d, byteSize=%d",
                            offset, length, getByteSize()));
        }
        return sliceNoIndexCheck(offset, length);
    }

    @Override
    public byte @NotNull [] toByteArray() {
        if (getByteSize() > Integer.MAX_VALUE) {
            throw new UnsupportedOperationException(
                    String.format("This off-heap memory's size is too big to export to a byte array : byteSize=%d", getByteSize()));
        }
        return toByteArrayNoIndexCheck();
    }

    @Override
    public long getReadIndex() {
        return this.readIndex;
    }

    @Override
    public byte readByte() {
        if (this.readIndex > getWriteIndex()) {
            throw new IndexOutOfBoundsException(
                    String.format("readIndex=%d is greater than writeIndex=%d, there is no byte left to read",
                            this.readIndex, getWriteIndex()));
        }
        final var index = this.readIndex;
        final var val = readByteAtNoIndexCheck(index);
        this.readIndex = index + 1;
        return val;
    }

    @Override
    public int readInt() {
        if (this.readIndex > getWriteIndex() - 3) {
            throw new IndexOutOfBoundsException(
                    String.format("readIndex=%d is greater to (writeIndex=%d - 3), " +
                                    "there are not enough bytes left to read a 4-bytes int",
                            this.readIndex, getWriteIndex()));
        }
        final var index = this.readIndex;
        final var val = readIntAtNoIndexCheck(index);
        this.readIndex = index + 4;
        return val;
    }

    @Override
    public byte readByteAt(long index) {
        if (index < 0 || index > getWriteIndex()) {
            throw new IndexOutOfBoundsException(
                    String.format("requested index=%d is less than 0 or greater than writeIndex=%d, " +
                                    "it is out of the readable bounds",
                            index, getWriteIndex()));
        }
        return readByteAtNoIndexCheck(index);
    }

    @Override
    public int readIntAt(long index) {
        if (index < 0 || index > getWriteIndex()) {
            throw new IndexOutOfBoundsException(
                    String.format("requested index=%d is less than 0 or greater than writeIndex=%d, " +
                                    "it is out of the readable bounds",
                            index, getWriteIndex()));
        }
        return readIntAtNoIndexCheck(index);
    }

    protected abstract ByteBufferOffHeap sliceNoIndexCheck(long offset, int length);

    protected abstract byte[] toByteArrayNoIndexCheck();

    protected abstract byte readByteAtNoIndexCheck(long index);

    protected abstract int readIntAtNoIndexCheck(long index);
}
