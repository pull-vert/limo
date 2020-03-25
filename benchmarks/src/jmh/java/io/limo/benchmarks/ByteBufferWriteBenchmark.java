/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.benchmarks;

import io.limo.bench.ByteBufferWriteBench;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(value = 1)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
public class ByteBufferWriteBenchmark {

    ByteBufferWriteBench parent = new ByteBufferWriteBench();

    @Setup
    public void setup() {
        parent.setup();
    }

    @TearDown
    public void tearDown() {
        parent.tearDown();
    }

    @Benchmark
    public void directWrite() {
        parent.directWrite();
    }

    @Benchmark
    public void indexedWrite() {
        parent.indexedWrite();
    }

    @Benchmark
    public void varhandleWrite() {
        parent.varhandleWrite();
    }

    @Benchmark
    public void indexedWriteMemorySegmentAssociated() {
        parent.indexedWriteMemorySegmentAssociated();
    }

    @Benchmark
    public void varhandleMemorySegmentWrite() {
        parent.varhandleMemorySegmentWrite();
    }

    @Benchmark
    public void varhandleMemorySegmentWriteGroupAndStruct() {
        parent.varhandleMemorySegmentWriteGroupAndStruct();
    }
}
