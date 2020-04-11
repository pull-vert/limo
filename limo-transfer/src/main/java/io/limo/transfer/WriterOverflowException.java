package io.limo.transfer;

import io.limo.LimoIOException;

/**
 * Unchecked exception thrown when a relative <i>write</i> operation reaches the byte sequence's limit.
 */
public final class WriterOverflowException extends LimoIOException {

    /**
     * Constructs an instance of this class.
     */
    public WriterOverflowException() {
    }
}
