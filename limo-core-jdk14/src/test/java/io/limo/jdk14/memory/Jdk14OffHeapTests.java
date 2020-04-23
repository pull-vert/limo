/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.jdk14.memory;

import io.limo.memory.OffHeapFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class Jdk14OffHeapTests {

    @Test
    @DisplayName("Verify OffHeap allocate requested long length")
    void allocateLong() {
        try(final var memory = OffHeapFactory.allocate(2L)) {
            assertThat(memory.getByteSize())
                    .isEqualTo(2);
        }
    }
}
