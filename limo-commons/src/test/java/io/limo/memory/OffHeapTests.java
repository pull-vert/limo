package io.limo.memory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class OffHeapTests {

    @Test
    @DisplayName("Verify OffHeap allocate requested long length")
    void allocateLong() {
        try(final var memory = OffHeapFactory.allocate(2L)) {
            assertThat(memory.getByteSize())
                    .isEqualTo(2);
        }
    }
}
