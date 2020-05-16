# Limo

[![License: Unlicense](https://img.shields.io/badge/license-Unlicense-blue.svg)](http://unlicense.org/)

:exclamation: **This project has migrated to [new location](https://github.com/ufoss-org/dino) :exclamation:

* Limo is a IO library that works with one thread per IO operation, for example a multiplexed TCP server that runs with Limo will create two threads for each client Socket : one thread for read operations and one thread for write operations.
* Limo is intended to be used in conjunction with [Project Loom virtual threads](https://wiki.openjdk.java.net/display/loom/Main) and [JDK14 foreign memory](http://cr.openjdk.java.net/~mcimadamore/panama/memaccess_javadoc/jdk/incubator/foreign/package-summary.html).

## Requirements

* Base modules of Limo require JDK11, the last Long Term Support Java version.
* Additional modules allow benefiting from additional features available in more recent JDKs, such as virtual threads and foreign memory, transparently thanks to ServiceLoader.
These additional modules have a **-jdk1X** suffix, so you easily know which modules you can import as dependency, depending on the JDK your project uses.

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
* JDK1X (release version is not known yet) Project Loom virtual threads (in progress)

... and more to come

## Compile

To compile Limo project you will need a [Project Loom Early Access JDK](http://jdk.java.net/loom/).

## Other inspiring libraries
* [Quasar](https://github.com/puniverse/quasar), loom ancestor on JDK
* [Chronicle Bytes](https://github.com/OpenHFT/Chronicle-Bytes)
* [kotlinx-io](https://github.com/Kotlin/kotlinx-io)
* [Netty](https://github.com/netty/netty)
