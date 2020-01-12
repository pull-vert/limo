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

import java.io.EOFException;
import java.nio.ByteOrder;
import java.util.OptionalInt;

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
	 * The array buffer into which the elements of the ArrayData are stored
	 */
	@NotNull
	private Memory[] data;

	/**
	 * The array of limits for each memory
	 */
	private long[] limits;

	/**
	 * Index of the memory in data that is currently read
	 */
	private int readIndex = 0;

	/**
	 * Max index of the memory in data that has been written
	 *
	 * @implNote It has a 0 initial value, even if first memory was not written
	 */
	private int writeIndex = 0;

	/**
	 * The memory supplier, can act as a pool
	 */
	@Nullable
	private MemorySupplier memorySupplier;

	boolean isBigEndian = true;

	/**
	 * The data reader
	 */
	@NotNull
	private Reader reader;

	public ArrayData(@NotNull MemorySupplier memorySupplier) {
		this.memorySupplier = memorySupplier;
		final var initialMemory = memorySupplier.get();
		// init data with DEFAULT_CAPACITY size and first element in data = initialMemory
		this.data = new Memory[DEFAULT_CAPACITY];
		data[0] = initialMemory;
		reader = new ReaderImpl();
		this.limits = new long[DEFAULT_CAPACITY];
	}

	public ArrayData(@NotNull Data data, @Nullable MemorySupplier memorySupplier) {
		// todo use instanceof pattern matching of java 14 https://openjdk.java.net/jeps/305
		if (data instanceof ArrayData) {
			final var arrayData = (ArrayData) data;
			if (arrayData.data.length == 0) {
				throw new IllegalArgumentException("data array must not be empty");
			}
			this.data = arrayData.data;
			this.limits = arrayData.limits;
		} else {
			throw new IllegalArgumentException("data type " + data.getClass().getTypeName() + " is unsupported");
		}
		reader = new ReaderImpl();
		this.memorySupplier = memorySupplier;
	}

	/**
	 * @return next not empty chunk of memory, or empty if none exists
	 */
	private OptionalInt getNextReadIndex() {
		if (readIndex < writeIndex) {
			return OptionalInt.of(++readIndex);
		}
		return OptionalInt.empty();
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

	@NotNull
	@Override
	public ByteOrder getByteOrder() {
		return isBigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
	}

	@Override
	public void close() {
		for (var memory : data) {
			memory.close();
		}
	}

	/**
	 * Implementation of the {@code Reader} interface that reads in data array of {@code ArrayData}
	 */
	private final class ReaderImpl implements Reader {

		/**
		 * Current Memory chunk to read from
		 */
		@NotNull
		private Memory memory;

		/**
		 * Reading index in the current {@link #memory}
		 */
		private long index = 0L;

		/**
		 * Number of bytes loaded in the current {@link #memory}
		 */
		private long limit;

		/**
		 * Current memory is the first of data array of {@code ArrayData}
		 */
		public ReaderImpl() {
			memory = data[0];
			limit = limits[0];
		}

		@Override
		public byte readByte() throws EOFException {
			final var currentIndex = index;
			final var currentLimit = limit;
			final var byteSize = 1;
			final var targetLimit = currentIndex + byteSize;

			// 1) at least 1 byte left to read a byte in current memory
			if (currentLimit >= targetLimit) {
				index = targetLimit;
				return memory.readByteAt(currentIndex);
			}

			// 2) current memory is exactly exhausted
			// let's get next chunk of data and if present read it
			nextMemory();

			// we are at 0 index in newly obtained memory

			if (limit >= byteSize) {
				index = byteSize;
				return memory.readByteAt(0);
			}
			throw new EOFException("End of file while reading memory");
		}

		@Override
		public int readInt() throws EOFException {
			final var currentIndex = index;
			final var currentLimit = limit;
			final var intSize = 4;
			final var targetLimit = currentIndex + intSize;

			// 1) at least 4 bytes left to read an int in current memory
			if (currentLimit >= targetLimit) {
				index = targetLimit;
				return memory.readIntAt(currentIndex);
			}

			// 2) current memory is exactly exhausted
			if (currentLimit == currentIndex) {
				// let's get next chunk of data and if present read it
				nextMemory();

				// we are at 0 index in newly obtained memory

				if (limit >= intSize) {
					index = intSize;
					return memory.readIntAt(0);
				}
				throw new EOFException("End of file while reading memory");
			}

			// 3) must read some bytes in current chunk, some others from next one
			return BytesOps.bytesToInt(readByte(), readByte(), readByte(), readByte(), isBigEndian);
		}

		/**
		 * Switch to next memory chunk because current memory is exhausted
		 *
		 * @throws EOFException if no readable next memory
		 */
		private void nextMemory() throws EOFException {
			final var nextReadIndex = getNextReadIndex().orElseThrow(() -> new EOFException("End of file while reading memory"));
			memory = data[nextReadIndex];
			limit = limits[nextReadIndex];
		}

		@Override
		public void close() {
			ArrayData.this.close();
		}
	}
}
