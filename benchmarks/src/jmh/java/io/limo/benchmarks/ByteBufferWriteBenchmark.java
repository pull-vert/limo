/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.benchmarks;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

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
public class ByteBufferWriteBenchmark {

    private static final int OBJ_SIZE = 8 + 4 + 1;
    private static final int NUM_ELEM = 1_000_000;

    private static final VarHandle intHandle = MethodHandles.byteBufferViewVarHandle(int[].class, ByteOrder.BIG_ENDIAN);
    private static final VarHandle longHandle = MethodHandles.byteBufferViewVarHandle(long[].class, ByteOrder.BIG_ENDIAN);

    private ByteBuffer bb;

    @Setup
    public void setup() {
        bb = ByteBuffer.allocateDirect(OBJ_SIZE * NUM_ELEM);
    }

    @Benchmark
    public void directWrite() {
        bb.clear();
        for (int i = 0; i < NUM_ELEM; i++) {
            bb.putLong(i);
            bb.putInt(i);
            bb.put((byte) (i & 1));
        }
    }

    @Benchmark
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

    @Benchmark
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
