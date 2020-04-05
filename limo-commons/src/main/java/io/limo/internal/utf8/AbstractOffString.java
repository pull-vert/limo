/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.utf8;

import io.limo.utf8.OffString;
import jdk.incubator.foreign.MemorySegment;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.util.Objects;

abstract class AbstractOffString implements OffString {

    final @NotNull MemorySegment segment;

    /**
     * This ByteBuffer store utf-8 bytes
     */
    private final @NotNull ByteBuffer bb;

    /**
     * @param bytes utf-8 bytes
     */
    protected AbstractOffString(byte @NotNull [] bytes) {
        this(Objects.requireNonNull(bytes), MemorySegment.allocateNative(bytes.length));
    }

    private AbstractOffString(byte @NotNull [] bytes, @NotNull MemorySegment segment) {
        this(segment, segment.asByteBuffer().put(bytes));
    }

    protected AbstractOffString(@NotNull MemorySegment segment, @NotNull ByteBuffer bb) {
        this.segment = Objects.requireNonNull(segment);
        this.bb = Objects.requireNonNull(bb);
    }

    @Override
    public @NotNull MemorySegment getSegment() {
        return this.segment;
    }

    @Override
    public @NotNull ByteBuffer getByteBuffer() {
        return this.bb;
    }

    @Override
    abstract @NotNull public String toString();
}
