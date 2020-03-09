# limo

* Limo is an IO framework based on standard Java IO running in Loom virtual threads.
* Virtual threads are much lighter than regular JDK threads, allowing millions of them running at the same time.
* This simplify conception because we do not need Thread pooling, we just create a fresh new virtual thread when needed.
* With dedicated virtual threads used with standard IO, we do not need complex synchronisation required by multi-threaded NIO.

## requirements

Limo compiles and executes on a [Project Loom EA JDK](http://jdk.java.net/loom/).

## inspirations and related documentation
* [Quasar](https://github.com/puniverse/quasar), loom ancestor on JDK
* [Chronicle Bytes](https://github.com/OpenHFT/Chronicle-Bytes)
* [kotlinx-io](https://github.com/Kotlin/kotlinx-io)
* [Netty](https://github.com/netty/netty)
* [JDK14 foreign memory](http://cr.openjdk.java.net/~mcimadamore/panama/memaccess_javadoc/jdk/incubator/foreign/package-summary.html)
