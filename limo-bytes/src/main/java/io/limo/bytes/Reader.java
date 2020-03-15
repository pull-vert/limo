/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.bytes;

/**
 * This interface allows to read binary data
 */
public interface Reader extends AutoCloseable {

    /**
     * @return a byte that was read from the data, read index increases by 1
     * @throws ReaderUnderflowException if there is no byte left to read in data
     */
    byte readByte();

    /**
     * @return a 4 bytes int that was read from the data, read index increases by 4
     * @throws ReaderUnderflowException if there is less than 4 bytes left to read in data
     */
    int readInt();

    /**
     * Closes all resources that store binary data
     */
    @Override
    void close();
}
