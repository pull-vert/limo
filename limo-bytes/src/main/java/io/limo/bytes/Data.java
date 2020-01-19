/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.bytes;

import io.limo.common.NotNull;

import java.nio.ByteOrder;

/**
 * A complete binary data
 */
public interface Data extends AutoCloseable {

	/**
	 * @return the data reader
	 */
	@NotNull
	Reader getReader();

	/**
	 * @return the data writer
	 */
	@NotNull
	Writer getWriter();

	/**
	 * todo replace by jdk14 new enumeration for byte order
	 * Retrieves this data's byte order.
	 *
	 * <p> The byte order is used when reading or writing multibyte values.
	 * The order of a newly-created data is always {@link ByteOrder#BIG_ENDIAN
	 * BIG_ENDIAN}.  </p>
	 *
	 * @return This data's byte order
	 */
	@NotNull
	ByteOrder getByteOrder();

	/**
	 * Closes all resources that store binary data
	 */
	@Override
	void close();
}
