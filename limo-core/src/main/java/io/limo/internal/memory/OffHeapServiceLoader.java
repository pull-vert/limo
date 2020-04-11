/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.memory;

import io.limo.memory.OffHeapFactory;

import java.util.Comparator;
import java.util.ServiceLoader;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

public final class OffHeapServiceLoader {

    public static final OffHeapFactory OFF_HEAP_FACTORY = getOffHeapFactory();


    // uninstanciable
    private OffHeapServiceLoader() {
    }

    private static OffHeapFactory getOffHeapFactory() {
        final var iterator = ServiceLoader.load(OffHeapFactory.class).iterator();

        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED),false)
                .max(Comparator.comparingInt(OffHeapFactory::getLoadPriority))
                // If no module in classpath implements this Service, fallback to base OffHeapFactoryImpl
                .orElse(new OffHeapFactoryBase());
    }
}
