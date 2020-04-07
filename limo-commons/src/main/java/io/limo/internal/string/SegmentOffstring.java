/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.string;

import jdk.incubator.foreign.MemorySegment;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public final class SegmentOffstring extends AbstractOffString {

    /**
     * late init string, only assigned if {@link SegmentOffstring#toString} is called
     */
    private String string;

    public SegmentOffstring(MemorySegment segment, Charset charset) {
        super(segment, charset);
        if (charset == StandardCharsets.US_ASCII) {
            this.isAscii = true;
            this.isLatin1 = true;
        } else if (charset == StandardCharsets.ISO_8859_1) {
            this.isLatin1 = true;
        }
    }

    @Override
    public final @NotNull String toString() {
        if (this.string != null) {
            return this.string;
        }

        // fast-path for Latin1 or ASCII (Latin1 is ASCII compatible)
        if (Boolean.TRUE.equals(this.isLatin1) || Boolean.TRUE.equals(this.isAscii)) {
            return this.string = UnsafeStringCoding.toLatin1String(this.segment.toByteArray());
        }
        return this.string = new String(this.segment.toByteArray(), this.charset);
    }
}
