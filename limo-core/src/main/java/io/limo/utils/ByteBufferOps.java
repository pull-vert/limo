/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.utils;

import io.limo.internal.utils.ByteBuffers;

import java.nio.ByteBuffer;

public final class ByteBufferOps {

    // uninstanciable
    private ByteBufferOps() {
    }

    /**
     * Copy the contents of this byte array into a ByteBuffer.
     * <p>Does no change position
     */
    public static void fillWithByteArray(ByteBuffer bb, int index, byte[] bytes, int offset, int length) {
        ByteBuffers.fillWithByteArray(bb, index, bytes, offset, length);
    }
}
