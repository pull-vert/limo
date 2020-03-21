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

interface MutableBytesTests {

    @Test
    @DisplayName("Verify write using native Big Endian is working")
    default void writeBE() {
        // Bytes is natively Big Endian ordered
        try (final var mutableBytes = instanciateMutableBytes()) {
            mutableBytes.writeByteAt(0, FIRST_BYTE);
            mutableBytes.writeIntAt(1, FIRST_INT, true);
            mutableBytes.writeByteAt(5, SECOND_BYTE);
            mutableBytes.writeIntAt(6, SECOND_INT, true);
            assertThat(mutableBytes.toByteArray()).isEqualTo(BYTES_BIG_ENDIAN);
        }
    }

    @Test
    @DisplayName("Verify write using Little Endian is working")
    default void writeLE() {
        try (final var mutableBytes = instanciateMutableBytes()) {
            mutableBytes.writeByteAt(0, FIRST_BYTE);
            mutableBytes.writeIntAt(1, FIRST_INT, false);
            mutableBytes.writeByteAt(5, SECOND_BYTE);
            mutableBytes.writeIntAt(6, SECOND_INT, false);
            assertThat(mutableBytes.toByteArray()).isEqualTo(BYTES_LITTLE_ENDIAN);
        }
    }

    MutableBytes instanciateMutableBytes();
}
