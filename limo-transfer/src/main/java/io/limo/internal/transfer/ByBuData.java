/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.transfer;

import io.limo.memory.ByBuOffHeap;
import io.limo.transfer.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.Objects;

/**
 * Implementation of the immutable {@link Data} interface based on a single {@link ByBuOffHeap}
 */
public class ByBuData implements Data {

    /**
     * The byte sequence into which the elements of this BytesData are stored
     */
    final @NotNull ByBuOffHeap bybu;

    private int readIndex;
    int writeIndex;

    public ByBuData(@NotNull ByBuOffHeap bybu, int limit) {
        this.bybu = Objects.requireNonNull(bybu);
        this.writeIndex = limit;
    }

    @Override
    public final @Range(from = 1, to = Integer.MAX_VALUE) long getByteSize() {
        return this.bybu.getByteSize();
    }

    @Override
    public final long getReadIndex() {
        return this.readIndex;
    }

    @Override
    public final @Range(from = 0, to = Long.MAX_VALUE - 1) long getWriteIndex() {
        return this.writeIndex;
    }

    /**
     * Closes associated {@link #bybu}
     */
    @Override
    public final void close() {
        this.bybu.close();
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
        final var val = this.bybu.readByteAt(index);
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
        final var val = this.bybu.readIntAt(index);
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
        return this.bybu.readByteAt(index);
    }

    /**
     * {@inheritDoc}
     *
     * @implSpec {@inheritDoc}
     */
    @Override
    public final int readIntAt(long index) {
        indexCheck(index, this.writeIndex, 4);
        return this.bybu.readIntAt(index);
    }

    /**
     * Checks that there is at least 'requestedLength' bytes available
     */
    protected static void indexCheck(long index, int limit, int requestedLength) {
        if ((index | limit - index - requestedLength) < 0) {
            throw new IndexOutOfBoundsException(
                    String.format("requested index=%d is less than 0 or greater than (limit=%d - %d)",
                            index, limit, requestedLength));
        }
    }

    /**
     * Checks that there is at least 'requestedLength' bytes available
     */
    protected static void existingIndexCheck(int index, int limit, int requestedLength) {
        if (index > limit - requestedLength) {
            throw new IndexOutOfBoundsException(
                    String.format("requested index=%d is greater than (limit=%d - %d)",
                            index, limit, requestedLength));
        }
    }
}
