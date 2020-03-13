/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.bytes;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.assertj.core.api.Assertions.assertThat;

public final class ByteBufferByteSequenceTests {

    private static final byte FIRST = 0xa;
    private static final int SECOND = 42;

    @Test
    @DisplayName("Verify read using native Big Endian is working")
    void readBE() {
        // Allocate 5 bytes : 1 for byte, 4 for int
        // ByteBuffer is natively Big Endian ordered
        final var bb = ByteBuffer.allocateDirect(5);
        bb.put(FIRST);
        bb.putInt(SECOND);

        final var byteSeq = new ByteBufferByteSequence(bb);
        assertThat(byteSeq.readByteAt(0)).isEqualTo(FIRST);
        assertThat(byteSeq.readIntAt(1)).isEqualTo(SECOND);
    }

    @Test
    @DisplayName("Verify read using Little Endian is working")
    void readLE() {
        // Allocate 5 bytes : 1 for byte, 4 for int
        // configure ByteBuffer to use Little Endian
        final var bb = ByteBuffer.allocateDirect(5);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.put(FIRST);
        bb.putInt(SECOND);

        final var byteSeq = new ByteBufferByteSequence(bb);
        byteSeq.setByteOrder(ByteOrder.LITTLE_ENDIAN);
        assertThat(byteSeq.readByteAt(0)).isEqualTo(FIRST);
        assertThat(byteSeq.readIntAt(1)).isEqualTo(SECOND);
    }
}
