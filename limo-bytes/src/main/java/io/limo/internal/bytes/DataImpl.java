/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.bytes;

import io.limo.bytes.Data;
import io.limo.bytes.Reader;
import io.limo.bytes.Writer;
import io.limo.common.NotNull;
import io.limo.common.Nullable;
import io.limo.internal.bytes.memory.Memory;
import io.limo.internal.bytes.memory.MemorySupplier;

public final class DataImpl implements Data {

	@NotNull
	private Memory[] data;

	@Nullable
	private MemorySupplier memorySupplier;

	public DataImpl(@NotNull MemorySupplier memorySupplier) {
		this.memorySupplier = memorySupplier;
		this.data = new Memory[]{memorySupplier.get()};
	}

	public DataImpl(@NotNull Memory[] data) {
		this.data = data;
	}

	@NotNull
	@Override
	public Reader getReader() {
		return null;
	}

	@Override
	public Writer getWriter() {
		return null;
	}

	@Override
	public void close() {
		for (var memory : data) {
			memory.close();
		}
	}

	private class ReaderImpl implements Reader {

		/**
		 * Current Memory chunk to read from
		 */
		@NotNull
		private Memory memory;

		/**
		 * Reading index in the current {@link #memory}
		 */
		private long index = 0L;

		public ReaderImpl(@NotNull Memory memory) {
			this.memory = memory;
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
			DataImpl.this.close();
		}
	}
}
