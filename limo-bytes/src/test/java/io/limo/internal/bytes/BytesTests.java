/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.bytes;

import io.limo.utils.BytesOps;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.ByteOrder;

import static org.assertj.core.api.Assertions.assertThat;

abstract class BytesTests {

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
        // Bytes is natively Big Endian ordered
        try (final var bytes = instanciateBytes(BYTES_BIG_ENDIAN)) {
            assertThat(bytes.readByteAt(0)).isEqualTo(FIRST_BYTE);
            assertThat(bytes.readIntAt(1)).isEqualTo(FIRST_INT);
            assertThat(bytes.readByteAt(5)).isEqualTo(SECOND_BYTE);
            assertThat(bytes.readIntAt(6)).isEqualTo(SECOND_INT);
        }
    }

    @Test
    @DisplayName("Verify read using Little Endian is working")
    void readLE() {
        try (final var bytes = instanciateBytes(BYTES_LITTLE_ENDIAN)) {
            bytes.setByteOrder(ByteOrder.LITTLE_ENDIAN);
            assertThat(bytes.readByteAt(0)).isEqualTo(FIRST_BYTE);
            assertThat(bytes.readIntAt(1)).isEqualTo(FIRST_INT);
            assertThat(bytes.readByteAt(5)).isEqualTo(SECOND_BYTE);
            assertThat(bytes.readIntAt(6)).isEqualTo(SECOND_INT);
        }
    }

    protected void testWriteBE(MutableBytes bytes) {
        // Bytes is natively Big Endian ordered
        try (bytes) {
            bytes.writeByteAt(0, FIRST_BYTE);
            bytes.writeIntAt(1, FIRST_INT);
            bytes.writeByteAt(5, SECOND_BYTE);
            bytes.writeIntAt(6, SECOND_INT);
            assertThat(bytes.toByteArray()).isEqualTo(BYTES_BIG_ENDIAN);
        }
    }

    protected void testWriteLE(MutableBytes bytes) {
        try (bytes) {
            bytes.setByteOrder(ByteOrder.LITTLE_ENDIAN);
            bytes.writeByteAt(0, FIRST_BYTE);
            bytes.writeIntAt(1, FIRST_INT);
            bytes.writeByteAt(5, SECOND_BYTE);
            bytes.writeIntAt(6, SECOND_INT);
            assertThat(bytes.toByteArray()).isEqualTo(BYTES_LITTLE_ENDIAN);
        }
    }

    protected abstract Bytes instanciateBytes(byte[] byteArray);
}
