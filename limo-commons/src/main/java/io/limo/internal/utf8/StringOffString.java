/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.utf8;

import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * OffString built from a java.lang.String
 */
public class StringOffString extends AbstractOffString {

    private final @NotNull String string;

    public StringOffString(@NotNull String string) {
        super(Objects.requireNonNull(string).getBytes(StandardCharsets.UTF_8));
        this.string = string;
    }

    @Override
    public @NotNull CharSequence toCharSequence() {
        return this.string;
    }

    @Override
    public @NotNull String toString() {
        return this.string;
    }
}
