/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.bytes;

/**
 * This interface store a full binary content we can only write to
 */
public interface WritableBinary extends AutoCloseable {

	void writeInt(int value);

	/**
	 * Closes all resources that store the binary content
	 */
	@Override
	void close();
}
