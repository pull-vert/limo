/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.data;

import io.limo.bytes.Data;
import io.limo.internal.bytes.Bytes;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;

/**
 * Implementation of the immutable {@link Data} interface based on a fixed size array of {@link Bytes}
 *
 * @see MutableBytesArrayData
 */
public class BytesArrayData extends AbstractBytesArrayData<Bytes> {

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
            final var arrayData = (BytesArrayData) first;
            offset = arrayData.writeIndex + 1;
            totalCapacity += offset;
            if (totalCapacity < DEFAULT_CAPACITY) {
                totalCapacity = DEFAULT_CAPACITY;
            }
            this.bytesArray = Arrays.copyOf(arrayData.bytesArray, totalCapacity);
            this.limits = Arrays.copyOf(arrayData.limits, totalCapacity);
        } else if (first instanceof BytesData) {
            final var bytesData = (BytesData) first;
            offset = 1;
            totalCapacity++;
            if (totalCapacity < DEFAULT_CAPACITY) {
                totalCapacity = DEFAULT_CAPACITY;
            }
            this.bytesArray = new Bytes[totalCapacity];
            this.bytesArray[0] = bytesData.bytes;
            this.limits = new int[totalCapacity];
            this.limits[0] = bytesData.limit;
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
                final var bytesData = (BytesData) data;
                this.bytesArray[offset] = bytesData.bytes;
                this.limits[offset] = bytesData.limit;
                offset++;
            }
        }
        this.byteSize = byteSizesSum;

        this.reader = new ReaderImpl();
    }
}
