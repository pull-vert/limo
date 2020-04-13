/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.memory;

import io.limo.internal.memory.OffHeapServiceLoader;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public interface OffHeapFactory {

    @NotNull OffHeap newOffHeap(long byteSize);

    @NotNull ByBuOffHeap newByteBufferOffHeap(int byteSize);

    @NotNull ByBuOffHeap newByteBufferOffHeap(byte @NotNull [] bytes);

    int getLoadPriority();

    static @NotNull OffHeap allocate(long byteSize) {
        return OffHeapServiceLoader.OFF_HEAP_FACTORY.newOffHeap(byteSize);
    }

    static @NotNull ByBuOffHeap allocate(int byteSize) {
        return OffHeapServiceLoader.OFF_HEAP_FACTORY.newByteBufferOffHeap(byteSize);
    }

    static @NotNull ByBuOffHeap of(byte @NotNull [] bytes) {
        return OffHeapServiceLoader.OFF_HEAP_FACTORY.newByteBufferOffHeap(Objects.requireNonNull(bytes));
    }
}
