/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.bytes.memory;

/**
 * A memory chunk that can store all or a part of a binary content
 */
public interface Memory extends AutoCloseable {

	byte readByteAt(long index);

	int readIntAt(long index);

	void writeByteAt(long index, byte value);

	void writeIntAt(long index, int value);

	/**
	 * @return Total capacity, in bytes
	 */
	long getCapacity();

	/**
	 * Closes this memory chunk
	 */
	@Override
	void close();
}
