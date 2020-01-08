/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.bytes;

import io.limo.bytes.Reader;
import io.limo.bytes.memory.Memory;
import io.limo.bytes.memory.MemorySupplier;
import io.limo.common.NotNull;

public final class ReadableBytes implements Reader {

	/**
	 * Current Memory chunk to read from.
	 */
	@NotNull
	private Memory memory;

	/**
	 * Reading index in the current {@link #memory}.
	 */
	private long index = 0L;
	
	@NotNull
	private final MemorySupplier memorySupplier;

	public ReadableBytes(@NotNull MemorySupplier memorySupplier) {
		this.memorySupplier = memorySupplier;
		memory = memorySupplier.get();
	}

	@Override
	public int readInt() {
		final var currentIndex = index;
		final var intSize = 4;
		final var requestedIndex = currentIndex + intSize;
		// enough bytes left to read
		if (memory.isValidIndex(requestedIndex)) {
			this.index = requestedIndex;
			return memory.readIntAt(currentIndex);
		}
		return -1;
	}

	@Override
	public void close() {

	}
}
