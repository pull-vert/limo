/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.string;

import io.limo.string.OffString;
import jdk.incubator.foreign.MemorySegment;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.Charset;
import java.util.Objects;

abstract class AbstractOffString implements OffString {

    /**
     * This MemorySegment stores bytes
     */
    final MemorySegment segment;

    final Charset charset;

    Boolean isLatin1;

    Boolean isAscii;

    AbstractOffString(byte[] bytes, Charset charset) {
        this(MemorySegment.allocateNative(bytes.length), charset);
        segment.asByteBuffer().put(bytes);
    }

    AbstractOffString(MemorySegment segment, Charset charset) {
        this.segment = segment;
        this.charset = charset;
    }

    @Override
    public @NotNull MemorySegment toSegment(@NotNull Charset charset) {
        // fast-path 1) if destination charset is the same, or is fully compatible
        if (Objects.requireNonNull(charset).contains(this.charset)) {
            return this.segment;
        }
        return MemorySegment.allocateNative(0); // fixme implement !
    }

    @Override
    public @NotNull Charset getCharset() {
        return this.charset;
    }

    @Override
    abstract @NotNull public String toString();
}
