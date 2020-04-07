/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.string;

import org.jetbrains.annotations.NotNull;

import java.nio.charset.Charset;

/**
 * OffString built from a java.lang.String
 */
public final class StringOffString extends AbstractOffString {

    private final String string;

    public StringOffString(String string, Charset charset) {
        this(string, UnsafeStringCoding.encodeUnsafe(string, charset));
    }

    private StringOffString(String string, UnsafeStringCoding.Result result) {
        super(result.value, result.charset);
        this.isAscii = result.isAscii;
        this.isLatin1 = result.isLatin1;
        this.string = string;
    }

    @Override
    public final @NotNull String toString() {
        return this.string;
    }
}
