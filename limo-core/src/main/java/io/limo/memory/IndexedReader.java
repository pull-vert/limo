/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.memory;

import io.limo.Reader;

/**
 * This interface allows to read in a memory region using a {@code readIndex}
 */
public interface IndexedReader extends Reader {

    long getReadIndex();

    long getWriteIndex();

    /**
     * Read a byte at the current {@code readIndex}
     *
     * @throws IndexOutOfBoundsException if {@code readIndex} is greater than {@code writeIndex} there is no byte
     * left to read
     * @implSpec increase {@code readIndex} by {@code 1} after read
     */
    @Override
    byte readByte();

    /**
     * Read a 4-bytes int at the current {@code readIndex}
     * <p>bytes are read using BIG ENDIAN byte order
     *
     * @throws IndexOutOfBoundsException if {@code readIndex} is greater than {@code (writeIndex - 3)} there are not
     * enough bytes left to read a 4-bytes int
     * @implSpec increase {@code readIndex} by {@code 4} after read
     */
    @Override
    int readInt();

    /**
     * Read a byte at the specified absolute {@code index}
     *
     * @throws IndexOutOfBoundsException if {@code index} is less than {@code 0} or is greater than {@code writeIndex},
     * then {@code index} is out of the readable bounds
     * @implSpec do not modify {@code readIndex}
     */
    byte readByteAt(long index);

    /**
     * Read a 4-bytes int at the specified absolute {@code index}
     * <p>bytes are read using BIG ENDIAN byte order
     *
     * @throws IndexOutOfBoundsException if {@code index} is less than {@code 0} or is greater than {@code (writeIndex - 3)},
     * then {@code index} is out of the readable bounds
     * @implSpec do not modify {@code readIndex}
     */
    int readIntAt(long index);
}
