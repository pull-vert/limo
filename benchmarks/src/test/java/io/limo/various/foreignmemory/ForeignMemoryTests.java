/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.various.foreignmemory;

import jdk.incubator.foreign.MemoryLayout;
import jdk.incubator.foreign.MemorySegment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.ByteOrder;

import static org.assertj.core.api.Assertions.assertThat;

public final class ForeignMemoryTests {

    @Test
    @DisplayName("Verify read using Big Endian is working with Layout")
    void readLayout() {
        final var byteAndIntStruct = MemoryLayout.ofStruct(
            MemoryLayout.ofValueBits(Byte.SIZE, ByteOrder.BIG_ENDIAN).withName("byte"),
            MemoryLayout.ofValueBits(Integer.SIZE, ByteOrder.BIG_ENDIAN).withName("int").withBitAlignment(8));

        final var byteHandle = byteAndIntStruct.varHandle(byte.class,
            MemoryLayout.PathElement.groupElement("byte"));
        final var intHandle = byteAndIntStruct.varHandle(int.class,
            MemoryLayout.PathElement.groupElement("int"));

        // Allocate 5 bytes : 1 for byte, 4 for int
        try (final var segment = MemorySegment.allocateNative(byteAndIntStruct.byteSize())) {
            final var base = segment.baseAddress();
            byteHandle.set(base, (byte) 0xa);
            intHandle.set(base, 42);

            assertThat(byteHandle.get(base)).isEqualTo((byte) 0xa);
            assertThat(intHandle.get(base)).isEqualTo(42);
        }
    }
}
