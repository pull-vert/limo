/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.utils;

import io.limo.Writer;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

/**
 * Util class providing unsafe optimised operations on native ByteBuffer (fallback to safe if unsafe is not supported)
 */
public final class UnsafeByteBufferOps {

    private static final Ops OPS = (UnsafeAccess.UNSAFE_BYTE_BUFFER_ADDRESS_OFFSET != null) ? new UnsafeOps() : new SafeOps();

    // uninstanciable
    private UnsafeByteBufferOps() {
    }

    /**
     * Write operations in ByteBuffer without any index bound check (very dangerous !!)
     */
    public static int write(ByteBuffer bybu, int index, Consumer<Writer> consumer) {
        return OPS.write(bybu, index, consumer);
    }

    public static void invokeCleaner(ByteBuffer bybu) {
        OPS.invokeCleaner(bybu);
    }

    /**
     * Copy the contents of this ByteBuffer into a byte array
     */
    public static void fillTargetByteArray(ByteBuffer bybu, int index, byte[] bytes, int offset, int length) {
        OPS.fillTargetByteArray(bybu, index, bytes, offset, length);
    }

    /**
     * Copy the contents of this byte array into a ByteBuffer.
     * <p>Does no change position
     */
    public static ByteBuffer fillWithByteArray(ByteBuffer bybu, int index, byte[] bytes, int offset, int length) {
        return OPS.fillWithByteArray(bybu, index, bytes, offset, length);
    }

    /**
     * Returns the base memory address of this ByteBuffer. Only Unsafe can provide this.
     * <p>safe implementation always returns 0
     */
    public static long getBaseAddress(ByteBuffer bybu) {
        return OPS.getBaseAddress(bybu);
    }

    private static abstract class Ops {

        abstract long getBaseAddress(ByteBuffer bybu);

        abstract int write(ByteBuffer bybu, int index, Consumer<Writer> consumer);

        abstract void invokeCleaner(ByteBuffer bybu);

        abstract void fillTargetByteArray(ByteBuffer bybu, int index, byte[] bytes, int offset, int length);

        public abstract ByteBuffer fillWithByteArray(ByteBuffer bybu, int index, byte[] bytes, int offset, int length);
    }

    private static final class UnsafeOps extends Ops {

        private static final long BYTE_BUFFER_ADDRESS_OFFSET = UnsafeAccess.UNSAFE_BYTE_BUFFER_ADDRESS_OFFSET;

        @Override
        long getBaseAddress(ByteBuffer bybu) {
            return UnsafeAccess.getLong(bybu, BYTE_BUFFER_ADDRESS_OFFSET);
        }

        // this allows to write directly in off-heap Memory of a native ByteBuffer
        @Override
        final int write(ByteBuffer bybu, int index, Consumer<Writer> consumer) {
            final var baseAddress = getBaseAddress(bybu);
            final var bybuWriter = new UnsafeByBuWriter(baseAddress + index);
            consumer.accept(bybuWriter);
            return (int) (bybuWriter.address - baseAddress);
        }

        private static final class UnsafeByBuWriter implements Writer {

            private long address;

            private UnsafeByBuWriter(long startAddress) {
                this.address = startAddress;
            }

            @Override
            public Writer writeByte(byte value) {
                final var currentAddress = this.address;
                UnsafeAccess.putByte(currentAddress, value);
                this.address = currentAddress + 1;
                return this;
            }

            @Override
            public Writer writeInt(int value) {
                final var currentAddress = this.address;
                UnsafeAccess.putInt(currentAddress, value);
                this.address = currentAddress + 4;
                return this;
            }
        }

        @Override
        void invokeCleaner(ByteBuffer bybu) {
            UnsafeAccess.invokeCleaner(bybu);
        }

        @Override
        void fillTargetByteArray(ByteBuffer bybu, int index, byte[] bytes, int offset, int length) {
            final var baseAddress = UnsafeAccess.getLong(bybu, BYTE_BUFFER_ADDRESS_OFFSET);
            UnsafeAccess.copyMemory(null, baseAddress + index, bytes,
                    UnsafeArrayOps.UnsafeOps.BYTE_ARRAY_BASE_OFFSET + offset, length);
        }

        @Override
        public ByteBuffer fillWithByteArray(ByteBuffer bybu, int index, byte[] bytes, int offset, int length) {
            final var baseAddress = UnsafeAccess.getLong(bybu, BYTE_BUFFER_ADDRESS_OFFSET);
            UnsafeAccess.copyMemory(bytes, UnsafeArrayOps.UnsafeOps.BYTE_ARRAY_BASE_OFFSET + offset, null,
                    baseAddress + index, length);
            return bybu;
        }
    }

    private static final class SafeOps extends Ops {

        @Override
        long getBaseAddress(ByteBuffer bybu) {
            return 0;
        }

        @Override
        final int write(ByteBuffer bybu, int index, Consumer<Writer> consumer) {
            final var bybuWriter = new SafeByteBufferWriter(bybu, index);
            consumer.accept(bybuWriter);
            return bybuWriter.writeIndex;
        }

        private static final class SafeByteBufferWriter implements Writer {
            
            private final ByteBuffer bybu;
            private int writeIndex;

            private SafeByteBufferWriter(ByteBuffer bybu, int startIndex) {
                this.bybu = bybu;
                this.writeIndex = startIndex;
            }

            @Override
            public Writer writeByte(byte value) {
                final var index = this.writeIndex;
                bybu.put(index, value);
                this.writeIndex = index + 1;
                return this;
            }

            @Override
            public Writer writeInt(int value) {
                final var index = this.writeIndex;
                bybu.putInt(index, value);
                this.writeIndex = index + 4;
                return this;
            }
        }

        // NOP without unsafe, just wait for garbage collection :)
        @Override
        void invokeCleaner(ByteBuffer bybu) {
        }

        @Override
        void fillTargetByteArray(ByteBuffer bybu, int index, byte[] bytes, int offset, int length) {
            // save previous position
            final var position = bybu.position();

            bybu.position(index);
            bybu.get(bytes, offset, length);

            // re-affect previous position
            bybu.position(position);
        }

        @Override
        public ByteBuffer fillWithByteArray(ByteBuffer bybu, int index, byte[] bytes, int offset, int length) {
            // save previous position
            final var position = bybu.position();

            bybu.position(index);
            bybu.put(bytes, offset, length);

            // re-affect previous position
            return bybu.position(position);
        }
    }
}
