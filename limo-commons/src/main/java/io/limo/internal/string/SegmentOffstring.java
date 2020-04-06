/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.string;

import jdk.incubator.foreign.MemorySegment;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.Charset;

public final class SegmentOffstring extends AbstractOffString {

    /**
     * late init string, only assigned if {@link SegmentOffstring#toString} is called
     */
    private String string;

    public SegmentOffstring(MemorySegment segment, Charset charset) {
        super(segment, charset);
    }

    @Override
    public @NotNull String toString() {
        if (this.string != null) {
            return this.string;
        }

        // fast-path for Latin1
        if (this.isLatin1 != null && this.isLatin1) {
            return this.string = UnsafeStringCoding.toLatin1String(this.segment.toByteArray());
        }
        return this.string = new String(this.segment.toByteArray(), this.charset);
    }
}
