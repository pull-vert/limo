/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.bench;

import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class ByteBufferWriteBench {

    private static final int OBJ_SIZE = 8 + 4 + 1;
    private static final int NUM_ELEM = 1_000_000;

    private static final VarHandle intHandle = MethodHandles.byteBufferViewVarHandle(int[].class, ByteOrder.BIG_ENDIAN);
    private static final VarHandle longHandle = MethodHandles.byteBufferViewVarHandle(long[].class, ByteOrder.BIG_ENDIAN);

    private ByteBuffer bb;
    private ByteBuffer bb2;
    private MemorySegment segment;
    private MemoryAddress base;

    public void setup() {
        bb = ByteBuffer.allocateDirect(OBJ_SIZE * NUM_ELEM);
        segment = MemorySegment.allocateNative(OBJ_SIZE * NUM_ELEM);
        base = segment.baseAddress();
        bb2 = segment.asByteBuffer();
    }

    public void directWrite() {
        bb.clear();
        for (int i = 0; i < NUM_ELEM; i++) {
            bb.putLong(i);
            bb.putInt(i);
            bb.put((byte) (i & 1));
        }
    }

    public void indexedWrite() {
        bb.clear();
        var index = 0;
        for (int i = 0; i < NUM_ELEM; i++) {
            index = OBJ_SIZE * i;
            bb.putLong(index, i);
            bb.putInt(index + 8, i);
            bb.put(index + 12, (byte) (i & 1));
        }
    }

    public void varhandleWrite() {
        bb.clear();
        var index = 0;
        for (int i = 0; i < NUM_ELEM; i++) {
            index = OBJ_SIZE * i;
            longHandle.set(bb, index, i);
            intHandle.set(bb, index + 8, i);
            bb.put(index + 12, (byte) (i & 1));
        }
    }
}
