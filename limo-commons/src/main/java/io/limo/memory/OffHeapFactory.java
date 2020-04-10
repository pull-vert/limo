/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.memory;

import io.limo.internal.memory.OffHeapServiceLoader;
import io.limo.internal.utils.ByteBuffers;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public interface OffHeapFactory {

    @NotNull OffHeap newOffHeap(long byteSize);

    @NotNull ByteBufferOffHeap newByteBufferOffHeap(int byteSize);

    int getLoadPriority();

    static OffHeap allocate(long byteSize) {
        return OffHeapServiceLoader.OFF_HEAP_FACTORY.newOffHeap(byteSize);
    }

    static ByteBufferOffHeap allocate(int byteSize) {
        return OffHeapServiceLoader.OFF_HEAP_FACTORY.newByteBufferOffHeap(byteSize);
    }

    static ByteBufferOffHeap of(byte @NotNull [] bytes) {
        final var bbMemory = allocate(Objects.requireNonNull(bytes).length);
        ByteBuffers.fillWithByteArray(bbMemory.getByteBuffer(), 0, bytes, 0, bytes.length);
        return bbMemory;
    }
}
