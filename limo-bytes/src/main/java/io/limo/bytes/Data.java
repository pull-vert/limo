/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.bytes;

/**
 * This interface contains binary data
 */
public interface Data extends AutoCloseable {

    /**
     * @return the data reader
     */
    Reader getReader();

    /**
     * @return the data writer
     */
    Writer getWriter();

    /**
     * Closes all resources that store binary data
     */
    @Override
    void close();
}
