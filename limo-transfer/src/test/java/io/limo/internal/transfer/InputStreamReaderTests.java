/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.transfer;

import io.limo.transfer.Reader;

import java.io.ByteArrayInputStream;

public class InputStreamReaderTests implements ReaderTests {

    @Override
    public Reader instanciateReader(byte[] byteArray) {
        return new InputStreamReader(new ByteArrayInputStream(byteArray));
    }
}
