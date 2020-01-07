/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.bytes.memory;

/**
 * This interface is a chunk of memory, that can store all or a part of a binary content we can write and read
 */
public interface Memory extends AutoCloseable {

	int readIntAt(long index);

	void writeIntAt(long index, int value);

	/**
	 * Closes this memory chunk
	 */
	@Override
	void close();
}
