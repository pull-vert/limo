/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.bytes;

import org.jetbrains.annotations.NotNull;

/**
 * A complete binary data
 */
public interface MutableData extends Data {

    /**
     * @return the data writer
     */
    @NotNull
    Writer getWriter();
}
