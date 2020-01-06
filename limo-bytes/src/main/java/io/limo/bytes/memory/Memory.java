/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.bytes.memory;

public interface Memory {

	int readIntAt(long index);

	void writeIntAt(long index, int value);
}
