package com.itextpdf.io.font.cmap;

import com.itextpdf.commons.exceptions.ITextException;
import com.itextpdf.io.source.ByteBuffer;
import com.itextpdf.io.util.TextUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class StandardCMapCharsetsTest extends ExtendedITextTest {

    private static final String TEST_STRING_WITH_DIFFERENT_UNICODES = "eр؊\u0E84\uA515뀏";
    private static final byte[] BYTES_REPRESENTATION_OF_TEST_STRING = new byte[] {
            0, 101, // Latin Small Letter E
            4, 64, // Cyrillic Small Letter Er
            6, 10, // Arabic-Indic Per Ten Thousand Sign
            14, (byte)0x84, // Lao Letter Kho Tam
            (byte)0xA5, 21, // Vai Syllable Ndee
            (byte)0xB0, 15, // Hangul Syllable Ggwigs
    };

    @Test
    public void ucs2EncodingStringTest() {
        CMapCharsetEncoder encoder = StandardCMapCharsets.getEncoder("UniGB-UCS2-H");
        // UCS-2 represents full BMP, so all symbols should be correctly processed
        ByteBuffer buffer = new ByteBuffer(BYTES_REPRESENTATION_OF_TEST_STRING.length);
        for (int cp : TextUtil.convertToUtf32(TEST_STRING_WITH_DIFFERENT_UNICODES)) {
            byte[] actual = encoder.encodeUnicodeCodePoint(cp);
            buffer.append(actual);
        }
        Assert.assertArrayEquals(BYTES_REPRESENTATION_OF_TEST_STRING, buffer.toByteArray());
    }
    @Test
    public void ucs2TryToEncodeSymbolNotFromBmpStringTest() {
        CMapCharsetEncoder encoder = StandardCMapCharsets.getEncoder("UniGB-UCS2-H");
        // Symbol outside BMP of Unicode, so in native Java UTF-16 it encoded by surrogate pair
        // It is U+10437 symbol (Deseret Small Letter Yee)
        String str = "\uD801\uDC37";
        int cp = TextUtil.convertToUtf32(str)[0];
        Exception e = Assert.assertThrows(ITextException.class, () ->
                encoder.encodeUnicodeCodePoint(cp));
        Assert.assertEquals("This encoder only accepts BMP codepoints", e.getMessage());
    }
    @Test
    public void ucs2EncodingCodePointTest() {
        CMapCharsetEncoder encoder = StandardCMapCharsets.getEncoder("UniGB-UCS2-H");
        // U+0E84 (Lao Letter Kho Tam) from BMP
        int codePoint = 3716;
        byte[] actual = encoder.encodeUnicodeCodePoint(codePoint);
        Assert.assertArrayEquals(new byte[] {14, (byte)0x84}, actual);
    }

    @Test
    public void utf16EncodingStringTest() {
        CMapCharsetEncoder encoder = StandardCMapCharsets.getEncoder("UniGB-UTF16-H");
        ByteBuffer buffer = new ByteBuffer(BYTES_REPRESENTATION_OF_TEST_STRING.length);
        for (int cp : TextUtil.convertToUtf32(TEST_STRING_WITH_DIFFERENT_UNICODES)) {
            byte[] actual = encoder.encodeUnicodeCodePoint(cp);
            buffer.append(actual);
        }
        Assert.assertArrayEquals(BYTES_REPRESENTATION_OF_TEST_STRING, buffer.toByteArray());
        // UTF-16 represents full BMP, so all symbols should be correctly processed
    }
    @Test
    public void utf16TryToEncodeSymbolNotFromBmpStringTest() {
        CMapCharsetEncoder encoder = StandardCMapCharsets.getEncoder("UniGB-UTF16-H");
        // Symbol outside BMP of Unicode, so in native Java UTF-16 it encoded by surrogate pair
        // It is U+10437 symbol (Deseret Small Letter Yee)
        String str = "\uD801\uDC37";
        byte[] actual = encoder.encodeUnicodeCodePoint(TextUtil.convertToUtf32(str)[0]);
        Assert.assertArrayEquals(new byte[] {(byte)0xD8, 1, (byte)0xDC, 55}, actual);
    }

    @Test
    public void ucs2TryToEncodeSymbolNotFromBmpCodePointTest() {
        CMapCharsetEncoder encoder = StandardCMapCharsets.getEncoder("UniGB-UCS2-H");
        // It is U+10437 symbol (Deseret Small Letter Yee) outside BMP
        int codePoint = 66615;
        Exception e = Assert.assertThrows(ITextException.class, () -> encoder.encodeUnicodeCodePoint(codePoint));
        Assert.assertEquals("This encoder only accepts BMP codepoints", e.getMessage());
    }

    @Test
    public void udf16EncodingCodePointTest() {
        CMapCharsetEncoder encoder = StandardCMapCharsets.getEncoder("UniGB-UTF16-H");
        // U+0E84 (Lao Letter Kho Tam) from BMP
        int codePoint = 3716;
        byte[] actual = encoder.encodeUnicodeCodePoint(codePoint);
        Assert.assertArrayEquals(new byte[] {14, (byte)0x84}, actual);
    }

    @Test
    public void udf16TryToEncodeSymbolNotFromBmpCodePointTest() {
        CMapCharsetEncoder encoder = StandardCMapCharsets.getEncoder("UniGB-UTF16-H");
        // It is U+10437 symbol (Deseret Small Letter Yee) outside BMP
        int codePoint = 66615;
        byte[] actual = encoder.encodeUnicodeCodePoint(codePoint);
        Assert.assertArrayEquals(new byte[] {(byte)0xD8, 1, (byte)0xDC, 55}, actual);
    }

    @Test
    public void charsetEncodersDisabledTest() {
        try {
            StandardCMapCharsets.disableCharsetEncoders();
            CMapCharsetEncoder encoder = StandardCMapCharsets.getEncoder("UniGB-UTF16-H");
            Assert.assertNull(encoder);
        } finally {
            StandardCMapCharsets.enableCharsetEncoders();
        }
    }

    @Test
    public void charsetEncodersReEnabledTest() {
        try {
            StandardCMapCharsets.disableCharsetEncoders();
            CMapCharsetEncoder encoder = StandardCMapCharsets.getEncoder("UniGB-UTF16-H");
            Assert.assertNull(encoder);
        } finally {
            StandardCMapCharsets.enableCharsetEncoders();
            CMapCharsetEncoder encoder = StandardCMapCharsets.getEncoder("UniGB-UTF16-H");
            Assert.assertNotNull(encoder);
        }
    }
}
