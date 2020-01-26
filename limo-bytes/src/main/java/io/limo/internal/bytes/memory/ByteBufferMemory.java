/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.bytes.memory;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;

/**
 * A memory chunk based on a ByteBuffer
 */
public final class ByteBufferMemory implements Memory {

	@NotNull
	private ByteBuffer bb;

	/**
	 * Build a memory chunk from a {@link ByteBuffer}
	 *
	 * @param bb the ByteBuffer
	 */
	public ByteBufferMemory(@NotNull ByteBuffer bb) {
		this.bb = Objects.requireNonNull(bb);
	}

	@Override
	public byte readByteAt(@Range(from = 0, to = Integer.MAX_VALUE) long index) {
		return bb.get((int) index);
	}

	@Override
	public int readIntAt(@Range(from = 0, to = Integer.MAX_VALUE) long index) {
		return bb.getInt((int) index);
	}

	@Override
	public void writeByteAt(@Range(from = 0, to = Integer.MAX_VALUE) long index, byte value) {
		bb.put((int) index, value);
	}

	@Override
	public void writeIntAt(@Range(from = 0, to = Integer.MAX_VALUE) long index, int value) {
		bb.putInt((int) index, value);
	}

	@Override
	@Range(from = 0, to = Integer.MAX_VALUE)
	public long getCapacity() {
		return bb.capacity();
	}

	@Override
	public void setByteOrder(@NotNull ByteOrder byteOrder) {
		bb.order(byteOrder);
	}

	/**
	 * Close is no op because {@link ByteBuffer} does not provide close
	 */
	@Override
	public void close() {
	}
}
