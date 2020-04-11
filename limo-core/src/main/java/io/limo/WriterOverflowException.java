/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo;

/**
 * Unchecked exception thrown when a relative <i>write</i> operation reaches the data's limit.
 */
public final class WriterOverflowException extends LimoIOException {

    /**
     * Constructs an instance of this class.
     */
    public WriterOverflowException() {
    }
}
