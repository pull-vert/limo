/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.various;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.assertj.core.api.Assertions.assertThat;

public final class ByteBufferReadTests {

    private ByteBuffer bb;

    private static final VarHandle intHandle = MethodHandles.byteBufferViewVarHandle(int[].class, ByteOrder.BIG_ENDIAN);
    private static final VarHandle longHandle = MethodHandles.byteBufferViewVarHandle(long[].class, ByteOrder.BIG_ENDIAN);

    @BeforeAll
    void before() {
        // Allocate 13 bytes : 4 for int, 8 for long, 1 for byte
        // ByteBuffer is natively Big Endian ordered
        bb = ByteBuffer.allocateDirect(13);
        bb.putInt(42);
        bb.putLong(128L);
        bb.put((byte) 0xa);
    }

    @Test
    @DisplayName("Direct read")
    void directRead() {
        bb.rewind();
        assertThat(bb.getInt()).isEqualTo(42);
        assertThat(bb.getLong()).isEqualTo(128L);
        assertThat(bb.get()).isEqualTo((byte) 0xa);
    }

    @Test
    @DisplayName("Indexed read")
    void indexedRead() {
        assertThat(bb.getInt(0)).isEqualTo(42);
        assertThat(bb.getLong(4)).isEqualTo(128L);
        assertThat(bb.get(12)).isEqualTo((byte) 0xa);
    }

    @Test
    @DisplayName("VarHandle read")
    void varHandleRead() {
        assertThat(intHandle.get(bb, 0)).isEqualTo(42);
        assertThat(longHandle.get(bb, 4)).isEqualTo(128L);
    }
}
