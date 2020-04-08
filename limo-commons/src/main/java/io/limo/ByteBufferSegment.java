/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo;

import jdk.incubator.foreign.MemorySegment;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.util.Objects;

/**
 * This class contains a native {@link MemorySegment} and a direct {@link ByteBuffer} that both point to the same off-heap memory region.
 */
public class ByteBufferSegment {
    private final @NotNull MemorySegment segment;
    private final @NotNull ByteBuffer bb;

    private ByteBufferSegment(byte @NotNull [] bytes, int offset, int length) {
        this.segment = MemorySegment.allocateNative(bytes.length);
        this.bb = this.segment.asByteBuffer().put(bytes, offset, length);
    }

    private ByteBufferSegment(@NotNull MemorySegment segment) {
        this.segment = segment;
        this.bb = segment.asByteBuffer();
    }

    public static ByteBufferSegment of(byte @NotNull [] bytes) {
        return new ByteBufferSegment(Objects.requireNonNull(bytes), 0, bytes.length);
    }

    public static ByteBufferSegment allocate(int byteSize) {
        return new ByteBufferSegment(MemorySegment.allocateNative(byteSize));
    }

    public final @NotNull MemorySegment getSegment() {
        return this.segment;
    }

    public final @NotNull ByteBuffer getByteBuffer() {
        return this.bb;
    }

    public final int getByteSize() {
        return (int) this.segment.byteSize();
    }

    public final int getposition() {
        return this.bb.position();
    }

    public final @NotNull ByteBufferSegment asSlice(long offset, int length) {
        return new ByteBufferSegment(this.segment.asSlice(offset, length));
    }
}
