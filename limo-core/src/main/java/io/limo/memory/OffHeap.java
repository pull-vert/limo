/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.memory;

import org.jetbrains.annotations.NotNull;

/**
 * This interface represents a off-heap memory region
 */
public interface OffHeap extends /*IndexedReader, */AutoCloseable {

    long getByteSize();

    /**
     * Obtains a new off-heap memory view whose base address is the same as the base address of this memory plus a given offset,
     * new size is specified by the given argument.
     * @param offset The new memory base offset (relative to the current memory base address), specified in bytes.
     * @param length The new memory length, specified in bytes.
     * @return a new ByteBufferOffHeap (off-heap memory represented by a direct ByteBuffer) view with updated base position and limit addresses.
     * @throws IndexOutOfBoundsException if {@code offset < 0}, {@code offset > getByteSize()}, {@code length < 0}, or {@code length > getByteSize() - offset}
     * @throws IllegalStateException if this memory has been closed, or if access occurs from a thread other than the
     * thread owning this memory.
     */
    @NotNull ByteBufferOffHeap asSlice(long offset, int length);

    /**
     * Copy the contents of this off-heap memory into a fresh byte array.
     * @return a fresh byte array copy of this off-heap memory.
     * @throws UnsupportedOperationException if this off-heap memory's contents cannot be copied into a {@link byte[]} instance,
     * e.g. its size is greater than {@link Integer#MAX_VALUE}.
     * @throws IllegalStateException if this memory has been closed, or if access occurs from a thread other than the
     * thread owning this memory.
     */
    byte @NotNull [] toByteArray();

    /**
     * Closes this off-heap memory. Once a off-heap memory has been closed, any attempt to use the off-heap memory,
     * or to access the memory associated with the it will fail with {@link IllegalStateException}. Calling this method
     * trigger deallocation of the off-heap memory.
     * @throws IllegalStateException if this memory has been closed, or if access occurs from a thread other than the
     * thread owning this memory
     */
    @Override
    void close();
}
