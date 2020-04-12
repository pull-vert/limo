/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.utils;

import io.limo.memory.ByteBufferOffHeap;
import io.limo.memory.OffHeapFactory;
import io.limo.utils.AsciiOps;
import io.limo.utils.Latin1Ops;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Util class providing unsafe optimised operations on String (fallback to safe if unsafe is not supported)
 */
public final class UnsafeStringOps {

    private static final Ops OPS = (UnsafeAccess.UNSAFE_STRING_OFFSETS != null) ? new UnsafeOps() : new SafeOps();

    // uninstanciable
    private UnsafeStringOps() {
    }

    public static Result encode(String string, Charset charset) {
        return OPS.encode(string, charset);
    }

    public static String toLatin1String(byte[] bytes) {
        return OPS.toLatin1String(bytes);
    }

    private static abstract class Ops {
        /**
         * Never change the returned bytes in Result
         * <p>would break String parameter's immutability when bytes were obtained via Unsafe call !
         */
        abstract Result encode(String string, Charset charset);

        /**
         * Never change the parameter bytes used as parameter
         * <p>would break String parameter's immutability when using unsafe instantiation !
         */
        abstract String toLatin1String(byte[] bytes);
    }

    static final class UnsafeOps extends Ops {

        private static final byte LATIN1 = 0;
        private static final byte UTF16 = 1;
        private static final UnsafeAccess.StringOffsets STRING_OFFSETS = UnsafeAccess.UNSAFE_STRING_OFFSETS;

        static boolean isLatin1(String str) {
            return UnsafeAccess.getByte(str, STRING_OFFSETS.coderFieldOffset) == LATIN1;
        }

        @Override
        final Result encode(String string, Charset charset) {
            // ASCII compatible target charset require special path
            if (charset.contains(StandardCharsets.US_ASCII)) {
                // Check for Latin1 (ISO_8859_1) compact String via Unsafe call
                if (isLatin1(string)) {
                    // get Latin1 bytes directly from String via Unsafe call
                    final var bytes = (byte[]) UnsafeAccess.getObject(string, STRING_OFFSETS.bytesFieldOffset);

                    // 1) all bytes are Ascii
                    if (!AsciiOps.hasNegatives(bytes)) {
                        return new Result().withAscii(bytes);
                    }

                    // 2) non ASCII ISO_8859_1 bytes, ISO_8859_1 compatible target charset
                    if (charset.contains(StandardCharsets.ISO_8859_1)) {
                        return new Result().withNotAsciiLatin1(bytes);
                    }

                    // 3) UTF-8 is ASCII compatible but is NOT ISO_8859_1 compatible,
                    // must encode this non Ascii Latin1 bytes to UTF-8
                    if (charset.contains(StandardCharsets.UTF_8)) {
                        return new Result().withNotAsciiNotLatin1Utf8(Latin1Ops.encodeUTF8(bytes));
                    }

                    // 4) Required charset is ASCII compatible but is NOT ISO_8859_1 nor UTF-8 compatible,
                    // must encode this non Ascii String to target charset
                    return new Result().withNotAsciiNotLatin1(string.getBytes(charset), charset);
                }
            }
            // classic encode for non Ascii compatible charset
            return new Result().withNotAsciiNotLatin1(string.getBytes(charset), charset);
        }

        /**
         * This is a unsafe Latin1 String builder
         */
        @Override
        final String toLatin1String(byte[] bytes) {
            // create String instance via allocateInstance (do not call any constructor) -> this is risky !
            final String string;
            try {
                string = UnsafeAccess.allocateInstance(String.class);
            } catch (Throwable t) {
                // should never happen
                return new String(bytes, StandardCharsets.ISO_8859_1);
            }

            // init fields
            UnsafeAccess.putObject(string, STRING_OFFSETS.bytesFieldOffset, bytes);
            UnsafeAccess.putByte(string, STRING_OFFSETS.coderFieldOffset, LATIN1);
            UnsafeAccess.putInt(string, STRING_OFFSETS.hashFieldOffset, 0);

            return string;
        }

        /**
         * This is a unsafe UTF-16 String builder
         */
        private static String toUtf16String(byte[] bytes) {
            // create String instance via allocateInstance (do not call any constructor) -> this is risky !
            final String string;
            try {
                string = UnsafeAccess.allocateInstance(String.class);
            } catch (Throwable t) {
                // should never happen
                return new String(bytes, StandardCharsets.UTF_16);
            }

            // init fields
            UnsafeAccess.putObject(string, STRING_OFFSETS.bytesFieldOffset, bytes);
            UnsafeAccess.putByte(string, STRING_OFFSETS.coderFieldOffset, UTF16);
            UnsafeAccess.putInt(string, STRING_OFFSETS.hashFieldOffset, 0);

            return string;
        }
    }

    private static final class SafeOps extends Ops {

        @Override
        final Result encode(String string, Charset charset) {
            // ASCII compatible target charset require special path
            if (charset.contains(StandardCharsets.US_ASCII)) {
                final var bytes = string.getBytes(charset);

                // 1) ASCII bytes
                if (!AsciiOps.hasNegatives(bytes)) {
                    return new Result().withAscii(bytes);
                }

                // 2) non ASCII bytes, target charset is ISO_8859_1
                if (charset == StandardCharsets.ISO_8859_1) {
                    return new Result().withNotAsciiLatin1(bytes);
                }

                // 3) Charset is ASCII compatible but is not ISO_8859_1
                return new Result().withNotAsciiNotLatin1(bytes, charset);
            }
            // classic encode for non Ascii compatible charset
            return new Result().withNotAsciiNotLatin1(string.getBytes(charset), charset);
        }

        @Override
        final String toLatin1String(byte[] bytes) {
            return new String(bytes, StandardCharsets.ISO_8859_1);
        }
    }

    public static class Result {

        private ByteBufferOffHeap bbMemory;
        private boolean isAscii;
        private boolean isLatin1;
        private Charset charset;

        Result withAscii(byte[] bytes) {
            this.bbMemory = OffHeapFactory.of(bytes);
            this.isAscii = true;
            this.isLatin1 = true;
            this.charset = StandardCharsets.US_ASCII;
            return this;
        }

        Result withNotAsciiLatin1(byte[] bytes) {
            this.bbMemory = OffHeapFactory.of(bytes);
            this.isAscii = false;
            this.isLatin1 = true;
            this.charset = StandardCharsets.ISO_8859_1;
            return this;
        }

        public Result withNotAsciiNotLatin1Utf8(ByteBufferOffHeap bbMemory) {
            this.bbMemory = bbMemory;
            this.isAscii = false;
            this.isLatin1 = false;
            this.charset = StandardCharsets.UTF_8;
            return this;
        }

        Result withNotAsciiNotLatin1(byte[] bytes, Charset charset) {
            this.bbMemory = OffHeapFactory.of(bytes);
            this.isAscii = false;
            this.isLatin1 = false;
            this.charset = charset;
            return this;
        }

        public ByteBufferOffHeap getBbMemory() {
            return bbMemory;
        }

        public boolean isAscii() {
            return isAscii;
        }

        public boolean isLatin1() {
            return isLatin1;
        }

        public Charset getCharset() {
            return charset;
        }
    }
}
