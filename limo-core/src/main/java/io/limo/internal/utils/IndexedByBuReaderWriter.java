/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.utils;

/**
 * Write operations in ByteBuffer without any index bound check (very dangerous !!)
 */
public interface IndexedByBuReaderWriter {
    byte readByteAt(long index);
    int readIntAt(long index);
    void writeByteAt(long index, byte value);
    void writeIntAt(long index, int value);
    byte[] toByteArray();
}
