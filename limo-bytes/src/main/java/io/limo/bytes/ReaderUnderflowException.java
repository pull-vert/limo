package io.limo.bytes;

import io.limo.LimoIOException;

/**
 * Unchecked exception thrown when a relative <i>read</i> operation reaches the byte sequence's limit.
 */
public final class ReaderUnderflowException extends LimoIOException {

    /**
     * Constructs an instance of this class.
     */
    public ReaderUnderflowException() {
    }
}
