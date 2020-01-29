package io.limo.internal.bytes;

import io.limo.bytes.Data;
import io.limo.bytes.Reader;
import org.jetbrains.annotations.NotNull;

import java.io.EOFException;
import java.nio.ByteOrder;
import java.util.Objects;

/**
 * Implementation of the immutable {@code Data} interface based on a single byte sequence
 *
 * @see Data
 */
public class SingleData implements Data {

    /**
     * The memory into which the elements of the SingleData are stored
     */
    @NotNull
    final ByteSequence byteSequence;

    /**
     * The limit of memory
     */
    final long limit;

    boolean isBigEndian = true;

    /**
     * The data reader
     */
    @NotNull
    Reader reader;

    public SingleData(@NotNull ByteSequence byteSequence, long limit) {
        this.byteSequence = Objects.requireNonNull(byteSequence);
        this.limit = limit;
        reader = new ReaderImpl();
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
    public void setByteOrder(@NotNull ByteOrder byteOrder) {
        isBigEndian = (byteOrder == ByteOrder.BIG_ENDIAN);
        // affect this byte order to memory
        byteSequence.setByteOrder(byteOrder);
    }

    @Override
    public void close() {
        byteSequence.close();
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
                return byteSequence.readByteAt(currentIndex);
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
                return byteSequence.readIntAt(currentIndex);
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