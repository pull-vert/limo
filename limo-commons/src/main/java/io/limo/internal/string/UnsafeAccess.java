/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.string;

import sun.misc.Unsafe;

final class UnsafeAccess {
    final static private Unsafe unsafe;

    static {
        final Unsafe tmpUnsafe;

        try {
            final var field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            tmpUnsafe = (Unsafe) field.get(null);
        } catch (java.lang.Exception e) {
            throw new Error(e);
        }

        unsafe = tmpUnsafe;
    }

    // uninstanciable
    private UnsafeAccess() { }

    static Unsafe unsafe() {
        return unsafe;
    }
}
