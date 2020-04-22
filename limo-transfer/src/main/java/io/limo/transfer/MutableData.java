/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.transfer;

import io.limo.IndexedWriter;

/**
 * A complete read-write (mutable) binary data
 */
public interface MutableData extends Data, IndexedWriter {
}
