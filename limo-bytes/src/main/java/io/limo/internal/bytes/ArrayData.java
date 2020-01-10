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

/**
 * Resizable-array implementation of the {@code Data} interface.
 *
 * @implNote Inspired by ArrayList
 * @see Data
 */
public final class ArrayData implements Data {

	/**
	 * Default initial capacity.
	 */
	private static final int DEFAULT_CAPACITY = 4;

	/**
	 * The array buffer into which the elements of the ArrayData are stored.
	 */
	@NotNull
	private Memory[] data;

	/**
	 * The memory supplier, can act as a pool
	 */
	@Nullable
	private MemorySupplier memorySupplier;

	/**
	 * The data reader
	 */
	@NotNull
	private Reader reader;

	public ArrayData(@NotNull MemorySupplier memorySupplier) {
		this.memorySupplier = memorySupplier;
		final var initialMemory = memorySupplier.get();
		reader = new ReaderImpl(initialMemory);
		// init data with DEFAULT_CAPACITY size and first element in data = initialMemory
		this.data = new Memory[DEFAULT_CAPACITY];
		data[0] = initialMemory;
	}

	public ArrayData(@NotNull Memory[] data, @Nullable MemorySupplier memorySupplier) {
		if (data.length == 0) {
			throw new IllegalArgumentException("data must not be empty");
		}
		reader = new ReaderImpl(data[0]);
		this.data = data;
		this.memorySupplier = memorySupplier;
	}

	@NotNull
	@Override
	public Reader getReader() {
		return reader;
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

	/**
	 *Implementation of the {@code Reader} interface that reads in data array of {@code ArrayData}
	 */
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
			// 1) enough bytes left to read an int in memory
			if (memory.isValidIndex(requestedIndex)) {
				this.index = requestedIndex;
				return memory.readIntAt(currentIndex);
			}
			return -1;
		}

		@Override
		public void close() {
			ArrayData.this.close();
		}
	}
}
