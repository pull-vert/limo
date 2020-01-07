/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.bytes;

/**
 * This interface store a full binary content we can only read from
 */
public interface ReadableBinary extends AutoCloseable {

	int readInt();

	/**
	 * Closes all resources that store the binary content
	 */
	@Override
	void close();
}
