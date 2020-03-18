/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.bytes;

import io.limo.bytes.Data;
import io.limo.bytes.Reader;
import io.limo.bytes.ReaderUnderflowException;
import io.limo.utils.BytesOps;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Stream;

/**
 * Implementation of the immutable {@link Data} interface based on a fixed size array of {@link Bytes}
 *
 * @see MutableBytesArrayData
 */
public class BytesArrayData extends AbstractBytesArrayData<Bytes> {

    BytesArrayData() {
    }

    public BytesArrayData(@NotNull Data first, Data @NotNull ... rest) {
        Objects.requireNonNull(first);
        Objects.requireNonNull(rest);

        // to get total capacity we start with a loop on rest array
        var totalCapacity = 0;
        // todo use instanceof pattern matching of java 14 https://openjdk.java.net/jeps/305
        for (final var data : rest) {
            if (data instanceof BytesArrayData) {
                totalCapacity += ((BytesArrayData) data).writeIndex + 1;
            } else if (data instanceof BytesData) {
                totalCapacity++;
            } else {
                throw new IllegalArgumentException("data type " + data.getClass().getTypeName() + " is not supported");
            }
        }

        var byteSizesSum = first.getByteSize();
        // initiate arrays
        var offset = 0;
        if (first instanceof BytesArrayData) {
            final var firstArrayData = (BytesArrayData) first;
            offset = firstArrayData.writeIndex + 1;
            totalCapacity += offset;
            if (totalCapacity < DEFAULT_CAPACITY) {
                totalCapacity = DEFAULT_CAPACITY;
            }
            this.bytesArray = Arrays.copyOf(firstArrayData.bytesArray, totalCapacity);
            this.limits = Arrays.copyOf(firstArrayData.limits, totalCapacity);
        } else if (first instanceof BytesData) {
            final var firstData = (BytesData) first;
            offset = 1;
            totalCapacity++;
            if (totalCapacity < DEFAULT_CAPACITY) {
                totalCapacity = DEFAULT_CAPACITY;
            }
            this.bytesArray = new Bytes[totalCapacity];
            this.bytesArray[0] = firstData.bytes;
            this.limits = new int[totalCapacity];
            this.limits[0] = firstData.limit;
        } else {
            throw new IllegalArgumentException("data type " + first.getClass().getTypeName() + " is not supported");
        }
        this.writeIndex = totalCapacity;

        int dataLength;
        for (final var data : rest) {
            byteSizesSum += data.getByteSize();
            if (data instanceof BytesArrayData) {
                final var arrayData = (BytesArrayData) data;
                dataLength = arrayData.writeIndex + 1;
                System.arraycopy(arrayData.bytesArray, 0, this.bytesArray, offset, dataLength);
                System.arraycopy(arrayData.limits, 0, this.limits, offset, dataLength);
                offset += dataLength;
            } else if (first instanceof BytesData) {
                final var byBuData = (BytesData) data;
                this.bytesArray[offset] = byBuData.bytes;
                this.limits[offset] = byBuData.limit;
                offset++;
            }
        }
        this.byteSize = byteSizesSum;

        this.reader = new ReaderImpl();
    }
}
