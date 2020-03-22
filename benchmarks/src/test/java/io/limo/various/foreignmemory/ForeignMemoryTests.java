/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.various.foreignmemory;

import jdk.incubator.foreign.MemoryHandles;
import jdk.incubator.foreign.MemoryLayout;
import jdk.incubator.foreign.MemorySegment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.invoke.VarHandle;
import java.nio.ByteOrder;

import static org.assertj.core.api.Assertions.assertThat;

public final class ForeignMemoryTests {

    private static final VarHandle BYTE_HANDLE = MemoryHandles.varHandle(byte.class, ByteOrder.BIG_ENDIAN);

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

    @Test
    @DisplayName("Verify that MemorySegment to ByteBuffer works fine (write on MemorySegment)")
    void memorySegmentToByteBuffer1() {
        try (final var segment = MemorySegment.allocateNative(3)) {
            final var base = segment.baseAddress();
            BYTE_HANDLE.set(base, (byte) 0xa);
            BYTE_HANDLE.set(base.addOffset(1), (byte) 0xb);
            BYTE_HANDLE.set(base.addOffset(2), (byte) 0xc);

            final var bb = segment.asByteBuffer();

            assertThat(bb.get()).isEqualTo((byte) 0xa);
            assertThat(bb.get()).isEqualTo((byte) 0xb);
            assertThat(bb.get()).isEqualTo((byte) 0xc);
        }
    }

    @Test
    @DisplayName("Verify that MemorySegment to ByteBuffer works fine (write on ByteBuffer)")
    void memorySegmentToByteBuffer2() {
        try (final var segment = MemorySegment.allocateNative(3)) {
            var bb = segment.asByteBuffer();
            bb.put((byte) 0xa);
            bb.put((byte) 0xb);
            bb.put((byte) 0xc);

            bb = segment.asByteBuffer();

            assertThat(bb.get()).isEqualTo((byte) 0xa);
            assertThat(bb.get()).isEqualTo((byte) 0xb);
            assertThat(bb.get()).isEqualTo((byte) 0xc);
        }
    }
}
