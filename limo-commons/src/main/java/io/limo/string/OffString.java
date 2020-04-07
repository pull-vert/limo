/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.string;

import io.limo.internal.string.SegmentOffstring;
import io.limo.internal.string.StringOffString;
import jdk.incubator.foreign.MemorySegment;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Objects;
import java.util.Optional;

/**
 * String stored in off-heap memory as {@link MemorySegment} with a current {@link Charset}
 * <p>
 * OffStrings are constant (immutable), their values cannot be changed after they are created.
 */
public interface OffString extends AutoCloseable {

    /**
     * @return the current off-heap {@link MemorySegment} encoded with {@link Charset}
     * @see #getCharset() to obtain current charset
     */
    @NotNull MemorySegment getSegment();

    /**
     * @return a native off-heap ByteBuffer bound to the {@link MemorySegment} (if exists)
     */
    @NotNull Optional<ByteBuffer> getByteBuffer();

    /**
     * @return the current {@link Charset} used to encode String in off-heap memory
     * @see #getSegment() to obtain current MemorySegment
     */
    @NotNull Charset getCharset();

    @NotNull MemorySegment toSegment(@NotNull Charset charset);

    /**
     * Best effort to return a String from the {@link MemorySegment} (that may be too big for one single String)
     *
     * @return a String that contains all content of this OffString
     */
    @Override
    @NotNull String toString();

    /**
     * Close the {@link MemorySegment}
     */
    @Override
    void close();

    static @NotNull OffString of(@NotNull String string, @NotNull Charset charset) {
        return new StringOffString(Objects.requireNonNull(string), Objects.requireNonNull(charset));
    }

    static @NotNull OffString of(@NotNull MemorySegment segment, @NotNull Charset charset) {
        return new SegmentOffstring(Objects.requireNonNull(segment), Objects.requireNonNull(charset));
    }
}
