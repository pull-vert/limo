/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.bytes;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static org.assertj.core.api.Assertions.assertThat;

public final class ByteBufferSeqTests {

    private ByteBufferSeq byteSeq;

    @BeforeAll
    void before() {
        // Allocate 13 bytes : 4 for int, 8 for long, 1 for byte
        // ByteBuffer is natively Big Endian ordered
        final var bb = ByteBuffer.allocateDirect(13);
        bb.putInt(42);
        bb.putLong(128L);
        bb.put((byte) 0xa);
        byteSeq = new ByteBufferSeq(bb);
    }

    @Test
    @DisplayName("Verify read is working")
    void read() {
        assertThat(byteSeq.readIntAt(0)).isEqualTo(42);
//        assertThat(byteSeq.readLongAt(4)).isEqualTo(128L);
        assertThat(byteSeq.readByteAt(12)).isEqualTo((byte) 0xa);
    }
}
