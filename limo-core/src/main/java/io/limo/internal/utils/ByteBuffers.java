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

    private static final Ops OPS = (UnsafeAccess.UNSAFE_BYTE_BUFFER_ADDRESS_OFFSET != null) ? new UnsafeOps() : new SafeOps();

    // uninstanciable
    private ByteBuffers() {
    }

    /**
     * bulk write in ByteBuffer, in Unsafe version position is only set after all write operations
     */
    public static void write(ByteBuffer bb, Consumer<ByteBufferWriter> consumer) {
        OPS.write(bb, consumer);
    }

    public static void invokeCleaner(ByteBuffer bb) {
        OPS.invokeCleaner(bb);
    }

    /**
     * Copy the contents of this ByteBuffer into a byte array
     */
    public static void fillTargetByteArray(ByteBuffer bb, int index, byte[] bytes, int offset, int length) {
        OPS.fillTargetByteArray(bb, index, bytes, offset, length);
    }

    /**
     * Copy the contents of this byte array into a ByteBuffer.
     * <p>Does no change position
     */
    public static void fillWithByteArray(ByteBuffer bb, int index, byte[] bytes, int offset, int length) {
        OPS.fillWithByteArray(bb, index, bytes, offset, length);
    }

    /**
     * Returns the base memory address of this ByteBuffer. Only Unsafe can provide this.
     * <p>safe implementation always returns 0
     */
    public static long getBaseAddress(ByteBuffer bb) {
        return OPS.getBaseAddress(bb);
    }

    public interface ByteBufferWriter {
        void put(byte value);
    }

    private static abstract class Ops {

        abstract long getBaseAddress(ByteBuffer bb);

        abstract void write(ByteBuffer bb, Consumer<ByteBufferWriter> consumer);

        abstract void invokeCleaner(ByteBuffer bb);

        abstract void fillTargetByteArray(ByteBuffer bb, int index, byte[] bytes, int offset, int length);

        public abstract void fillWithByteArray(ByteBuffer bb, int index, byte[] bytes, int offset, int length);
    }

    private static final class UnsafeOps extends Ops {

        private static final long BYTE_BUFFER_ADDRESS_OFFSET = UnsafeAccess.UNSAFE_BYTE_BUFFER_ADDRESS_OFFSET;

        @Override
        long getBaseAddress(ByteBuffer bb) {
            return UnsafeAccess.getLong(bb, BYTE_BUFFER_ADDRESS_OFFSET);
        }

        // this allows to write directly in off-heap Memory of a native ByteBuffer
        @Override
        final void write(ByteBuffer bb, Consumer<ByteBufferWriter> consumer) {
            final var baseAddress = getBaseAddress(bb);
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

        @Override
        void invokeCleaner(ByteBuffer bb) {
            UnsafeAccess.invokeCleaner(bb);
        }

        @Override
        void fillTargetByteArray(ByteBuffer bb, int index, byte[] bytes, int offset, int length) {
            final var baseAddress = UnsafeAccess.getLong(bb, BYTE_BUFFER_ADDRESS_OFFSET);
            UnsafeAccess.copyMemory(null, baseAddress + index, bytes,
                    Arrays.UnsafeOps.BYTE_ARRAY_BASE_OFFSET + offset, length);
        }

        @Override
        public void fillWithByteArray(ByteBuffer bb, int index, byte[] bytes, int offset, int length) {
            final var baseAddress = UnsafeAccess.getLong(bb, BYTE_BUFFER_ADDRESS_OFFSET);
            UnsafeAccess.copyMemory(bytes, Arrays.UnsafeOps.BYTE_ARRAY_BASE_OFFSET + offset, null,
                    baseAddress + index, length);
        }
    }

    private static final class SafeOps extends Ops {

        @Override
        long getBaseAddress(ByteBuffer bb) {
            return 0;
        }

        @Override
        final void write(ByteBuffer bb, Consumer<ByteBufferWriter> consumer) {
            consumer.accept(bb::put);
        }

        // NOP without unsafe
        @Override
        void invokeCleaner(ByteBuffer bb) {
        }

        @Override
        void fillTargetByteArray(ByteBuffer bb, int index, byte[] bytes, int offset, int length) {
            // save previous position
            final var position = bb.position();

            bb.position(index);
            bb.get(bytes, offset, length);

            // re-affect previous position
            bb.position(position);
        }

        @Override
        public void fillWithByteArray(ByteBuffer bb, int index, byte[] bytes, int offset, int length) {
            // save previous position
            final var position = bb.position();

            bb.position(index);
            bb.put(bytes, offset, length);

            // re-affect previous position
            bb.position(position);
        }
    }
}
