/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.string;

import io.limo.internal.string.OffstringImpl;
import io.limo.internal.string.StringOffString;
import io.limo.memory.OffHeap;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.Charset;
import java.util.Objects;

/**
 * String stored in off-heap memory as {@link OffHeap} with a current {@link Charset}
 * <p>
 * OffStrings are constant (immutable), their values cannot be changed after they are created.
 */
public interface OffString extends AutoCloseable {

    /**
     * @return the current off-heap memory encoded with {@link Charset}
     * @see #getCharset() to obtain current charset
     */
    @NotNull OffHeap getMemory();

    /**
     * @return the current {@link Charset} used to encode String in off-heap memory
     * @see #getMemory() to obtain current MemorySegment
     */
    @NotNull Charset getCharset();

    @NotNull OffHeap toMemory(@NotNull Charset charset);

    /**
     * Best effort to return a String from the memory (that may be too big for one single String)
     *
     * @return a String that contains all content of this OffString
     * @throws UnsupportedOperationException if the memory's content cannot fit into a {@link String} instance,
     * e.g. it has more than {@link Integer#MAX_VALUE} characters
     */
    @Override
    @NotNull String toString();

    /**
     * Close the memory
     */
    @Override
    void close();

    static @NotNull OffString of(@NotNull String string, @NotNull Charset charset) {
        return new StringOffString(Objects.requireNonNull(string), Objects.requireNonNull(charset));
    }

    static @NotNull OffString of(@NotNull OffHeap memory, @NotNull Charset charset) {
        return new OffstringImpl(Objects.requireNonNull(memory), Objects.requireNonNull(charset));
    }
}
