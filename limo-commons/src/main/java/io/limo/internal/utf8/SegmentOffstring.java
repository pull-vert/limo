/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.utf8;

import jdk.incubator.foreign.MemorySegment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

public class SegmentOffstring extends AbstractOffString {

    /**
     * late init string, only assigned if {@link #toString} is called here or in CharSequence
     */
    @Nullable String string;

    public SegmentOffstring(@NotNull MemorySegment segment, @NotNull ByteBuffer bb) {
        super(segment, bb);
    }

    @Override
    public @NotNull CharSequence toCharSequence() {
        return (this.string != null) ? this.string : createCharSequence(this.segment.toByteArray());
    }

    private @NotNull CharSequence createCharSequence(byte @NotNull [] bytes) {
        // check if pure ASCII bytes
        var index = 0;
        final var limit = bytes.length;
        while (index < limit && bytes[index] >= 0) {
            index++;
        }

        return (index == limit) ? new AsciiCharSeq(bytes) : stringFromBytes(bytes);
    }

    @Override
    public @NotNull String toString() {
        return stringFromBytes(this.segment.toByteArray());
    }

    private @NotNull String stringFromBytes(byte @NotNull [] bytes) {
        return this.string = new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * Simple CharSequence implementation for Ascii
     */
    private class AsciiCharSeq implements CharSequence {

        private final byte @NotNull [] bytes;

        private AsciiCharSeq(byte @NotNull [] bytes) {
            this.bytes = Objects.requireNonNull(bytes);
        }

        @Override
        public int length() {
            return this.bytes.length;
        }

        @Override
        public char charAt(int index) {
            if (index < 0 || index >= this.bytes.length) {
                throw new StringIndexOutOfBoundsException(index);
            }
            return (char) (this.bytes[index] & 0xff);
        }

        @Override
        public @NotNull CharSequence subSequence(int start, int end) {
            return new AsciiCharSeq(Arrays.copyOfRange(this.bytes, start, end));
        }

        @Override
        public @NotNull String toString() {
            return stringFromBytes(this.bytes);
        }
    }
}
