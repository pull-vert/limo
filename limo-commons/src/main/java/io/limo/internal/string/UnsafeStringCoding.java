/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.internal.string;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Utility class for string encoding and decoding, using String methods via unsafe calls
 */
public final class UnsafeStringCoding {

    private static final byte LATIN1 = 0;

    private static final byte UTF16 = 1;

    private static final long coderFieldOffset;

    private static final long bytesFieldOffset;

    private static final long hashFieldOffset;

    static {
        long _coderFieldOffset;

        try {
            final var coderField = String.class.getDeclaredField("coder");
            _coderFieldOffset = UnsafeAccess.unsafe().objectFieldOffset(coderField);

            if (UnsafeAccess.unsafe().getByte("€", _coderFieldOffset) != UTF16) {
                _coderFieldOffset = -1;
            }
        } catch (Throwable e) {
            _coderFieldOffset = -1;
        }

        coderFieldOffset = _coderFieldOffset;

        if (coderFieldOffset == -1) {
            System.err.println("UnsafeStringCoding.isLatin1(String) is broken");
        }

        long _bytesFieldOffset;

        try {
            final var valueField = String.class.getDeclaredField("value");
            _bytesFieldOffset = UnsafeAccess.unsafe().objectFieldOffset(valueField);

            if (UnsafeAccess.unsafe().getObject("€", _bytesFieldOffset) == null) {
                _bytesFieldOffset = -1;
            }

            if (!(UnsafeAccess.unsafe().getObject("€", _bytesFieldOffset) instanceof byte[])) {
                _bytesFieldOffset = -1;
            }
        } catch (Throwable e) {
            _bytesFieldOffset = -1;
        }

        bytesFieldOffset = _bytesFieldOffset;

        if (bytesFieldOffset == -1) {
            System.err.println("UnsafeStringCoding.getUTF8Bytes(String) is broken");
        }

        long _hashFieldOffset;

        try {
            final var hashField = String.class.getDeclaredField("hash");
            _hashFieldOffset = UnsafeAccess.unsafe().objectFieldOffset(hashField);
        } catch (Throwable e) {
            _hashFieldOffset = -1;
        }

        hashFieldOffset = _hashFieldOffset;

        if (hashFieldOffset == -1) {
            System.err.println("UnsafeStringCoding.toLatin1String(byte[]) is broken");
        }
    }

    // uninstanciable
    private UnsafeStringCoding() {
    }

    static Boolean isLatin1(String str) {
        if (coderFieldOffset > 0) {
            return UnsafeAccess.unsafe().getByte(str, coderFieldOffset) == LATIN1;
        }

        return null;
    }

    /**
     * Never change the returned bytes obtained via Unsafe call in Result
     * <p>(would break String parameter's immutability) !
     */
    static Result encodeUnsafe(String string, Charset charset) {
        final var isAsciiCompatible = charset.contains(StandardCharsets.US_ASCII);
        // ASCII compatible target charset require special path
        if (isAsciiCompatible) {
            // Check for Latin1 (ISO_8859_1) String via Unsafe call
            final var isLatin1 = isLatin1(string);

            if (isLatin1 != null && isLatin1) {
                byte[] bytes = null;
                // fast-path : get Latin1 bytes directly from String via Unsafe call
                if (bytesFieldOffset > 0) {
                    bytes = (byte[]) UnsafeAccess.unsafe().getObject(string, bytesFieldOffset);
                }

                if (bytes != null) {
                    // 1) ASCII bytes
                    if (!hasNegatives(bytes)) {
                        return new Result().withAscii(bytes);
                    }

                    // 2) non ASCII ISO_8859_1 bytes, ISO_8859_1 compatible target charset
                    if (charset.contains(StandardCharsets.ISO_8859_1)) {
                        return new Result().withNotAsciiLatin1(bytes);
                    }

                    // 3) Charset is ASCII compatible but not ISO_8859_1 compatible,
                    // must encode this non Ascii String to target charset
                    return new Result().withNotAscii(string.getBytes(charset), charset);
                }

                // Unsafe access failed, encode safely this Latin1 String
                return encodeSafeLatin1AsciiCompatible(string, charset);
            }

            // Unsafe access failed, encode safely
            if (isLatin1 == null) {
                return encodeSafeAsciiCompatible(string, charset);
            }
        }

        // classic encode for non Ascii compatible charset
        return new Result().withString(string, charset);
    }

