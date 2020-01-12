/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.bytes;

import java.io.EOFException;

/**
 * This interface allow to read binary data
 */
public interface Reader extends AutoCloseable {

	/**
	 * @return a byte that was read from the data, read index increases by 1
	 * @throws EOFException if there is no byte left to read in data
	 */
	byte readByte() throws EOFException;

	/**
	 * @return a 4 bytes int that was read from the data, read index increases by 4
	 * @throws EOFException if there is less than 4 bytes left to read in data
	 */
	int readInt() throws EOFException;

	/**
	 * Closes all resources that store binary data
	 */
	@Override
	void close();
}
