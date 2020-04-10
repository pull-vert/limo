/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

import io.limo.memory.OffHeapFactory;

module limo.commons {
    requires jdk.incubator.foreign;
    requires jdk.unsupported;
    requires org.jetbrains.annotations;
    requires org.slf4j;

    uses OffHeapFactory;

    exports io.limo;
    exports io.limo.concurrent;
    exports io.limo.memory;
    exports io.limo.string;
    exports io.limo.utils;
}
