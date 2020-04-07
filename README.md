# Limo

Limo is a IO library that runs on [Project Loom virtual threads](https://wiki.openjdk.java.net/display/loom/Main) and uses [JDK14 foreign memory](http://cr.openjdk.java.net/~mcimadamore/panama/memaccess_javadoc/jdk/incubator/foreign/package-summary.html).

## Limo features

### Virtual threads

* Virtual threads are much lighter than regular JDK threads, allowing millions of them running at the same time.
* No need for Thread pooling, we just create a fresh new virtual thread when needed.
* With virtual threads, we do not need complex synchronisation required by multi-threaded Selector based NIO.

### Foreign Memory

* Foreign Memory was added as an experimental feature in JDK14 to support low-level, safe and efficient memory access.
* Foreign Memory provides nice features like spatial safety, temporal safety. and a strong thread-confinement.
* Limo uses off-heap native MemorySegments.

### Other Java features

* JDK9
  * Limo is fully modular thanks to Jigsaw modules
  * Compact Strings (in progress)
* JDK11 TLSv1.3 (later)
* JDK14 foreign memory (in progress)
* JDK15 (maybe) Project Loom virtual threads (in progress)
... more to come

## Requirements

Limo compiles and executes on a [Project Loom Early Access JDK](http://jdk.java.net/loom/).

## Inspirations and other nice IO libraries
* [Quasar](https://github.com/puniverse/quasar), loom ancestor on JDK
* [Chronicle Bytes](https://github.com/OpenHFT/Chronicle-Bytes)
* [kotlinx-io](https://github.com/Kotlin/kotlinx-io)
* [Netty](https://github.com/netty/netty)
