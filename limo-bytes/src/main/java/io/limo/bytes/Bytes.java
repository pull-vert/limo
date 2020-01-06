/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.bytes;

public interface Bytes extends AutoCloseable {

	int readInt();

	void writeInt(int value);
}
