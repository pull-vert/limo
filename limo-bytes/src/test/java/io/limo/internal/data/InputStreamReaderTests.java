/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.data;

import io.limo.data.Reader;

import java.io.ByteArrayInputStream;

public class InputStreamReaderTests implements ReaderTests {

    @Override
    public Reader instanciateReader(byte[] byteArray) {
        return new InputStreamReader(new ByteArrayInputStream(byteArray));
    }
}
