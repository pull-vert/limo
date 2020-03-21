/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.bytes;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.limo.internal.BinaryTestData.BYTES_BIG_ENDIAN;
import static io.limo.internal.BinaryTestData.BYTES_LITTLE_ENDIAN;
import static io.limo.internal.BinaryTestData.FIRST_BYTE;
import static io.limo.internal.BinaryTestData.FIRST_INT;
import static io.limo.internal.BinaryTestData.SECOND_BYTE;
import static io.limo.internal.BinaryTestData.SECOND_INT;
import static org.assertj.core.api.Assertions.assertThat;

interface BytesTests {

    @Test
    @DisplayName("Verify read using native Big Endian is working")
    default void readBE() {
        // Bytes is natively Big Endian ordered
        try (final var bytes = instanciateBytes(BYTES_BIG_ENDIAN)) {
            assertThat(bytes.readByteAt(0)).isEqualTo(FIRST_BYTE);
            assertThat(bytes.readIntAt(1, true)).isEqualTo(FIRST_INT);
            assertThat(bytes.readByteAt(5)).isEqualTo(SECOND_BYTE);
            assertThat(bytes.readIntAt(6, true)).isEqualTo(SECOND_INT);
        }
    }

    @Test
    @DisplayName("Verify read using Little Endian is working")
    default void readLE() {
        try (final var bytes = instanciateBytes(BYTES_LITTLE_ENDIAN)) {
            assertThat(bytes.readByteAt(0)).isEqualTo(FIRST_BYTE);
            assertThat(bytes.readIntAt(1, false)).isEqualTo(FIRST_INT);
            assertThat(bytes.readByteAt(5)).isEqualTo(SECOND_BYTE);
            assertThat(bytes.readIntAt(6, false)).isEqualTo(SECOND_INT);
        }
    }

    Bytes instanciateBytes(byte[] byteArray);
}
