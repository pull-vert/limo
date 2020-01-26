package io.limo.internal.bytes;

import io.limo.bytes.Data;
import io.limo.bytes.Reader;
import io.limo.internal.bytes.memory.Memory;
import org.jetbrains.annotations.NotNull;

import java.io.EOFException;
import java.nio.ByteOrder;
import java.util.Objects;

/**
 * Implementation of the immutable {@code Data} interface based on a single memory chunk
 *
 * @see Data
 */
public class SingleData extends AbstractData {

	/**
	 * The memory into which the elements of the SingleData are stored
	 */
	@NotNull
	final Memory memory;

	/**
	 * The limit of memory
	 */
	final long limit;

	public SingleData(@NotNull Memory memory, long limit) {
		this.memory = Objects.requireNonNull(memory);
		this.limit = limit;
		reader = new ReaderImpl();
	}

	@Override
	@NotNull
	public Reader getReader() {
		return reader;
	}

	@Override
	public void setByteOrder(@NotNull ByteOrder byteOrder) {
		isBigEndian = (byteOrder == ByteOrder.BIG_ENDIAN);
		// affect this byte order to memory
		memory.setByteOrder(byteOrder);
	}

	@Override
	public void close() {
		memory.close();
	}

	/**
	 * Implementation of the {@code Reader} interface that reads in data array of {@code ArrayData}
	 */
	private final class ReaderImpl implements Reader {

		/**
		 * Reading index in the memory
		 */
		private long index = 0L;


		@Override
		public byte readByte() throws EOFException {
			final var currentIndex = index;
			final var byteSize = 1;
			final var targetLimit = currentIndex + byteSize;

			// 1) at least 1 byte left to read a byte in memory
			if (limit >= targetLimit) {
				index = targetLimit;
				return memory.readByteAt(currentIndex);
			}

			// 2) memory is exhausted
			throw new EOFException("End of file while reading memory");
		}

		@Override
		public int readInt() throws EOFException {
			final var currentIndex = index;
			final var intSize = 4;
			final var targetLimit = currentIndex + intSize;

			// 1) at least 4 bytes left to read an int in memory
			if (limit >= targetLimit) {
				index = targetLimit;
				return memory.readIntAt(currentIndex);
			}

			// 2) memory is exhausted
			throw new EOFException("End of file while reading memory");
		}

		@Override
		public void close() {
			SingleData.this.close();
		}
	}
}
