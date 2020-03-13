# Limo

Limo is a IO library based on [Project Loom virtual threads](https://wiki.openjdk.java.net/display/loom/Main).

## Limo features

### Virtual threads

* Virtual threads are much lighter than regular JDK threads, allowing millions of them running at the same time.
* No need for Thread pooling, we just create a fresh new virtual thread when needed.
* With virtual threads, we do not need complex synchronisation required by multi-threaded Selector based NIO.

### Other Java features

* JDK9 Limo is modular thanks to Jigsaw modules
* JDK15 Project Loom virtual threads (in progress)
* JDK14 foreign memory (in progress)
* JDK11 TLSv1.3 (later)
* JDK9 compact Strings (later)
... more to come

## Requirements

Limo compiles and executes on a [Project Loom EA JDK](http://jdk.java.net/loom/).

## Inspirations and related documentation
* [Quasar](https://github.com/puniverse/quasar), loom ancestor on JDK
* [Chronicle Bytes](https://github.com/OpenHFT/Chronicle-Bytes)
* [kotlinx-io](https://github.com/Kotlin/kotlinx-io)
* [Netty](https://github.com/netty/netty)
* [JDK14 foreign memory](http://cr.openjdk.java.net/~mcimadamore/panama/memaccess_javadoc/jdk/incubator/foreign/package-summary.html)
