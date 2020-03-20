/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.experimental.loom;

/**
 * Interrelated interfaces and static methods for establishing
 * flow-controlled components in which {@link Publisher Publishers}
 * produce items consumed by one or more {@link Subscriber
 * Subscribers}.
 *
 * <p>These interfaces are a simplified Loom version to the <a
 * href="http://www.reactive-streams.org/"> reactive-streams</a>
 * specification.  They apply in both concurrent and distributed
 * asynchronous settings: All (two) methods are defined in {@code
 * void} "one-way" message style.
 */
public final class LoomFlow {

    // uninstantiable
    private LoomFlow() {
    }

    @FunctionalInterface
    public interface Publisher<T> {
        public void subscribe(Subscriber<? super T> subscriber);
    }

    @FunctionalInterface
    public interface Subscriber<T> {
        void emit(T item);
    }
}
