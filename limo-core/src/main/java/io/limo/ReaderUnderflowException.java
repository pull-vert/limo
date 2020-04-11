/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo;

/**
 * Unchecked exception thrown when a relative <i>read</i> operation reaches the data's limit.
 */
public final class ReaderUnderflowException extends LimoIOException {

    /**
     * Constructs an instance of this class.
     */
    public ReaderUnderflowException() {
    }
}
