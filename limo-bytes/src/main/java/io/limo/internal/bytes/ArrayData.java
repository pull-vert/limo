/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.bytes;

import io.limo.bytes.Data;
import io.limo.bytes.Reader;
import io.limo.internal.bytes.memory.Memory;
import org.jetbrains.annotations.NotNull;

import java.io.EOFException;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Objects;
import java.util.OptionalInt;

/**
 * Fixed-array implementation of the {@code Data} interface.
 *
 * @see Data
 * @see MutableArrayData
 */
public class ArrayData implements Data {

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

	boolean isBigEndian = true;

	/**
	 * The data reader
	 */
	@NotNull
	private final Reader reader;

	public ArrayData(@NotNull Data first, @NotNull Data... rest) {
//		Objects.requireNonNull(datas);
//		// get total length
//		int totalLength = 0;
//		for (final var data : datas) {
//			// todo use instanceof pattern matching of java 14 https://openjdk.java.net/jeps/305
//			if (data instanceof ArrayData) {
//				final var arrayData = (ArrayData) data;
//				Objects.requireNonNull(arrayData.data);
//				if (arrayData.data.length == 0) {
//					throw new IllegalArgumentException("data array must not be empty");
//				}
//				totalLength += arrayData.writeIndex + 1;
//			} else {
//				throw new IllegalArgumentException("data type " + data.getClass().getTypeName() + " is unsupported");
//			}
//		}
//		for (final var data : datas) {
//			final var arrayData = (ArrayData) data;
//			this.data = Arrays.copyOf(arrayData.data;
//			limits = Objects.requireNonNull(arrayData.limits);
//		}

		reader = new ReaderImpl();
	}

	@SafeVarargs
	private static <T> T[] concatAll(T[] first, T[]... rest) {
		int totalLength = first.length;
		for (T[] array : rest) {
			totalLength += array.length;
		}
		T[] result = Arrays.copyOf(first, totalLength);
		int offset = first.length;
		for (T[] array : rest) {
			System.arraycopy(array, 0, result, offset, array.length);
			offset += array.length;
		}
		return result;
	}

	/**
	 * @return next not empty chunk of memory, or empty if none exists
	 */
	@NotNull
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

	@NotNull
	@Override
	public ByteOrder getByteOrder() {
		return isBigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
	}

	@Override
	public void close() {
		for (final var memory : data) {
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
		 * Current memory is the first in the data array of {@code ArrayData}
		 */
		private ReaderImpl() {
			memory = Objects.requireNonNull(data[0]);
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
			memory = Objects.requireNonNull(data[nextReadIndex]);
			limit = limits[nextReadIndex];
		}

		@Override
		public void close() {
			ArrayData.this.close();
		}
	}
}
