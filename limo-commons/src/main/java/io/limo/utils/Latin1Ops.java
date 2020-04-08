/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.utils;

import io.limo.ByteBufferSegment;
import io.limo.internal.utils.ByteBuffers;

public final class Latin1Ops {

    // uninstanciable
    private Latin1Ops() {
    }

    /**
     * @param bytes Latin1 bytes
     * @return a UTF-8 encoded ByteBuffer built from Latin1 byte[] parameter
     */
    public static ByteBufferSegment encodeUTF8(final byte[] bytes) {
        final var bbSegment = ByteBufferSegment.allocate(bytes.length << 1);
        // position in this brand new ByteBuffer starts at 0
        ByteBuffers.write(bbSegment.getByteBuffer(), byteBufferWriter -> {
            for (int sourceIndex = 0; sourceIndex < bytes.length; sourceIndex++) {
                byte c = bytes[sourceIndex];
                if (c < 0) {
                    byteBufferWriter.put((byte) (0xc0 | ((c & 0xff) >> 6)));
                    byteBufferWriter.put((byte) (0x80 | (c & 0x3f)));
                } else {
                    byteBufferWriter.put(c);
                }
            }
        });
        if (bbSegment.getposition() == bbSegment.getByteSize()) {
            return bbSegment;
        }
        return bbSegment.asSlice(0, bbSegment.getposition());
    }
}
