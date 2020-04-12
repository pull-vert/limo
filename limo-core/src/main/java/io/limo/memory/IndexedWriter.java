/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.memory;

import io.limo.Writer;

public interface IndexedWriter extends Writer {

    /**
     * Write a byte at the current {@code writeIndex}
     *
     * @throws IndexOutOfBoundsException if {@code writeIndex} is equal to {@code (byteSize - 1)} so there is no room
     * left to write
     * @implSpec increase {@code writeIndex} by {@code 1} after write
     */
    @Override
    void writeByte(byte value);

    /**
     * Write a 4-byte int at the current {@code writeIndex}
     * <p>bytes are written using BIG ENDIAN byte order
     *
     * @throws IndexOutOfBoundsException if {@code writeIndex} is greater than {@code (byteSize - 5)} so there is no room
     * left to write a 4-byte int
     * @implSpec increase {@code writeIndex} by {@code 4} after write
     */
    @Override
    void writeInt(int value);

    /**
     * Write a byte at the specified absolute {@code index}
     *
     * @throws IndexOutOfBoundsException if {@code index} is less than {@code 0} or if {@code index} is greater
     * than {@code writeIndex}
     * @implSpec do not modify {@code writeIndex}
     */
    void writeByteAt(long index, byte value);
}
