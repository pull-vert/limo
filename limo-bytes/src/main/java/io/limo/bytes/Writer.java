/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.bytes;

/**
 * This interface allow to write binary data
 */
public interface Writer extends AutoCloseable {

	void writeInt(int value);

	/**
	 * Closes all resources that store binary data
	 */
	@Override
	void close();
}
