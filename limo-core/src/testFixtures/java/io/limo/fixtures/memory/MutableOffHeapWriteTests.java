/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.fixtures.memory;

import io.limo.memory.MutableOffHeap;

import static io.limo.fixtures.BinaryTestData.*;
import static org.assertj.core.api.Assertions.assertThat;

interface MutableOffHeapWriteTests {

    default void writeBETest(MutableOffHeap mutableMemory) {
        try (mutableMemory) {
            mutableMemory.writeByteAt(0, FIRST_BYTE);
            mutableMemory.writeIntAt(1, FIRST_INT);
            mutableMemory.writeByteAt(5, SECOND_BYTE);
            mutableMemory.writeIntAt(6, SECOND_INT);
            assertThat(mutableMemory.toByteArray()).isEqualTo(BYTES_BIG_ENDIAN);
        }
    }

    default void writeLETest(MutableOffHeap mutableMemory) {
        try (mutableMemory) {
            mutableMemory.writeByteAt(0, FIRST_BYTE);
            mutableMemory.writeIntAtLE(1, FIRST_INT);
            mutableMemory.writeByteAt(5, SECOND_BYTE);
            mutableMemory.writeIntAtLE(6, SECOND_INT);
            assertThat(mutableMemory.toByteArray()).isEqualTo(BYTES_LITTLE_ENDIAN);
        }
    }
}
