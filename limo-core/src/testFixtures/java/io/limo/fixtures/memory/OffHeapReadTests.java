/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.fixtures.memory;

import io.limo.memory.OffHeap;

import static io.limo.fixtures.BinaryTestData.*;
import static org.assertj.core.api.Assertions.assertThat;

interface OffHeapReadTests {

    default void readBETest(OffHeap memory) {
        try (memory) {
            assertThat(memory.readByteAt(0)).isEqualTo(FIRST_BYTE);
            assertThat(memory.readIntAt(1)).isEqualTo(FIRST_INT);
            assertThat(memory.readByteAt(5)).isEqualTo(SECOND_BYTE);
            assertThat(memory.readIntAt(6)).isEqualTo(SECOND_INT);
        }
    }

    default void readLETest(OffHeap memory) {
        try (memory) {
            assertThat(memory.readByteAt(0)).isEqualTo(FIRST_BYTE);
            assertThat(memory.readIntAtLE(1)).isEqualTo(FIRST_INT);
            assertThat(memory.readByteAt(5)).isEqualTo(SECOND_BYTE);
            assertThat(memory.readIntAtLE(6)).isEqualTo(SECOND_INT);
        }
    }
}
