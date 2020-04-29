/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.transfer;

import io.limo.transfer.Writer;

import java.io.ByteArrayOutputStream;

public class OutputStreamWriterTests implements WriterTests {

    private ByteArrayOutputStream out;

    @Override
    public Writer instanciateWriter() {
        this.out = new ByteArrayOutputStream(10);
        return new OutputStreamWriter(this.out);
    }

    @Override
    public byte[] writtenByteArray() {
        return this.out.toByteArray();
    }
}
