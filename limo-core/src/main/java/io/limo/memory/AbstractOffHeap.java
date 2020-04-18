/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.memory;

import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.util.Objects;

/**
 * Base abstract implementation of {@link OffHeap} memory
 */
public abstract class AbstractOffHeap<T extends OffHeap> implements OffHeap {

    private long readIndex;
    long writeIndex;

    /**
     * Depending on byteSize :
     * <ul>
     *     <li>if byteSize < Integer.MAX_VALUE this ByteBuffer covers this full memory region</li>
     *     <li>if byteSize > Integer.MAX_VALUE this ByteBuffer covers first Integer.MAX_VALUE bytes of memory
     *     region</li>
     * </ul>
     */
    final @NotNull ByteBuffer baseByBu;

    /**
     * Instantiate a AbstractOffHeap from a ByteBuffer, can be readonly
     */
    protected AbstractOffHeap(@NotNull ByteBuffer baseByBu, long limit, boolean isReadonly) {
        if (!Objects.requireNonNull(baseByBu).isDirect()) {
            throw new IllegalArgumentException("Provided ByteBuffer must be Direct");
        }
        if (isReadonly) {
            if (!baseByBu.isReadOnly()) {
                this.baseByBu = baseByBu.asReadOnlyBuffer();
            } else {
                this.baseByBu = baseByBu;
            }
        } else {
            if (baseByBu.isReadOnly()) {
                throw new IllegalArgumentException("Provided ByteBuffer must not be readOnly");
            }
            this.baseByBu = baseByBu;
        }
        this.writeIndex = limit;
    }

    @Override
    public final byte @NotNull [] toByteArray() {
        if (getByteSize() > Integer.MAX_VALUE) {
            throw new UnsupportedOperationException(
                    String.format("This off-heap memory's size is too big to export to a byte array : byteSize=%d", getByteSize()));
        }
        checkState();
        return toByteArrayNoIndexCheck();
    }

    @Override
    public final long getReadIndex() {
        return this.readIndex;
    }

    @Override
    public final long getWriteIndex() {
        return this.writeIndex;
    }

    /**
     * {@inheritDoc}
     *
     * @implSpec {@inheritDoc}
     */
    @Override
    public final byte readByte() {
        existingIndexCheck(this.readIndex, this.writeIndex, 1);
        final var index = this.readIndex;
        final var val = readByteAtNoIndexCheck(index);
        this.readIndex = index + 1;
        return val;
    }

    /**
     * {@inheritDoc}
     *
     * @implSpec {@inheritDoc}
     */
    @Override
    public final int readInt() {
        existingIndexCheck(this.readIndex, this.writeIndex, 4);
        final var index = this.readIndex;
        final var val = readIntAtNoIndexCheck(index);
        this.readIndex = index + 4;
        return val;
    }

    /**
     * {@inheritDoc}
     *
     * @implSpec {@inheritDoc}
     */
    @Override
    public final byte readByteAt(long index) {
        indexCheck(index, this.writeIndex, 1);
        return readByteAtNoIndexCheck(index);
    }

    /**
     * {@inheritDoc}
     *
     * @implSpec {@inheritDoc}
     */
    @Override
    public final int readIntAt(long index) {
        indexCheck(index, this.writeIndex, 4);
        return readIntAtNoIndexCheck(index);
    }

    /**
     * Check it is ok to do an operation on this off-heap memory
     *
     * @throws IllegalStateException if this memory has been closed, or if access occurs from a thread other
     *                               than the thread owning this memory.
     */
    protected abstract void checkState();

    protected abstract byte[] toByteArrayNoIndexCheck();

    protected abstract byte readByteAtNoIndexCheck(long index);

    protected abstract int readIntAtNoIndexCheck(long index);

    protected static void sliceIndexCheck(long offset, long length, long byteSize) {
        if ((offset | length) < 0 || offset > byteSize || length > (byteSize - offset)) {
            throw new IndexOutOfBoundsException(
                    String.format("Incorrect parameters to slice : offset=%d, length=%d, byteSize=%d",
                            offset, length, byteSize));
        }
    }

    /**
     * Checks that there is at least 'requestedLength' bytes available
     */
    protected static void indexCheck(long index, long limit, int requestedLength) {
        if ((index | limit - index - requestedLength) < 0) {
            throw new IndexOutOfBoundsException(
                    String.format("requested index=%d is less than 0 or greater than (limit=%d - %d)",
                            index, limit, requestedLength));
        }
    }

    /**
     * Checks that there is at least 'requestedLength' bytes available
     */
    protected static void existingIndexCheck(long index, long limit, int requestedLength) {
        if (index > limit - requestedLength) {
            throw new IndexOutOfBoundsException(
                    String.format("requested index=%d is greater than (limit=%d - %d)",
                            index, limit, requestedLength));
        }
    }
}
