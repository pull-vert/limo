/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.bytes;

/**
 * Util class providing operations on bytes
 */
final class BytesOps {

	// uninstanciable
	private BytesOps() { }


	static int bytesToInt(final byte b0, final byte b1, final byte b2, final byte b3, final boolean isBigEndian) {
		if (isBigEndian) {
			return (b0 << 24) | ((b1 & 0xff) << 16) | ((b2 & 0xff) << 8) | (b3 & 0xff);
		}
		return (b3 << 24) | ((b2 & 0xff) << 16) | ((b1 & 0xff) << 8) | (b0 & 0xff);
	}

	static byte[] intToBytes(final int data, final boolean isBigEndian) {
		if (isBigEndian) {
			return new byte[]{
				(byte) ((data >> 24) & 0xff),
				(byte) ((data >> 16) & 0xff),
				(byte) ((data >> 8) & 0xff),
				(byte) (data & 0xff)
			};
		}
		return new byte[]{
			(byte) (data & 0xff),
			(byte) ((data >> 8) & 0xff),
			(byte) ((data >> 16) & 0xff),
			(byte) ((data >> 24) & 0xff)
		};
	}
}
