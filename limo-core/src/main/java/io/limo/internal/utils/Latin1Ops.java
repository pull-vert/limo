/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.utils;

import io.limo.memory.ByBuOffHeap;
import io.limo.memory.OffHeapFactory;

public final class Latin1Ops {

    // uninstanciable
    private Latin1Ops() {
    }

    /**
     * @param bytes Latin1 bytes
     * @return a UTF-8 encoded ByteBufferOffHeap built from Latin1 byte[] parameter
     */
    public static ByBuOffHeap encodelatinBytesToUTF8(final byte[] bytes) {
        final var bbMemory = OffHeapFactory.allocate(bytes.length << 1);
        final var bb = bbMemory.getByteBuffer();
        // position in this brand new ByteBuffer starts at 0
        UnsafeByteBufferOps.write(bb, writer -> {
            for (int sourceIndex = 0; sourceIndex < bytes.length; sourceIndex++) {
                byte c = bytes[sourceIndex];
                if (c < 0) {
                    writer
                            .writeByte((byte) (0xc0 | ((c & 0xff) >> 6)))
                            .writeByte((byte) (0x80 | (c & 0x3f)));
                } else {
                    writer.writeByte(c);
                }
            }
        });
        if (bb.position() == bbMemory.getByteSize()) {
            return bbMemory;
        }
        return bbMemory.slice(0, bb.position());
    }
}