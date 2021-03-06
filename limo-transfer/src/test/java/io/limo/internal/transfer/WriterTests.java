/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.transfer;

import io.limo.transfer.Writer;
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

interface WriterTests {

    @Test
    @DisplayName("Verify write using native Big Endian is working")
    default void writeBE() {
        // Writer is natively Big Endian ordered
        final var writer = instanciateWriter();
        writer.writeByte(FIRST_BYTE);
        writer.writeInt(FIRST_INT);
        writer.writeByte(SECOND_BYTE);
        writer.writeInt(SECOND_INT);
        assertThat(writtenByteArray()).isEqualTo(BYTES_BIG_ENDIAN);
    }

    @Test
    @DisplayName("Verify write using Little Endian is working")
    default void writeLE() {
        final var writer = instanciateWriter();
        writer.setByteOrder(ByteOrder.LITTLE_ENDIAN);
        writer.writeByte(FIRST_BYTE);
        writer.writeInt(FIRST_INT);
        writer.writeByte(SECOND_BYTE);
        writer.writeInt(SECOND_INT);
        assertThat(writtenByteArray()).isEqualTo(BYTES_LITTLE_ENDIAN);
    }

    Writer instanciateWriter();

    byte[] writtenByteArray();
}
