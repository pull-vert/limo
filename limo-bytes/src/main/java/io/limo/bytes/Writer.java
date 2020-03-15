/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.bytes;

/**
 * This interface allows to write binary data
 */
public interface Writer extends AutoCloseable {

    /**
     * writes a byte in the data, write index increases by 1
     * @throws WriterOverflowException if there is no room in data to write a byte
     */
    void writeByte(byte value);

    /**
     * writes an int in the data, write index increases by 4
     * @throws WriterOverflowException if there is no room in data to write an int (4 bytes)
     */
    void writeInt(int value);

    /**
     * Closes all resources that store binary data
     */
    @Override
    void close();
}
