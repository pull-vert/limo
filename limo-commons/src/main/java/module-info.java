/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

module limo.commons {
    requires jdk.incubator.foreign;
    requires jdk.unsupported;
    requires org.jetbrains.annotations;

    exports io.limo;
    exports io.limo.concurrent;
    exports io.limo.string;
    exports io.limo.utils;
}
