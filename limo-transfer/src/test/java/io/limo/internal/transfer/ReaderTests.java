/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.transfer;

import io.limo.transfer.Reader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.ByteOrder;

import static io.limo.internal.BinaryTestData.BYTES_BIG_ENDIAN;
import static io.limo.internal.BinaryTestData.BYTES_LITTLE_ENDIAN;
import static io.limo.internal.BinaryTestData.FIRST_BYTE;
import static io.limo.internal.BinaryTestData.FIRST_INT;
import static io.limo.internal.BinaryTestData.SECOND_BYTE;
import static io.limo.internal.BinaryTestData.SECOND_INT;
import static org.assertj.core.api.Assertions.assertThat;

interface ReaderTests {

    @Test
    @DisplayName("Verify read using native Big Endian is working")
    default void readBE() {
        // Reader is natively Big Endian ordered
        final var reader = instanciateReader(BYTES_BIG_ENDIAN);
        assertThat(reader.readByte()).isEqualTo(FIRST_BYTE);
        assertThat(reader.readInt()).isEqualTo(FIRST_INT);
        assertThat(reader.readByte()).isEqualTo(SECOND_BYTE);
        assertThat(reader.readInt()).isEqualTo(SECOND_INT);
    }

    @Test
    @DisplayName("Verify read using Little Endian is working")
    default void readLE() {
        final var reader = instanciateReader(BYTES_LITTLE_ENDIAN);
        reader.setByteOrder(ByteOrder.LITTLE_ENDIAN);
        assertThat(reader.readByte()).isEqualTo(FIRST_BYTE);
        assertThat(reader.readInt()).isEqualTo(FIRST_INT);
        assertThat(reader.readByte()).isEqualTo(SECOND_BYTE);
        assertThat(reader.readInt()).isEqualTo(SECOND_INT);
    }

    Reader instanciateReader(byte[] byteArray);
}
