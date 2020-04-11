/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.concurrent;

import org.jetbrains.annotations.ApiStatus;

/**
 * Interrelated interfaces and static methods for establishing
 * flow-controlled components in which {@link Publisher Publishers}
 * produce items consumed by one or more {@link Subscriber Subscribers}.
 *
 * <p>These interfaces are a simplified version to the <a
 * href="http://www.reactive-streams.org/"> reactive-streams</a>
 * specification.
 * They apply in both concurrent and distributed
 * asynchronous settings: All (three) methods are defined in {@code
 * void} "one-way" message style.
 *
 * <p>Loom allows an imperative style code that greatly simplify Flow API by making blocking IO operations non-blocking
 * transparently without any change when running in Loom virtual threads.
 */
@ApiStatus.Experimental
public final class LimoFlow {

    // uninstantiable
    private LimoFlow() {
    }

    @ApiStatus.Experimental
    @FunctionalInterface
    public interface Publisher<T> {
        void subscribe(Subscriber<? super T> subscriber);
    }

    @ApiStatus.Experimental
    public interface Subscriber<T> {
        void emit(T item);
        void cancel();
    }
}
