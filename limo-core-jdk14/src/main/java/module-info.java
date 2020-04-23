/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

import io.limo.internal.jdk14.memory.MemorySegmentOffHeapFactory;
import io.limo.memory.OffHeapFactory;

@SuppressWarnings("module")
module limo.core.jdk14 {
    requires jdk.incubator.foreign;
    requires limo.core;
    requires org.jetbrains.annotations;
    requires org.slf4j;

    exports io.limo.jdk14.utils;

    provides OffHeapFactory with MemorySegmentOffHeapFactory;
}
