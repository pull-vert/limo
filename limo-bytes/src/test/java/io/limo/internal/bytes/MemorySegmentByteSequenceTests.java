/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.bytes;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public final class MemorySegmentByteSequenceTests {

    private static final byte FIRST = 0xa;
    private static final int SECOND = 42;

    @Test
    @DisplayName("Verify read using native Big Endian is working")
    void readBE() {
        // Allocate 5 bytes : 1 for byte, 4 for int
        // ByteSequence is natively Big Endian ordered
        try (final var byteSeq = new MemorySegmentByteSequence(5)) {
            byteSeq.writeByteAt(0, FIRST);
            byteSeq.writeIntAt(1L, SECOND);

            assertThat(byteSeq.readByteAt(0)).isEqualTo(FIRST);
            assertThat(byteSeq.readIntAt(1L)).isEqualTo(SECOND);
        }
    }

//    @Test
//    @DisplayName("Verify read using Little Endian is working")
//    void readLE() {
//        // Allocate 5 bytes : 1 for byte, 4 for int
//        // ByteSequence is natively Big Endian ordered
//        try (final var byteSeq = new MemorySegmentByteSequence(5)) {
//            byteSeq.setByteOrder(ByteOrder.LITTLE_ENDIAN);
//            byteSeq.writeByteAt(0, FIRST);
//            byteSeq.writeIntAt(1, SECOND);
//
//            assertThat(byteSeq.readByteAt(0)).isEqualTo(FIRST);
//            assertThat(byteSeq.readIntAt(1)).isEqualTo(SECOND);
//        }
//    }
}
