/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.bytes;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static org.assertj.core.api.Assertions.assertThat;

public final class ByteBufferSeqTests {

    @Test
    @DisplayName("Verify read is working")
    void read() {
        // Allocate 5 bytes : 1 for byte, 4 for int
        // ByteBuffer is natively Big Endian ordered
        final var bb = ByteBuffer.allocateDirect(5);
        bb.put((byte) 0xa);
        bb.putInt(42);
        final var byteSeq = new ByteBufferSeq(bb);
        assertThat(byteSeq.readByteAt(0)).isEqualTo((byte) 0xa);
        assertThat(byteSeq.readIntAt(1)).isEqualTo(42);
    }
}
