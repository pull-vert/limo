/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.utils;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

/**
 * Util class providing unsafe optimised operations on native ByteBuffer (fallback to safe if unsafe is not supported)
 */
public final class ByteBuffers {

    private static Ops OPS = (UnsafeAccess.UNSAFE_BYTE_BUFFER_ADDRESS_OFFSET != null) ? new UnsafeOps() : new SafeOps();

    public static void write(ByteBuffer bb, Consumer<ByteBufferWriter> consumer) {
        OPS.write(bb, consumer);
    }

    // uninstanciable
    private ByteBuffers() {
    }

    public interface ByteBufferWriter {
        void put(byte value);
    }

    private static abstract class Ops {

        abstract void write(ByteBuffer bb, Consumer<ByteBufferWriter> consumer);
    }

    private static final class UnsafeOps extends Ops {

        private static final long BYTE_BUFFER_ADDRESS_OFFSET = UnsafeAccess.UNSAFE_BYTE_BUFFER_ADDRESS_OFFSET;

        @Override
        final void write(ByteBuffer bb, Consumer<ByteBufferWriter> consumer) {
            final var baseAddress = UnsafeAccess.getLong(bb, BYTE_BUFFER_ADDRESS_OFFSET);
            final var bbWriter = new UnsafeByteBufferWriter(baseAddress + bb.position());
            consumer.accept(bbWriter);
            bb.position((int) (bbWriter.position - baseAddress));
        }

        private static final class UnsafeByteBufferWriter implements ByteBufferWriter {

            private long position;

            private UnsafeByteBufferWriter(long startIndex) {
                this.position = startIndex;
            }

            @Override
            public void put(byte value) {
                UnsafeAccess.putByte(this.position++, value);
            }
        }
    }

    private static final class SafeOps extends Ops {

        @Override
        final void write(ByteBuffer bb, Consumer<ByteBufferWriter> consumer) {
            consumer.accept(new ByteBufferWriter() {
                @Override
                public void put(byte value) {
                    bb.put(value);
                }
            });
        }
    }
}
