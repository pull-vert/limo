/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.transfer;

import io.limo.memory.ByBuOffHeap;
import io.limo.utils.BytesOps;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.Objects;
import java.util.OptionalInt;
import java.util.stream.Stream;

abstract class AbstractByBuArrayData<T extends ByBuOffHeap> extends AbstractData {

    /**
     * The array of {@link ByBuOffHeap} into which data is stored
     */
    protected T @NotNull [] bybuArray;

    /**
     * Default initial capacity of the array
     */
    static final int DEFAULT_CAPACITY = 4;

    /**
     * The array of limits : one for each {@link ByBuOffHeap}
     */
    int @NotNull [] limits;

    /**
     * Index of the {@link ByBuOffHeap} in array that is currently read
     */
    @Range(from = 0, to = Integer.MAX_VALUE - 1) int readIndex = 0;

    /**
     * Index of the last {@link ByBuOffHeap} in array that has been written
     *
     * @implNote It has a 0 initial value, even if first memory was not written
     */
    @Range(from = 0, to = Integer.MAX_VALUE - 1) int writeIndex = 0;

    @Range(from = 0, to = Integer.MAX_VALUE - 1) long byteSize;

    /**
     * Current {@link ByBuOffHeap} to read from
     */
    private @NotNull T memory;

    /**
     * Reading index in the current {@link #memory}
     */
    private int index = 0;

    /**
     * Number of bytes loaded in the current {@link #memory}
     */
    private int limit;

    /**
     * Current byte sequence is the first in the data array
     */
    protected AbstractByBuArrayData() {
            this.memory = Objects.requireNonNull(bybuArray[0]);
            this.limit = limits[0];
    }

    /**
     * @return next not empty byte sequence index from array, or empty if none exists
     */
    private @NotNull OptionalInt getNextReadIndex() {
        if (this.readIndex < this.writeIndex) {
            return OptionalInt.of(++this.readIndex);
        }
        return OptionalInt.empty();
    }

    @Override
    public final @Range(from = 1, to = Long.MAX_VALUE) long getByteSize() {
        return this.byteSize;
    }

    /*@Override
    public final @Range(from = 0, to = Long.MAX_VALUE - 1) long getLimit() {
        // sum of all limits
        var totalLimit = 0L;
        for (final var limit : this.limits) {
            totalLimit += limit;
        }
        return totalLimit;
    }*/

    /**
     * closes all not null bytes in bytes array
     */
    @Override
    public final void close() {
        Stream.of(this.bybuArray)
                .filter(Objects::nonNull)
                .forEach(ByBuOffHeap::close);
    }

    @Override
    public byte readByte() {
        final var currentIndex = this.index;
        final var currentLimit = this.limit;
        final var byteSize = 1;
        final var targetLimit = currentIndex + byteSize;

        // 1) at least 1 byte left to read a byte in current byte sequence
        if (currentLimit >= targetLimit) {
            this.index = targetLimit;
            return this.memory.readByteAt(currentIndex);
        }

        // 2) current byte sequence is exactly exhausted
        // let's get next byte sequence and if present read it
        nextMemory();

        // we are at 0 index in newly obtained byte sequence

        if (this.limit >= byteSize) {
            this.index = byteSize;
            return this.memory.readByteAt(0);
        }

        // 3) memory is exhausted
        throw new IndexOutOfBoundsException();
    }

    @Override
    public int readInt() {
        final var currentIndex = this.index;
        final var currentLimit = this.limit;
        final var intSize = 4;
        final var targetLimit = currentIndex + intSize;

        // 1) at least 4 bytes left to read an int in current byte sequence
        if (currentLimit >= targetLimit) {
            this.index = targetLimit;
            return this.memory.readIntAt(currentIndex);
        }

        // 2) current byte sequence is exactly exhausted
        if (currentLimit == currentIndex) {
            // let's get next byte sequence and if present read it
            nextMemory();

            // we are at 0 index in newly obtained byte sequence
            if (this.limit >= intSize) {
                this.index = intSize;
                return this.memory.readIntAt(0);
            }

            // 3) memory is exhausted
            throw new IndexOutOfBoundsException();
        }

        // 3) must read some bytes in current byte sequence, some others from next one
        return BytesOps.bytesToInt(readByte(), readByte(), readByte(), readByte());
    }

    @Override
    public int readIntLE() {
        final var currentIndex = this.index;
        final var currentLimit = this.limit;
        final var intSize = 4;
        final var targetLimit = currentIndex + intSize;

        // 1) at least 4 bytes left to read an int in current byte sequence
        if (currentLimit >= targetLimit) {
            this.index = targetLimit;
            return this.memory.readIntAtLE(currentIndex);
        }

        // 2) current byte sequence is exactly exhausted
        if (currentLimit == currentIndex) {
            // let's get next byte sequence and if present read it
            nextMemory();

            // we are at 0 index in newly obtained byte sequence
            if (this.limit >= intSize) {
                this.index = intSize;
                return this.memory.readIntAtLE(0);
            }

            // 3) memory is exhausted
            throw new IndexOutOfBoundsException();
        }

        // 3) must read some bytes in current byte sequence, some others from next one
        return BytesOps.bytesToIntLE(readByte(), readByte(), readByte(), readByte());
    }

    /**
     * Switch to next byte sequence because current one is exhausted
     *
     * @throws IndexOutOfBoundsException if no readable next byte sequence
     */
    private void nextMemory() {
        final var nextReadIndex = getNextReadIndex().orElseThrow(IndexOutOfBoundsException::new);
        this.memory = Objects.requireNonNull(bybuArray[nextReadIndex]);
        this.limit = limits[nextReadIndex];
    }
}
