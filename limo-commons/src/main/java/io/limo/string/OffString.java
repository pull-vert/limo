/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.string;

import io.limo.internal.string.SegmentOffstring;
import io.limo.internal.string.StringOffString;
import jdk.incubator.foreign.MemorySegment;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.Charset;
import java.util.Objects;

/**
 * String stored in off-heap memory using {@link MemorySegment} in UTF-8, the most widely used encoding
 * <p>
 * OffStrings are constant (immutable), their values cannot be changed after they are created.
 */
public interface OffString extends AutoCloseable {

    @NotNull MemorySegment toSegment(@NotNull Charset charset);

    @NotNull Charset getCharset();

    /**
     * Best effort to return a String from the {@link MemorySegment} (that may contains too much for one single String)
     *
     * @return a String that contains all chars from this OffString
     */
    @Override
    @NotNull String toString();

    static @NotNull OffString of(@NotNull String string, @NotNull Charset charset) {
        return new StringOffString(Objects.requireNonNull(string), Objects.requireNonNull(charset));
    }

    static @NotNull OffString of(@NotNull MemorySegment segment, @NotNull Charset charset) {
        return new SegmentOffstring(Objects.requireNonNull(segment), Objects.requireNonNull(charset));
    }
}
