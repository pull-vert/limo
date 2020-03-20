/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.bytes;

import org.jetbrains.annotations.Range;

/**
 * A read-write (mutable) byte sequence that store all or a part of a complete binary content
 */
public interface MutableBytes extends Bytes {

    void writeByteAt(@Range(from = 0, to = Integer.MAX_VALUE - 1) int index, byte value);

    void writeIntAt(@Range(from = 0, to = Integer.MAX_VALUE - 1) int index, int value, boolean isBigEndian);
}
