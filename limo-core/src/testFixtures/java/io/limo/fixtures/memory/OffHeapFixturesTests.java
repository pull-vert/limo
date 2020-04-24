/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.fixtures.memory;

import io.limo.memory.OffHeapFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static io.limo.fixtures.BinaryTestData.*;

public class OffHeapFixturesTests implements OffHeapReadTests, MutableOffHeapWriteTests {

    @Test
    @DisplayName("Verify MutableOffHeap allocate requested long length")
    void allocateLong() {
        try(final var memory = OffHeapFactory.allocate(2L)) {
            assertThat(memory.getByteSize())
                    .isEqualTo(2);
        }
    }

    @Test
    @DisplayName("Verify read using Big Endian is working")
    void readBE() {
        readBETest(OffHeapFactory.of(BYTES_BIG_ENDIAN));
    }

    @Test
    @DisplayName("Verify read using Little Endian is working")
    void readLE() {
        readLETest(OffHeapFactory.of(BYTES_LITTLE_ENDIAN));
    }

    @Test
    @DisplayName("Verify write using Big Endian is working with MutableOffHeap")
    void writeBE() {
        writeBETest(OffHeapFactory.allocate(10L));
    }

    @Test
    @DisplayName("Verify write using Little Endian is working with MutableOffHeap")
    void writeLE() {
        writeLETest(OffHeapFactory.allocate(10));
    }
}
