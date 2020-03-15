/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.bytes;

import io.limo.utils.BytesOps;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.ByteOrder;

import static org.assertj.core.api.Assertions.assertThat;

public final class MemorySegmentDataTests {

    private static final byte FIRST_BYTE = 0xa;

    private static final int FIRST_INT = 42;

    private static final byte SECOND_BYTE = 0xe;

    private static final int SECOND_INT = 4568;

    private static final byte[] BYTES_BIG_ENDIAN;
    private static final byte[] BYTES_LITTLE_ENDIAN;

    static {
        BYTES_BIG_ENDIAN = new byte[10];
        BYTES_LITTLE_ENDIAN = new byte[10];

        BYTES_BIG_ENDIAN[0] = FIRST_BYTE;
        BYTES_LITTLE_ENDIAN[0] = FIRST_BYTE;

        final var firstIntBytesBigEndian = BytesOps.intToBytes(FIRST_INT, true);
        BYTES_BIG_ENDIAN[1] = firstIntBytesBigEndian[0];
        BYTES_BIG_ENDIAN[2] = firstIntBytesBigEndian[1];
        BYTES_BIG_ENDIAN[3] = firstIntBytesBigEndian[2];
        BYTES_BIG_ENDIAN[4] = firstIntBytesBigEndian[3];

        final var firstIntBytesLittleEndian = BytesOps.intToBytes(FIRST_INT, false);
        BYTES_LITTLE_ENDIAN[1] = firstIntBytesLittleEndian[0];
        BYTES_LITTLE_ENDIAN[2] = firstIntBytesLittleEndian[1];
        BYTES_LITTLE_ENDIAN[3] = firstIntBytesLittleEndian[2];
        BYTES_LITTLE_ENDIAN[4] = firstIntBytesLittleEndian[3];

        BYTES_BIG_ENDIAN[5] = SECOND_BYTE;
        BYTES_LITTLE_ENDIAN[5] = SECOND_BYTE;

        final var secondIntBytesBigEndian = BytesOps.intToBytes(SECOND_INT, true);
        BYTES_BIG_ENDIAN[6] = secondIntBytesBigEndian[0];
        BYTES_BIG_ENDIAN[7] = secondIntBytesBigEndian[1];
        BYTES_BIG_ENDIAN[8] = secondIntBytesBigEndian[2];
        BYTES_BIG_ENDIAN[9] = secondIntBytesBigEndian[3];

        final var secondIntBytesLittleEndian = BytesOps.intToBytes(SECOND_INT, false);
        BYTES_LITTLE_ENDIAN[6] = secondIntBytesLittleEndian[0];
        BYTES_LITTLE_ENDIAN[7] = secondIntBytesLittleEndian[1];
        BYTES_LITTLE_ENDIAN[8] = secondIntBytesLittleEndian[2];
        BYTES_LITTLE_ENDIAN[9] = secondIntBytesLittleEndian[3];
    }

    @Test
    @DisplayName("Verify read using native Big Endian is working")
    void readBE() {
        // ByteSequence is natively Big Endian ordered
        try (final var byteSeq = new MemorySegmentData(BYTES_BIG_ENDIAN)) {
            final var reader = byteSeq.getReader();
            assertThat(reader.readByte()).isEqualTo(FIRST_BYTE);
            assertThat(reader.readInt()).isEqualTo(FIRST_INT);
            assertThat(reader.readByte()).isEqualTo(SECOND_BYTE);
            assertThat(reader.readInt()).isEqualTo(SECOND_INT);
        }
    }

    @Test
    @DisplayName("Verify read using Little Endian is working")
    void readLE() {
        // Allocate 5 bytes : 1 for byte, 4 for int
        // ByteSequence is natively Big Endian ordered
        try (final var byteSeq = new MemorySegmentData(BYTES_LITTLE_ENDIAN)) {
            byteSeq.setByteOrder(ByteOrder.LITTLE_ENDIAN);
            final var reader = byteSeq.getReader();
            assertThat(reader.readByte()).isEqualTo(FIRST_BYTE);
            assertThat(reader.readInt()).isEqualTo(FIRST_INT);
            assertThat(reader.readByte()).isEqualTo(SECOND_BYTE);
            assertThat(reader.readInt()).isEqualTo(SECOND_INT);
        }
    }
}
