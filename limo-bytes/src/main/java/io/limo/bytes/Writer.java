/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.bytes;

import java.io.EOFException;

/**
 * This interface allow to write binary data
 */
public interface Writer extends AutoCloseable {

    void writeByte(byte value) throws EOFException;

    void writeInt(int value) throws EOFException;

    /**
     * Closes all resources that store binary data
     */
    @Override
    void close();
}
