/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.string;

import io.limo.internal.utils.UnsafeStringOps;
import io.limo.memory.OffHeap;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static java.lang.Boolean.TRUE;

public final class OffstringImpl extends AbstractOffString {

    /**
     * late init string, only assigned if {@link OffstringImpl#toString} is called
     */
    private String string;

    public OffstringImpl(OffHeap memory, Charset charset) {
        super(memory, charset);
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
        if (TRUE.equals(this.isLatin1) || TRUE.equals(this.isAscii)) {
            return this.string = UnsafeStringOps.toLatin1String(this.memory.toByteArray());
        }
        return this.string = new String(this.memory.toByteArray(), this.charset);
    }
}
