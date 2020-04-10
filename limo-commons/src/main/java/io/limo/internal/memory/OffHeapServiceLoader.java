/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.memory;

import io.limo.memory.OffHeapFactory;

import java.util.Comparator;
import java.util.ServiceLoader;
import java.util.stream.Stream;

public final class OffHeapServiceLoader {

    public static final OffHeapFactory OFF_HEAP_FACTORY = getOffHeapFactory();


    // uninstanciable
    private OffHeapServiceLoader() {
    }

    private static OffHeapFactory getOffHeapFactory() {
        final var iterator = ServiceLoader.load(OffHeapFactory.class).iterator();

        // If no module overrides this Service, fallback to core OffHeapFactoryImpl
        if (!iterator.hasNext()) {
            return new OffHeapFactoryImpl();
        }
        return Stream.generate(iterator::next).takeWhile((v) -> iterator.hasNext())
                .max(Comparator.comparingInt(OffHeapFactory::getLoadPriority))
                .get(); // this is safe thanks to previous hasNext check
    }
}
