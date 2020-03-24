/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.bytes;

import io.limo.internal.BinaryTestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public final class MemorySegmentBytesTests implements BytesTests {

    @Test
    @DisplayName("Verify read using Big Endian is working")
    void readBE() {
        readBETest(new MemorySegmentBytes(BinaryTestData.BYTES_BIG_ENDIAN));
    }

    @Test
    @DisplayName("Verify read using Little Endian is working")
    void readLE() {
        readLETest(new MemorySegmentBytes(BinaryTestData.BYTES_LITTLE_ENDIAN));
    }

    @Test
    @DisplayName("Verify all operations on closed Bytes throw IllegalStateException")
    void closed() {
        final var bytes = new MemorySegmentBytes(BinaryTestData.BYTES_LITTLE_ENDIAN);
        bytes.close();
        assertThatThrownBy(() -> bytes.readByteAt(0))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Segment is not alive");
        assertThatThrownBy(() -> bytes.readIntAt(0))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Segment is not alive");
    }
}
