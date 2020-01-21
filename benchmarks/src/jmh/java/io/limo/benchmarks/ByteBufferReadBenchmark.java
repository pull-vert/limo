/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.benchmarks;

import org.openjdk.jmh.annotations.*;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.TimeUnit;

@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(value = 1)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
public class ByteBufferReadBenchmark {

    private static final int OBJ_SIZE = 8 + 4 + 1;
    private static final int NUM_ELEM = 1_000_000;

    private static final VarHandle intHandle = MethodHandles.byteBufferViewVarHandle(int[].class, ByteOrder.BIG_ENDIAN);
    private static final VarHandle longHandle = MethodHandles.byteBufferViewVarHandle(long[].class, ByteOrder.BIG_ENDIAN);

    private ByteBuffer bb;

    @Setup
    public void setup() {
        bb = ByteBuffer.allocateDirect(OBJ_SIZE * NUM_ELEM);
        for (int i = 0; i < NUM_ELEM; i++) {
            bb.putLong(i);
            bb.putInt(i);
            bb.put((byte) (i & 1));
        }
    }

    @Benchmark
    public long directRead() {
        bb.rewind();
        var val = 0L;
        for (int i = 0; i < NUM_ELEM; i++) {
            val += bb.getLong();
            bb.getInt();
            bb.get();
        }
        return val;
    }

    @Benchmark
    public long indexedRead() {
        var val = 0L;
        var index = 0;
        for (int i = 0; i < NUM_ELEM; i++) {
            index = OBJ_SIZE * i;
            val += bb.getLong(index);
            bb.getInt(index + 8);
            bb.get(index + 12);
        }
        return val;
    }

    @Benchmark
    public long varhandleRead() {
        var val = 0L;
        var index = 0;
        for (int i = 0; i < NUM_ELEM; i++) {
            index = OBJ_SIZE * i;
            val += (long) longHandle.get(bb, index);
            intHandle.get(bb, index + 8);
            bb.get(index + 12);
        }
        return val;
    }
}
