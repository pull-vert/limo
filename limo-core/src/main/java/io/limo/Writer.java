/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo;

/**
 * This interface allows to write some binary data
 */
public interface Writer {

    /**
     * Writes a byte in the data
     *
     * @throws IndexOutOfBoundsException if there is no room in data to write a byte
     */
    void writeByte(byte value);

    /**
     * Writes a 4-byte int in the data
     * <p>bytes are written using BIG ENDIAN byte order
     *
     * @throws IndexOutOfBoundsException if there is no room in data to write an int (4 bytes)
     */
    void writeInt(int value);
}
