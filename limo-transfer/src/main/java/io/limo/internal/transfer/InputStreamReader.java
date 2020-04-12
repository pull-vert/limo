/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.transfer;

import io.limo.LimoIOException;
import io.limo.Reader;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;

/**
 * Implementation of the {@link Reader} interface based on a {@link InputStream}
 */
public final class InputStreamReader implements Reader {

    private final @NotNull InputStream in;

    public InputStreamReader(@NotNull InputStream in) {
        this.in = in;
    }


    @Override
    public byte readByte() {
        try {
            final var ch = in.read();
            if (ch < 0) {
                throw new ReaderUnderflowException();
            }
            return (byte) ch;
        } catch (IOException ioException) {
            throw new LimoIOException(ioException);
        }
    }

    @Override
    public int readInt() {
        try {
            final var i0 = this.in.read();
            final var i1 = this.in.read();
            final var i2 = this.in.read();
            final var i3 = this.in.read();
            if ((i0 | i1 | i2 | i3) < 0) {
                throw new ReaderUnderflowException();
            }
            if (isBigEndian) {
                return ((i0 << 24) + (i1 << 16) + (i2 << 8) + i3);
            }
            return ((i3 << 24) + (i2 << 16) + (i1 << 8) + i0);
        } catch (IOException ioException) {
            throw new LimoIOException(ioException);
        }
    }
}
