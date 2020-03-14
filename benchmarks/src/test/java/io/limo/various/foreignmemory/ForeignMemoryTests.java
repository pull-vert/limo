/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.various.foreignmemory;

import jdk.incubator.foreign.MemoryHandles;
import jdk.incubator.foreign.MemorySegment;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.ByteOrder;

import static org.assertj.core.api.Assertions.assertThat;

public final class ForeignMemoryTests {

    @Test
    @Disabled
    @DisplayName("Verify read using native Big Endian is working")
    void readBE() {
        final var byteHandle = MemoryHandles.varHandle(byte.class, ByteOrder.BIG_ENDIAN);
        final var intHandle = MemoryHandles.varHandle(int.class, ByteOrder.BIG_ENDIAN);
        // Allocate 5 bytes : 1 for byte, 4 for int
        try (final var segment = MemorySegment.allocateNative(5)) {
            final var base = segment.baseAddress();
            byteHandle.set(base, (byte) 0xa);
            intHandle.set(base.addOffset(1L), 42);

            assertThat(byteHandle.get(base)).isEqualTo((byte) 0xa);
            assertThat(intHandle.get(base.addOffset(1L))).isEqualTo(42);
        }
    }
}
