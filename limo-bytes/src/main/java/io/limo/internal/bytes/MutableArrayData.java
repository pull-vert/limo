/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.bytes;

import io.limo.bytes.Data;
import io.limo.bytes.MutableData;
import io.limo.bytes.Writer;
import io.limo.internal.bytes.memory.Memory;
import io.limo.internal.bytes.memory.MemorySupplier;
import org.jetbrains.annotations.NotNull;

import java.io.EOFException;
import java.util.Arrays;
import java.util.Objects;

/**
 * Resizable-array implementation of the {@code MutableData} interface.
 *
 * @implNote Inspired by ArrayList
 * @see ArrayData
 * @see MutableData
 */
public final class MutableArrayData extends ArrayData implements MutableData {

	/**
	 * The memory supplier, can act as a pool
	 */
	@NotNull
	private MemorySupplier memorySupplier;

	/**
	 * The data writer
	 */
	@NotNull
	private final Writer writer;

	public MutableArrayData(@NotNull MemorySupplier memorySupplier) {
		this.memorySupplier = Objects.requireNonNull(memorySupplier);
		final var initialMemory = memorySupplier.get();
		// First element in data = initialMemory
		memories[0] = initialMemory;
		writer = new WriterImpl();
	}

	public MutableArrayData(@NotNull Data data, @NotNull MemorySupplier memorySupplier) {
		// todo use instanceof pattern matching of java 14 https://openjdk.java.net/jeps/305
		if (Objects.requireNonNull(data) instanceof ArrayData) {
			final var arrayData = (ArrayData) data;
			memories = arrayData.memories;
			limits = arrayData.limits;
			readIndex = arrayData.readIndex;
			writeIndex = arrayData.writeIndex;
			reader = arrayData.reader;
		} else {
			throw new IllegalArgumentException("data type " + data.getClass().getTypeName() + " is unsupported");
		}
		this.memorySupplier = Objects.requireNonNull(memorySupplier);
		writer = new WriterImpl();
	}

	/**
	 * Get a new Memory from {@link #memorySupplier}
	 *
	 * @return new Memory
	 */
	@NotNull
	private Memory supplyNewMemory() {
		// no room left in array
		writeIndex += 1;
		if (writeIndex == memories.length) {
			// increase array size by 2 times
			final var newLength = memories.length * 2;
			memories = Arrays.copyOf(memories, newLength);
			limits = Arrays.copyOf(limits, newLength);
		}
		final var newMemory = memorySupplier.get();
		memories[writeIndex] = newMemory;
		return newMemory;
	}

	@NotNull
	@Override
	public Writer getWriter() {
		return writer;
	}

	/**
	 * Implementation of the {@code Writer} interface that writes in data array of {@code ArrayData}
	 */
	private final class WriterImpl implements Writer {

		/**
		 * Current Memory chunk to write in
		 */
		@NotNull
		private Memory memory;

		/**
		 * Writing index in the current {@link #memory}
		 */
		private long limit = 0L;

		/**
		 * Capacity of the current {@link #memory}
		 */
		private long capacity;

		/**
		 * Current memory is the last in the data array of {@code ArrayData}
		 */
		private WriterImpl() {
			memory = Objects.requireNonNull(memories[memories.length - 1]);
			capacity = memory.getCapacity();
		}

		@Override
		public void writeByte(byte value) throws EOFException {
			final var currentLimit = limit;
			final var byteSize = 1;
			final var targetLimit = currentLimit + byteSize;

			// 1) at least 1 byte left to write a byte in current memory
			if (capacity >= targetLimit) {
				limit = targetLimit;
				memory.writeByteAt(currentLimit, value);
				return;
			}

			// 2) current memory is exactly full
			// let's add a new chunk of data from supplier
			addNewMemory();

			// we are at 0 index in newly obtained memory

			if (capacity < byteSize) {
				throw new EOFException("Empty memory, no room for writing a byte");
			}
			limit = byteSize;
			memory.writeByteAt(currentLimit, value);
		}

		@Override
		public void writeInt(int value) throws EOFException {
			final var currentLimit = limit;
			final var intSize = 4;
			final var targetLimit = currentLimit + intSize;

			// 1) at least 4 bytes left to write an int in current memory
			if (capacity >= targetLimit) {
				limit = targetLimit;
				memory.writeIntAt(currentLimit, value);
				return;
			}

			// 2) current memory is exactly full
			if (currentLimit == capacity) {
				// let's add a new chunk of data from supplier
				addNewMemory();

				// we are at 0 index in newly obtained memory

				if (capacity >= intSize) {
					limit = intSize;
					memory.writeIntAt(currentLimit, value);
					return;
				}
				throw new EOFException("Memory too small, no room for writing an int");
			}

			// 3) must write some bytes in current chunk, some others in next one
			for (final var b : BytesOps.intToBytes(value, isBigEndian)) {
				writeByte(b);
			}
		}

		@Override
		public void close() {
			MutableArrayData.this.close();
		}

		/**
		 * Current memory is full, add a new Memory in data array
		 */
		private void addNewMemory() {
			memory = supplyNewMemory();
			capacity = memory.getCapacity();
			limit = 0L;
		}
	}
}
