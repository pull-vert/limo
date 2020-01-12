/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.bytes;

/**
 * Util class with operations on bytes
 */
final class BytesOps {

	// uninstanciable
	private BytesOps() { }


	static int bytesToInt(byte b0, byte b1, byte b2, byte b3, boolean isBigEndian) {
		if (isBigEndian) {
			return (b0 << 24) | ((b1 & 0xff) << 16) | ((b2 & 0xff) << 8) | (b3 & 0xff);
		}
		return (b3 << 24) | ((b2 & 0xff) << 16) | ((b1 & 0xff) << 8) | (b0 & 0xff);
	}
}
