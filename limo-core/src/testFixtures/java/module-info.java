/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

@SuppressWarnings("module")
open module limo.core.fixtures {
    requires org.junit.jupiter.api;
    requires org.assertj.core;
    requires limo.core;

    exports io.limo.fixtures.memory;
}
