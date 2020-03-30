/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package io.limo.various.string;

import org.apache.hadoop.io.Text;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import rpc.turbo.util.UnsafeStringUtils;

import static org.assertj.core.api.Assertions.assertThat;

public class StringCharsetTests {

    /**
     * ASCII Format
     * Each character is stored with a sing byte (using 7 bits)
     */
    private final static String ASCII = "1234567890abcdefghijklmnopqrstuvwxyz";

    /**
     * ISO Latin Alphabet {@literal No. 1}, also known as ISO-LATIN-1.
     * Each character is stored with a sing byte
     * <p>This String contains a non ASCII char
     */
    private final static String  ISO_8859_1 = "1234567890abcdefghijklmnopqrstuvwxyz¡";

    /**
     * Sixteen-bit UCS Transformation Format, byte order identified by an
     * optional byte-order mark.
     * Each character is stored with 2 bytes
     */
    private final static String UTF_16 = "€";

    @Test
    @DisplayName("check that a ISO_8859_1 String isLatin true using Compact String String#isLatin1 since JDK9")
    void isISO_8859_1() {
        assertThat(UnsafeStringUtils.isLatin1(ISO_8859_1))
                .isTrue();
    }

    @Test
    @DisplayName("check that a not ISO_8859_1 String isLatin false using Compact String String#isLatin1 since JDK9")
    void isNotISO_8859_1() {
        assertThat(UnsafeStringUtils.isLatin1(UTF_16))
                .isFalse();
    }

    @Test
    @DisplayName("check that a ASCII String has same length in UTF-8 than number of characters")
    void utf8_ascii_text() {
        Text utf8 = new Text(ASCII);
        assertThat(utf8.getLength())
                .isEqualTo(36);
    }

    @Test
    @DisplayName("check that a ISO_8859_1 non ASCII String has not same length in UTF-8 than number of characters")
    void utf8_non_ascii_text() {
        Text utf8 = new Text(ISO_8859_1);
        assertThat(utf8.getLength())
                .isEqualTo(38);
    }
}
