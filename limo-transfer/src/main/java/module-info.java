/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

module limo.transfer {
    requires jdk.incubator.foreign;
    requires limo.core;
    requires limo.core.jdk14;
    requires org.jetbrains.annotations;
    requires org.slf4j;

    exports io.limo.transfer;
}
