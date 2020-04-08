/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.string;

import io.limo.ByteBufferSegment;
import io.limo.string.OffString;
import jdk.incubator.foreign.MemorySegment;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;

abstract class AbstractOffString implements OffString {

    /**
     * This MemorySegment stores bytes
     */
    final MemorySegment segment;

    /**
     * An optional native off-heap ByteBuffer bound to the segment
     */
    private final ByteBuffer bb;

    final Charset charset;

    Boolean isLatin1;

    Boolean isAscii;

    AbstractOffString(MemorySegment segment, Charset charset) {
        this(segment, (ByteBuffer) null, charset);
    }

    AbstractOffString(byte[] bytes, Charset charset) {
        this(MemorySegment.allocateNative(bytes.length), bytes, charset);
    }

    private AbstractOffString(MemorySegment segment, byte[] bytes, Charset charset) {
        this(segment, segment.asByteBuffer().put(bytes), charset);
    }

    public AbstractOffString(ByteBufferSegment bbSegment, Charset charset) {
this(bbSegment.getSegment(), bbSegment.getByteBuffer(), charset);
    }

    private AbstractOffString(MemorySegment segment, ByteBuffer bb, Charset charset) {
        this.segment = segment;
        this.bb = bb;
        this.charset = charset;
    }

    @Override
    public final @NotNull MemorySegment getSegment() {
        return this.segment;
    }

    @Override
    public final @NotNull Optional<ByteBuffer> getByteBuffer() {
        return Optional.ofNullable(this.bb);
    }

    @Override
    public final @NotNull Charset getCharset() {
        return this.charset;
    }

    @Override
    public final @NotNull MemorySegment toSegment(@NotNull Charset charset) {
        // fast-path 1) if destination charset is the same, or is fully compatible with current
        if (Objects.requireNonNull(charset).contains(this.charset)) {
            return this.segment;
        }

        // fast-path 2) if destination and current charsets are ASCII compatible, then current OffString is maybe ASCII
        if (charset.contains(StandardCharsets.US_ASCII)
                && this.charset.contains(StandardCharsets.US_ASCII)
                && this.isAscii == null) {

        }

        return MemorySegment.allocateNative(0); // fixme implement !
    }

    @Override
    public final void close() {
        this.segment.close();
    }

    @Override
    public abstract @NotNull String toString();
}
