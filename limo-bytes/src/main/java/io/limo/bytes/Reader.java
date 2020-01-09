/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.bytes;

/**
 * This interface allow to read binary data
 */
public interface Reader extends AutoCloseable {

	int readInt();

	/**
	 * Closes all resources that store binary data
	 */
	@Override
	void close();
}