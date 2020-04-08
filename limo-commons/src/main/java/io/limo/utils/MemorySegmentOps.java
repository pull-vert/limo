/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.utils;

import jdk.incubator.foreign.*;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.VarHandle;
import java.nio.ByteOrder;
import java.util.Objects;

/**
 * Util class providing operations on MemorySegment
 */
public final class MemorySegmentOps {

    private static final VarHandle BYTE_HANDLE = MemoryHandles.varHandle(byte.class, ByteOrder.BIG_ENDIAN);

    private static final VarHandle INT_AS_BYTE_SEQ_HANDLE = MemoryLayout.ofSequence(4, MemoryLayouts.BITS_8_BE)
            .varHandle(byte.class, MemoryLayout.PathElement.sequenceElement());

    // uninstanciable
    private MemorySegmentOps() { }


    public static byte readByte(@NotNull MemoryAddress address) {
        return (byte) BYTE_HANDLE.get(Objects.requireNonNull(address));
    }

    public static void writeByte(@NotNull MemoryAddress address, byte value) {
        BYTE_HANDLE.set(Objects.requireNonNull(address), value);
    }

    public static int readInt(@NotNull MemoryAddress address, final boolean isBigEndian) {
        return BytesOps.bytesToInt(
                (byte) INT_AS_BYTE_SEQ_HANDLE.get(address, 0L),
                (byte) INT_AS_BYTE_SEQ_HANDLE.get(address, 1L),
                (byte) INT_AS_BYTE_SEQ_HANDLE.get(address, 2L),
                (byte) INT_AS_BYTE_SEQ_HANDLE.get(address, 3L),
                isBigEndian
        );
    }

    public static void writeInt(@NotNull MemoryAddress address, final int value, final boolean isBigEndian) {
        if (isBigEndian) {
            INT_AS_BYTE_SEQ_HANDLE.set(address, 0L, (byte) ((value >> 24) & 0xff));
            INT_AS_BYTE_SEQ_HANDLE.set(address, 1L, (byte) ((value >> 16) & 0xff));
            INT_AS_BYTE_SEQ_HANDLE.set(address, 2L, (byte) ((value >> 8) & 0xff));
            INT_AS_BYTE_SEQ_HANDLE.set(address, 3L, (byte) (value & 0xff));
            return;
        }
        INT_AS_BYTE_SEQ_HANDLE.set(address, 0L, (byte) (value & 0xff));
        INT_AS_BYTE_SEQ_HANDLE.set(address, 1L, (byte) ((value >> 8) & 0xff));
        INT_AS_BYTE_SEQ_HANDLE.set(address, 2L, (byte) ((value >> 16) & 0xff));
        INT_AS_BYTE_SEQ_HANDLE.set(address, 3L, (byte) ((value >> 24) & 0xff));
    }
}
