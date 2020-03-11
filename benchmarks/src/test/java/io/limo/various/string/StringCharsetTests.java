/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.various.string;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import rpc.turbo.util.UnsafeStringUtils;

import static org.assertj.core.api.Assertions.assertThat;

public class StringCharsetTests {

    /**
     * ISO Latin Alphabet {@literal No. 1}, also known as ISO-LATIN-1.
     * Each character is stored with a sing byte
     */
    private final static String ISO_8859_1 = "1234567890azertyuiopqsdfghjklmwxcvbn";
    /**
     * Sixteen-bit UCS Transformation Format, byte order identified by an
     * optional byte-order mark.
     * Each character is stored with 2 bytes
     */
    private final static String UTF_16 = "€";

    @Test
    @DisplayName("check that a String is ISO_8859_1 using Compact String String#isLatin1 since JDK9")
    void isISO_8859_1() {
        assertThat(UnsafeStringUtils.isLatin1(ISO_8859_1))
                .isTrue();
    }

    @Test
    @DisplayName("check that a String is not ISO_8859_1 using Compact String String#isLatin1 since JDK9")
    void isNotISO_8859_1() {
        assertThat(UnsafeStringUtils.isLatin1(UTF_16))
                .isFalse();
    }
}
