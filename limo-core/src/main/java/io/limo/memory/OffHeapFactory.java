/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.memory;

import io.limo.internal.memory.OffHeapServiceLoader;
import io.limo.internal.utils.UnsafeByteBufferOps;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public interface OffHeapFactory {

    @NotNull OffHeap newOffHeap(long byteSize);

    @NotNull MutableSafeByBuOffHeap newMutableSafeByBuOffHeap(int byteSize);

    @NotNull MutableUnsafeByBuOffHeap newMutableUnsafeByBuOffHeap(int byteSize);

    @NotNull SafeByBuOffHeap newSafeByteBufferOffHeap(byte @NotNull [] bytes);

    @NotNull UnsafeByBuOffHeap newUnsafeByteBufferOffHeap(byte @NotNull [] bytes);

    int getLoadPriority();

    static @NotNull OffHeap allocate(long byteSize) {
        return OffHeapServiceLoader.OFF_HEAP_FACTORY.newOffHeap(byteSize);
    }

    static @NotNull MutableByBuOffHeap allocate(int byteSize) {
        if (UnsafeByteBufferOps.SUPPORT_UNSAFE) {
            return OffHeapServiceLoader.OFF_HEAP_FACTORY.newMutableUnsafeByBuOffHeap(byteSize);
        }
        return OffHeapServiceLoader.OFF_HEAP_FACTORY.newMutableSafeByBuOffHeap(byteSize);
    }

    static @NotNull ByBuOffHeap of(byte @NotNull [] bytes) {
        if (UnsafeByteBufferOps.SUPPORT_UNSAFE) {
            return OffHeapServiceLoader.OFF_HEAP_FACTORY.newUnsafeByteBufferOffHeap(Objects.requireNonNull(bytes));
        }
        return OffHeapServiceLoader.OFF_HEAP_FACTORY.newSafeByteBufferOffHeap(Objects.requireNonNull(bytes));
    }
}
