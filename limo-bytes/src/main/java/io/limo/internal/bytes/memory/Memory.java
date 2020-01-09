/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.bytes.memory;

/**
 * A memory chunk that can store all or a part of a binary content
 */
public interface Memory extends AutoCloseable {

	int readIntAt(long index);

	void writeIntAt(long index, int value);

	boolean isValidIndex(long requestedIndex);

	/**
	 * Closes this memory chunk
	 */
	@Override
	void close();
}
