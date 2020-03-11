# Limo

Limo is an IO framework based on [Project Loom virtual threads](https://wiki.openjdk.java.net/display/loom/Main).

## Features

### Virtual threads

* Virtual threads are much lighter than regular JDK threads, allowing millions of them running at the same time.
* No need for Thread pooling, we just create a fresh new virtual thread when needed.
* With virtual threads, we do not need complex synchronisation required by multi-threaded Selector based NIO.

### Java 9 modules

* Limo is Java 9 Jigsaw module compliant

### Modern Java features

* JDK15 Project Loom virtual threads (in progress)
* JDK11 TLSv1.3 (later)
* JDK9 compact Strings (in progress)
... more to come

## Requirements

Limo compiles and executes on a [Project Loom EA JDK](http://jdk.java.net/loom/).

## Inspirations and related documentation
* [Quasar](https://github.com/puniverse/quasar), loom ancestor on JDK
* [Chronicle Bytes](https://github.com/OpenHFT/Chronicle-Bytes)
* [kotlinx-io](https://github.com/Kotlin/kotlinx-io)
* [Netty](https://github.com/netty/netty)
* [JDK14 foreign memory](http://cr.openjdk.java.net/~mcimadamore/panama/memaccess_javadoc/jdk/incubator/foreign/package-summary.html)
