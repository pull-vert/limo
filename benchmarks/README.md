## benchmarks for limo

### JMH benchmark results
Benchmark                                                            Mode  Cnt         Score         Error  Units
ByteBufferReadBenchmark.directRead                                  thrpt    5     13936.010 ±     128.056  ops/s
ByteBufferReadBenchmark.indexedRead                                 thrpt    5  84239684.584 ±  286217.250  ops/s
ByteBufferReadBenchmark.indexedReadMemorySegmentAssociated          thrpt    5  80988567.445 ± 1610139.339  ops/s
ByteBufferReadBenchmark.varhandleMemorySegmentReadGroupAndStruct    thrpt    5       903.596 ±      23.540  ops/s
ByteBufferReadBenchmark.varhandleRead                               thrpt    5      1830.938 ±       8.449  ops/s
ByteBufferWriteBenchmark.directWrite                                thrpt    5       337.695 ±       6.118  ops/s
ByteBufferWriteBenchmark.indexedWrite                               thrpt    5       485.777 ±      35.000  ops/s
ByteBufferWriteBenchmark.indexedWriteMemorySegmentAssociated        thrpt    5       387.543 ±      27.078  ops/s
ByteBufferWriteBenchmark.varhandleMemorySegmentWrite                thrpt    5        35.589 ±       4.478  ops/s
ByteBufferWriteBenchmark.varhandleMemorySegmentWriteGroupAndStruct  thrpt    5       289.529 ±      21.715  ops/s
ByteBufferWriteBenchmark.varhandleWrite                             thrpt    5        46.381 ±      15.373  ops/s

