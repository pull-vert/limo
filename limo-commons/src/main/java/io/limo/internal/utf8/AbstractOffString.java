/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.utf8;

import io.limo.utf8.OffString;
import jdk.incubator.foreign.MemorySegment;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

abstract class AbstractOffString implements OffString {

    /**
     * This MemorySegment stores utf-8 bytes
     */
    final @NotNull MemorySegment segment;

    /**
     * @param bytes utf-8 bytes
     */
    protected AbstractOffString(byte @NotNull [] bytes) {
        this(MemorySegment.allocateNative(Objects.requireNonNull(bytes).length));
        segment.asByteBuffer().put(bytes);
    }

    AbstractOffString(@NotNull MemorySegment segment) {
        this.segment = Objects.requireNonNull(segment);
    }

    @Override
    public @NotNull MemorySegment getSegment() {
        return this.segment;
    }

    @Override
    abstract @NotNull public String toString();
}
