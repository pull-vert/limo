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
import java.util.Optional;
import java.util.OptionalInt;

/**
 * Implementation of the immutable {@code Data} interface based on a fixed size array of memory chunks
 *
 * @see Data
 * @see MutableArrayData
 */
public class ArrayData extends AbstractData {

	/**
	 * Default initial capacity.
	 */
	private static final int DEFAULT_CAPACITY = 4;

	/**
	 * The array of memories into which the elements of the ArrayData are stored
	 */
	Memory @NotNull[] memories;

	/**
	 * The array of limits for each memory
	 */
	long @NotNull[] limits;

	/**
	 * Index of the memory in data array that is currently read
	 */
	int readIndex = 0;

	/**
	 * Last index of the memory in data array that has been written
	 *
	 * @implNote It has a 0 initial value, even if first memory was not written
	 */
	int writeIndex = 0;

	public ArrayData() {
		// init memories and limits with DEFAULT_CAPACITY size
		memories = new Memory[DEFAULT_CAPACITY];
		limits = new long[DEFAULT_CAPACITY];
		reader = new ReaderImpl();
	}

	public ArrayData(Memory @NotNull[] memories, long @NotNull[] limits) {
		this.memories = Objects.requireNonNull(memories);
		this.limits = Objects.requireNonNull(limits);
		writeIndex = limits.length;

		reader = new ReaderImpl();
	}

	public ArrayData(@NotNull Data first, Data @NotNull... rest) {
		Objects.requireNonNull(first);
		Objects.requireNonNull(rest);

		// must get total length
		var totalLength = 0;
		// todo use instanceof pattern matching of java 14 https://openjdk.java.net/jeps/305
		for (final var data : rest) {
			if (data instanceof ArrayData) {
				totalLength += ((ArrayData) data).writeIndex + 1;
			}
		}

		// initiate arrays
		var offset = 0;
		if (first instanceof ArrayData) {
			final var firstArrayData = (ArrayData) first;
			offset = firstArrayData.writeIndex + 1;
			totalLength += offset;
			memories = Arrays.copyOf(firstArrayData.memories, totalLength);
			limits = Arrays.copyOf(firstArrayData.limits, totalLength);
		} else {
			throw new IllegalArgumentException("data type " + first.getClass().getTypeName() + " is unsupported");
		}
		writeIndex = totalLength;

		int dataLength;
		for (final var data : rest) {
			if (data instanceof ArrayData) {
				final var arrayData = (ArrayData) data;
				dataLength = arrayData.writeIndex + 1;
				System.arraycopy(arrayData.memories, 0, memories, offset, dataLength);
				System.arraycopy(arrayData.limits, 0, limits, offset, dataLength);
				offset += dataLength;
			}
		}

		reader = new ReaderImpl();
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

	@Override
	public void setByteOrder(@NotNull ByteOrder byteOrder) {
		isBigEndian = (byteOrder == ByteOrder.BIG_ENDIAN);
		// affect this byte order to all memories
		for (final var memory : memories) {
			Optional.ofNullable(memory).ifPresent(mem -> mem.setByteOrder(byteOrder));
		}
	}

	@Override
	public void close() {
		for (final var memory : memories) {
			Optional.ofNullable(memory).ifPresent(Memory::close);
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
			memory = Objects.requireNonNull(memories[0]);
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
			memory = Objects.requireNonNull(memories[nextReadIndex]);
			limit = limits[nextReadIndex];
		}

		@Override
		public void close() {
			ArrayData.this.close();
		}
	}
}
