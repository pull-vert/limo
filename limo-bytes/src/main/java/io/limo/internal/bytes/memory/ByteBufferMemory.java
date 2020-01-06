/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.bytes.memory;

import io.limo.bytes.memory.Memory;
import io.limo.common.NotNull;

import java.nio.ByteBuffer;

public final class ByteBufferMemory implements Memory {

	@NotNull ByteBuffer bb;

	public ByteBufferMemory(@NotNull ByteBuffer bb) {
		this.bb = bb;
	}

	@Override
	public int readIntAt(long index) {
		return bb.getInt((int) index);
	}

	@Override
	public void writeIntAt(long index, int value) {
		bb.putInt((int) index, value);
	}
}
