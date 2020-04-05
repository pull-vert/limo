/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.utf8;

import io.limo.internal.utf8.SegmentOffstring;
import io.limo.internal.utf8.StringOffString;
import jdk.incubator.foreign.MemorySegment;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.util.Objects;

/**
 * String stored in off-heap memory using {@link MemorySegment} in UTF-8, the most widely used encoding
 * <p>
 * OffStrings are constant (immutable), their values cannot be changed after they are created.
 */
public interface OffString {

    @NotNull MemorySegment getSegment();

    @NotNull ByteBuffer getByteBuffer();

    /**
     * @return a CharSequence built from this OffString
     */
    @ApiStatus.Experimental
    @NotNull CharSequence toCharSequence();

    @Override
    @NotNull String toString();

    static @NotNull OffString of(@NotNull String string) {
        return new StringOffString(Objects.requireNonNull(string));
    }

    static @NotNull OffString of(@NotNull MemorySegment segment, @NotNull ByteBuffer bb) {
        return new SegmentOffstring(Objects.requireNonNull(segment), Objects.requireNonNull(bb));
    }
}