    /**
     * @param string a Latin1 String
     * @param charset target charset
     * @return the Result
     */
    private static Result encodeSafeLatin1AsciiCompatible(String string, Charset charset) {
        final var isLatin1Compatible = charset.contains(StandardCharsets.ISO_8859_1);
        final byte[] bytes;
        // we know this String is stored as Latin1
        if (isLatin1Compatible) {
            bytes = string.getBytes(StandardCharsets.ISO_8859_1);
        } else {
            bytes = string.getBytes(charset);
        }

        // 1) ASCII bytes
        if (!hasNegatives(bytes)) {
            return new Result().withAscii(bytes);
        }

        // 2) non ASCII ISO_8859_1 bytes, ISO_8859_1 compatible target charset
        if (isLatin1Compatible) {
            return new Result().withNotAsciiLatin1(bytes);
        }

        // 3) Charset is ASCII compatible but not ISO_8859_1 compatible
        return new Result().withNotAscii(bytes, charset);
    }

    private static Result encodeSafeAsciiCompatible(String string, Charset charset) {
        final var bytes = string.getBytes(charset);

        // 1) ASCII bytes
        if (!hasNegatives(bytes)) {
            return new Result().withAscii(bytes);
        }

        // 2) non ASCII bytes, target charset is ISO_8859_1
        if (charset == StandardCharsets.ISO_8859_1) {
            return new Result().withNotAsciiLatin1(bytes);
        }

        // 3) Charset is ASCII compatible but is not ISO_8859_1
        return new Result().withNotAscii(bytes, charset);
    }

    public static boolean hasNegatives(byte[] bytes) {
        for (final var aByte : bytes) {
            if (aByte < 0) {
                return true;
            }
        }
        return false;
    }

    static class Result {

        byte[] value;

        boolean isAscii;

        boolean isLatin1;

        Charset charset;

        Result withAscii(byte[] value) {
            this.value = value;
            this.isAscii = true;
            this.isLatin1 = true;
            this.charset = StandardCharsets.US_ASCII;
            return this;
        }

        Result withNotAsciiLatin1(byte[] value) {
            this.value = value;
            this.isAscii = false;
            this.isLatin1 = true;
            this.charset = StandardCharsets.ISO_8859_1;
            return this;
        }

        Result withNotAscii(byte[] value, Charset charset) {
            this.value = value;
            this.isAscii = false;
            this.charset = charset;
            return this;
        }

        Result withString(String string, Charset charset) {
            this.value = string.getBytes(charset);
            this.charset = charset;
            return this;
        }
    }

    /**
     * This is a very unsafe Latin1 String builder
     */
    static String toLatin1String(byte[] bytes) {
        if (bytesFieldOffset == -1 || coderFieldOffset == -1 || hashFieldOffset == -1) {
            return new String(bytes, StandardCharsets.ISO_8859_1);
        }

        // create String instance
        final Object obj;
        try {
            obj = UnsafeAccess.unsafe().allocateInstance(String.class);
        } catch (Throwable t) {
            return new String(bytes, StandardCharsets.ISO_8859_1);
        }

        // init fields
        UnsafeAccess.unsafe().putObject(obj, bytesFieldOffset, bytes);
        UnsafeAccess.unsafe().putByte(obj, coderFieldOffset, LATIN1);
        UnsafeAccess.unsafe().putInt(obj, hashFieldOffset, 0);

        return (String) obj;
    }
}
