/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
    Authors: iText Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
/*

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.kernel.font;

import com.itextpdf.io.font.FontMetrics;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfOutputStream;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.regex.Pattern;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class PdfFontUnitTest extends ExtendedITextTest {
    public static final int FONT_METRICS_DESCENT = -40;
    public static final int FONT_METRICS_ASCENT = 700;
    public static final int FONT_SIZE = 50;

    public static class TestFont extends PdfFont {

        public static final int SIMPLE_GLYPH = 97;
        public static final int SIMPLE_GLYPH_WITHOUT_BBOX = 98;
        public static final int SIMPLE_GLYPH_WITH_POSITIVE_DESCENT = 99;
        public static final int COMPLEX_GLYPH = 119070;
        public static final int ZERO_CODE_GLYPH = 0;

        // these are two parts of G-clef glyph
        public static final char[] COMPLEX_GLYPH_AS_CHARS = new char[]{'\ud834', '\udd1e'};

        public static final int SIMPLE_GLYPH_WIDTH = 100;
        public static final int COMPLEX_GLYPH_WIDTH = 200;

        public TestFont() {
            super();
        }

        public TestFont(PdfDictionary dictionary) {
            super(dictionary);
        }

        public void setFontProgram(FontProgram fontProgram) {
            this.fontProgram = fontProgram;
        }

        @Override
        public Glyph getGlyph(int unicode) {
            if (unicode == SIMPLE_GLYPH) {
                return new Glyph(1, SIMPLE_GLYPH_WIDTH, SIMPLE_GLYPH, new int[]{10, -20, 200, 600});
            } else if (unicode == SIMPLE_GLYPH_WITHOUT_BBOX) {
                return new Glyph(2, SIMPLE_GLYPH_WIDTH, SIMPLE_GLYPH);
            } else if (unicode == SIMPLE_GLYPH_WITH_POSITIVE_DESCENT) {
                return new Glyph(3, SIMPLE_GLYPH_WIDTH, SIMPLE_GLYPH, new int[]{10, 10, 200, 600});
            } else if (unicode == COMPLEX_GLYPH) {
                return new Glyph(4, COMPLEX_GLYPH_WIDTH, COMPLEX_GLYPH, new int[]{20, -100, 400, 800});
            } else if (unicode == ZERO_CODE_GLYPH) {
                return new Glyph(0, 0, 0);
            }
            return null;
        }

        @Override
        public GlyphLine createGlyphLine(String content) {
            return null;
        }

        @Override
        public int appendGlyphs(String text, int from, int to, List<Glyph> glyphs) {
            return 0;
        }

        @Override
        public int appendAnyGlyph(String text, int from, List<Glyph> glyphs) {
            return 0;
        }

        @Override
        public byte[] convertToBytes(String text) {
            return new byte[0];
        }

        @Override
        public byte[] convertToBytes(GlyphLine glyphLine) {
            return new byte[0];
        }

        @Override
        public String decode(PdfString content) {
            return null;
        }

        @Override
        public GlyphLine decodeIntoGlyphLine(PdfString content) {
            return null;
        }

        @Override
        public float getContentWidth(PdfString content) {
            return 0;
        }

        @Override
        public byte[] convertToBytes(Glyph glyph) {
            return new byte[0];
        }

        @Override
        public void writeText(GlyphLine text, int from, int to, PdfOutputStream stream) {

        }

        @Override
        public void writeText(String text, PdfOutputStream stream) {

        }

        @Override
        protected PdfDictionary getFontDescriptor(String fontName) {
            return null;
        }
    }

    public static class TestFontProgram extends FontProgram {

        @Override
        public int getPdfFontFlags() {
            return 0;
        }

        @Override
        public int getKerning(Glyph first, Glyph second) {
            return 0;
        }

        @Override
        public FontMetrics getFontMetrics() {
            return new TestFontMetrics();
        }

        @Override
        public boolean isFontSpecific() {
            return true;
        }
    }

    public static class TestFontMetrics extends FontMetrics {
        public TestFontMetrics() {
            setTypoDescender(FONT_METRICS_DESCENT);
            setTypoAscender(FONT_METRICS_ASCENT);
        }
    }

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void constructorWithoutParamsTest() {
        TestFont font = new TestFont();

        Assert.assertEquals(PdfName.Font, font.getPdfObject().get(PdfName.Type));
    }

    @Test
    public void constructorWithDictionaryTest() {
        PdfDictionary dictionary = new PdfDictionary();
        dictionary.put(PdfName.A, PdfName.B);

        TestFont font = new TestFont(dictionary);

        Assert.assertEquals(PdfName.Font, font.getPdfObject().get(PdfName.Type));
        Assert.assertEquals(PdfName.B, font.getPdfObject().get(PdfName.A));
    }

    @Test
    public void containsGlyphTest() {
        TestFont font = new TestFont();

        Assert.assertTrue(font.containsGlyph(TestFont.SIMPLE_GLYPH));
        Assert.assertFalse(font.containsGlyph(111));
    }

    @Test
    public void zeroGlyphIsAllowedOnlyIfFontIsSymbolicTest() {
        TestFont font = new TestFont();

        Assert.assertFalse(font.containsGlyph(TestFont.ZERO_CODE_GLYPH));

        font.setFontProgram(new TestFontProgram());
        Assert.assertTrue(font.containsGlyph(TestFont.ZERO_CODE_GLYPH));
    }

    @Test
    public void getFontMatrixTest() {
        TestFont font = new TestFont();

        Assert.assertArrayEquals(PdfFont.DEFAULT_FONT_MATRIX, font.getFontMatrix(), 0.0001);
    }

    @Test
    public void getWidthUnicodeTest() {
        TestFont font = new TestFont();

        Assert.assertEquals(TestFont.SIMPLE_GLYPH_WIDTH, font.getWidth(TestFont.SIMPLE_GLYPH));
        Assert.assertEquals(0, font.getWidth(111));
    }

    @Test
    public void getWidthFontSizeTest() {
        TestFont font = new TestFont();
        double expectedValue = TestFont.SIMPLE_GLYPH_WIDTH * FONT_SIZE / (double) FontProgram.UNITS_NORMALIZATION;
        Assert.assertEquals(expectedValue, font.getWidth(TestFont.SIMPLE_GLYPH, FONT_SIZE), 0.1);
        Assert.assertEquals(0, font.getWidth(111));
    }

    @Test
    public void getWidthOfStringTest() {
        TestFont font = new TestFont();
        char[] text = getSentence(3);
        String textAsString = new String(text);
        Assert.assertEquals(3 * TestFont.SIMPLE_GLYPH_WIDTH, font.getWidth(textAsString));
    }

    @Test
    public void getWidthOfSurrogatePairTest() {
        TestFont font = new TestFont();
        char[] text = new char[] {
                TestFont.COMPLEX_GLYPH_AS_CHARS[0],
                TestFont.COMPLEX_GLYPH_AS_CHARS[1],
                (char) TestFont.SIMPLE_GLYPH,
        };
        String textAsString = new String(text);
        Assert.assertEquals(TestFont.COMPLEX_GLYPH_WIDTH + TestFont.SIMPLE_GLYPH_WIDTH,
                font.getWidth(textAsString));
    }

    @Test
    public void getWidthOfUnknownGlyphsTest() {
        TestFont font = new TestFont();
        char[] text = new char[] {
                (char) 111,
                (char) 222,
                (char) 333,
        };
        String textAsString = new String(text);
        Assert.assertEquals(0, font.getWidth(textAsString));
    }

    @Test
    public void getWidthOfStringWithFontSizeTest() {
        TestFont font = new TestFont();
        char[] text = getSentence(3);
        String textAsString = new String(text);
        double expectedValue = 3 * TestFont.SIMPLE_GLYPH_WIDTH * FONT_SIZE / (double) FontProgram.UNITS_NORMALIZATION;
        Assert.assertEquals(expectedValue, font.getWidth(textAsString, FONT_SIZE), 0.1);
    }

    @Test
    public void getDescentOfGlyphTest() {
        TestFont font = new TestFont();

        int expectedDescent = font.getGlyph(TestFont.SIMPLE_GLYPH).getBbox()[1];
        int expectedValue = (int) (expectedDescent * FONT_SIZE / (double) FontProgram.UNITS_NORMALIZATION);
        Assert.assertEquals(expectedValue, font.getDescent(TestFont.SIMPLE_GLYPH, FONT_SIZE));
    }

    @Test
    public void descentCannotBePositiveTest() {
        TestFont font = new TestFont();

        Assert.assertEquals(0, font.getDescent(TestFont.SIMPLE_GLYPH_WITH_POSITIVE_DESCENT, 50));
    }

    @Test
    public void getDescentOfUnknownGlyphTest() {
        TestFont font = new TestFont();

        Assert.assertEquals(0, font.getDescent(111, 50));
    }

    @Test
    public void getDescentOfGlyphWithoutBBoxTest() {
        TestFont font = new TestFont();
        font.setFontProgram(new TestFontProgram());

        int expectedValue = (int) (FONT_METRICS_DESCENT * FONT_SIZE / (double) FontProgram.UNITS_NORMALIZATION);
        Assert.assertEquals(expectedValue, font.getDescent(TestFont.SIMPLE_GLYPH_WITHOUT_BBOX, FONT_SIZE));
    }

    @Test
    public void getDescentOfTextTest() {
        TestFont font = new TestFont();
        char[] text = new char[] {
                (char) TestFont.SIMPLE_GLYPH,
                TestFont.COMPLEX_GLYPH_AS_CHARS[0],
                TestFont.COMPLEX_GLYPH_AS_CHARS[1],
        };
        String textAsString = new String(text);
        int expectedMinDescent = Math.min(font.getGlyph(TestFont.SIMPLE_GLYPH).getBbox()[1],
                font.getGlyph(TestFont.COMPLEX_GLYPH).getBbox()[1]);
        int expectedValue = (int) (expectedMinDescent * FONT_SIZE / (double) FontProgram.UNITS_NORMALIZATION);
        Assert.assertEquals(expectedValue, font.getDescent(textAsString, FONT_SIZE));
    }

    @Test
    public void getDescentOfTextWithGlyphWithoutBBoxTest() {
        TestFont font = new TestFont();
        font.setFontProgram(new TestFontProgram());
        char[] text = new char[] {
                (char) TestFont.SIMPLE_GLYPH,
                (char) TestFont.SIMPLE_GLYPH_WITHOUT_BBOX
        };
        String textAsString = new String(text);
        int expectedMinDescent = Math.min(font.getGlyph(TestFont.SIMPLE_GLYPH).getBbox()[1],
                FONT_METRICS_DESCENT);
        int expectedValue = (int) (expectedMinDescent * FONT_SIZE / (double) FontProgram.UNITS_NORMALIZATION);
        Assert.assertEquals(expectedValue, font.getDescent(textAsString, FONT_SIZE));
    }

    @Test
    public void getAscentOfGlyphTest() {
        TestFont font = new TestFont();

        int expectedAscent = font.getGlyph(TestFont.SIMPLE_GLYPH).getBbox()[3];
        int expectedValue = (int) (expectedAscent * FONT_SIZE / (double) FontProgram.UNITS_NORMALIZATION);
        Assert.assertEquals(expectedValue, font.getAscent(TestFont.SIMPLE_GLYPH, FONT_SIZE));
    }

    @Test
    public void getAscentOfGlyphWithoutBBoxTest() {
        TestFont font = new TestFont();
        font.setFontProgram(new TestFontProgram());

        int expectedValue = (int) (FONT_METRICS_ASCENT * FONT_SIZE / (double) FontProgram.UNITS_NORMALIZATION);
        Assert.assertEquals(expectedValue, font.getAscent(TestFont.SIMPLE_GLYPH_WITHOUT_BBOX, FONT_SIZE));
    }

    @Test
    public void getAscentOfTextTest() {
        TestFont font = new TestFont();
        char[] text = new char[] {
                (char) TestFont.SIMPLE_GLYPH,
                TestFont.COMPLEX_GLYPH_AS_CHARS[0],
                TestFont.COMPLEX_GLYPH_AS_CHARS[1],
        };
        String textAsString = new String(text);
        int expectedMaxAscent = Math.max(
                font.getGlyph(TestFont.SIMPLE_GLYPH).getBbox()[3],
                font.getGlyph(TestFont.COMPLEX_GLYPH).getBbox()[3]);
        int expectedValue = (int) (expectedMaxAscent * FONT_SIZE / (double) FontProgram.UNITS_NORMALIZATION);
        Assert.assertEquals(expectedValue, font.getAscent(textAsString, FONT_SIZE));
    }

    @Test
    public void getAscentOfTextWithGlyphWithoutBBoxTest() {
        TestFont font = new TestFont();
        font.setFontProgram(new TestFontProgram());
        char[] text = new char[] {
                (char) TestFont.SIMPLE_GLYPH,
                (char) TestFont.SIMPLE_GLYPH_WITHOUT_BBOX
        };
        String textAsString = new String(text);
        int expectedMaxAscent = Math.max(
                font.getGlyph(TestFont.SIMPLE_GLYPH).getBbox()[3],
                FONT_METRICS_ASCENT);
        int expectedValue = (int) (expectedMaxAscent * FONT_SIZE / (double) FontProgram.UNITS_NORMALIZATION);
        Assert.assertEquals(expectedValue, font.getAscent(textAsString, FONT_SIZE));
    }

    @Test
    public void isEmbeddedTest() {
        TestFont font = new TestFont();
        Assert.assertFalse(font.isEmbedded());

        font.embedded = true;
        Assert.assertTrue(font.isEmbedded());
    }

    @Test
    public void isSubsetTest() {
        TestFont font = new TestFont();
        Assert.assertTrue(font.isSubset());

        font.setSubset(false);
        Assert.assertFalse(font.isSubset());
    }

    @Test
    public void addSubsetRangeTest() {
        TestFont font = new TestFont();
        font.setSubset(false);

        final int[] range1 = {1, 2};
        final int[] range2 = {10, 20};

        font.addSubsetRange(range1);
        font.addSubsetRange(range2);

        Assert.assertTrue(font.isSubset());
        Assert.assertEquals(2, font.subsetRanges.size());
        Assert.assertArrayEquals(range1, font.subsetRanges.get(0));
        Assert.assertArrayEquals(range2, font.subsetRanges.get(1));
    }

    @Test
    public void splitSentenceFitMaxWidthTest() {
        TestFont font = new TestFont();
        char[] words = getSentence(3, 3);
        String wordsAsString = new String(words);
        double width = 6 * font.getWidth(TestFont.SIMPLE_GLYPH, FONT_SIZE);
        List<String> result = font.splitString(wordsAsString, FONT_SIZE, (float) width + 0.01f);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(wordsAsString, result.get(0));
    }

    @Test
    public void splitSentenceWordFitMaxWidthTest() {
        TestFont font = new TestFont();
        char[] words = getSentence(3, 4, 2);
        String wordsAsString = new String(words);
        double width = 4 * font.getWidth(TestFont.SIMPLE_GLYPH, FONT_SIZE);
        List<String> result = font.splitString(wordsAsString, FONT_SIZE, (float) width + 0.01f);
        Assert.assertEquals(3, result.size());
        Assert.assertEquals(new String(getSentence(3)), result.get(0));
        Assert.assertEquals(new String(getSentence(4)), result.get(1));
        Assert.assertEquals(new String(getSentence(2)), result.get(2));
    }

    @Test
    public void splitSentenceWordDoesNotFitMaxWidthCase_PartIsCombinedWithTheFollowingWordTest() {
        TestFont font = new TestFont();
        char[] words = getSentence(3, 4, 2);
        String wordsAsString = new String(words);
        double width = 3 * font.getWidth(TestFont.SIMPLE_GLYPH, FONT_SIZE);
        List<String> result = font.splitString(wordsAsString, FONT_SIZE, (float) width + 0.01f);
        Assert.assertEquals(3, result.size());
        Assert.assertEquals(new String(getSentence(3)), result.get(0));
        Assert.assertEquals(new String(getSentence(3)), result.get(1));
        Assert.assertEquals(new String(getSentence(1, 2)), result.get(2));
    }

    @Test
    public void splitSentenceWordDoesNotFitMaxWidthCase_PartIsOnTheSeparateLineTest() {
        TestFont font = new TestFont();
        char[] words = getSentence(2, 4, 3);
        String wordsAsString = new String(words);
        double width = 3 * font.getWidth(TestFont.SIMPLE_GLYPH, FONT_SIZE);
        List<String> result = font.splitString(wordsAsString, FONT_SIZE, (float) width + 0.01f);
        Assert.assertEquals(4, result.size());
        Assert.assertEquals(new String(getSentence(2)), result.get(0));
        Assert.assertEquals(new String(getSentence(3)), result.get(1));
        Assert.assertEquals(new String(getSentence(1)), result.get(2));
        Assert.assertEquals(new String(getSentence(3)), result.get(3));
    }

    @Test
    public void splitSentenceSymbolDoesNotFitLineTest() {
        TestFont font = new TestFont();
        char[] words = getSentence(3);
        String wordsAsString = new String(words);
        double width = font.getWidth(TestFont.SIMPLE_GLYPH, FONT_SIZE) / 2.;
        List<String> result = font.splitString(wordsAsString, FONT_SIZE, (float) width + 0.01f);
        Assert.assertEquals(4, result.size());
        Assert.assertEquals(new String(getSentence(1)), result.get(0));
        Assert.assertEquals(new String(getSentence(1)), result.get(1));
        Assert.assertEquals(new String(getSentence(1)), result.get(2));
        Assert.assertEquals(new String(getSentence(0)), result.get(3));
    }


    @Test
    public void splitSentenceWithLineBreakTest() {
        TestFont font = new TestFont();
        char[] words = new char[] {
                (char) TestFont.SIMPLE_GLYPH,
                '\n',
                (char) TestFont.SIMPLE_GLYPH
        };
        String wordsAsString = new String(words);
        double width = 10 * font.getWidth(TestFont.SIMPLE_GLYPH, FONT_SIZE);
        List<String> result = font.splitString(wordsAsString, FONT_SIZE, (float) width + 0.01f);
        Assert.assertEquals(2, result.size());
        Assert.assertEquals(new String(getSentence(1)), result.get(0));
        Assert.assertEquals(new String(getSentence(1)), result.get(1));
    }

    @Test
    public void isBuiltWithTest() {
        TestFont font = new TestFont();
        Assert.assertFalse(font.isBuiltWith("Any String Here", "Any Encoding"));
    }

    @Test
    public void isWrappedObjectMustBeIndirectTest() {
        TestFont font = new TestFont();
        Assert.assertTrue(font.isWrappedObjectMustBeIndirect());
    }

    @Test
    public void updateEmbeddedSubsetPrefixTest() {
        final String fontName = "FontTest";
        String embeddedSubsetFontName = TestFont.updateSubsetPrefix(fontName, true, true);
        String onlySubsetFontName = TestFont.updateSubsetPrefix(fontName, true, false);
        String onlyEmbeddedFontName = TestFont.updateSubsetPrefix(fontName, false, true);
        String justFontName = TestFont.updateSubsetPrefix(fontName, false, false);

        Assert.assertEquals(fontName, onlySubsetFontName);
        Assert.assertEquals(fontName, onlyEmbeddedFontName);
        Assert.assertEquals(fontName, justFontName);

        Pattern prefixPattern = Pattern.compile("^[A-Z]{6}\\+FontTest$");
        Assert.assertTrue(prefixPattern.matcher(embeddedSubsetFontName).matches());
    }

    @Test
    public void getEmptyPdfStreamTest() {
        TestFont font = new TestFont();

        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(PdfException.FontEmbeddingIssue);
        font.getPdfFontStream(null, null);
    }

    @Test
    public void getPdfStreamTest() {
        TestFont font = new TestFont();
        byte[] data = new byte[10];
        for (int i = 0; i < 10; i++) {
            data[i] = (byte) i;
        }
        int[] fontStreamLength = new int[] {10, 20, 30};
        PdfStream stream = font.getPdfFontStream(data, fontStreamLength);
        Assert.assertArrayEquals(data, stream.getBytes());
        Assert.assertEquals(10, stream.getAsNumber(new PdfName("Length1")).intValue());
        Assert.assertEquals(20, stream.getAsNumber(new PdfName("Length2")).intValue());
        Assert.assertEquals(30, stream.getAsNumber(new PdfName("Length3")).intValue());
    }

    @Test
    public void getFontProgramTest() {
        TestFont font = new TestFont();
        TestFontProgram program = new TestFontProgram();

        Assert.assertNull(font.getFontProgram());
        font.setFontProgram(program);
        Assert.assertEquals(program, font.getFontProgram());
    }

    @Test
    public void toStringTest() {
        TestFont font = new TestFont();
        Assert.assertEquals("PdfFont{fontProgram=" + font.fontProgram + "}", font.toString());
    }

    @Test
    public void makeObjectIndirectWhileFontIsIndirectTest() {
        try (PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            // to avoid an exception
            document.addNewPage();

            TestFont font = new TestFont();
            font.getPdfObject().makeIndirect(document);

            PdfDictionary dictionary = new PdfDictionary();
            Assert.assertTrue(font.makeObjectIndirect(dictionary));
            Assert.assertNotNull(dictionary.getIndirectReference());
            Assert.assertEquals(document, dictionary.getIndirectReference().getDocument());
        }
    }

    @Test
    public void makeObjectIndirectWhileFontIsDirectTest() {
        TestFont font = new TestFont();

        PdfDictionary dictionary = new PdfDictionary();
        Assert.assertFalse(font.makeObjectIndirect(dictionary));
        Assert.assertNull(dictionary.getIndirectReference());
    }

    private char[] getSentence(int... lengthsOfWords) {
        int length = 0;
        for (int lengthOfWord : lengthsOfWords) {
            length += lengthOfWord;
        }

        int numberOfSpaces = lengthsOfWords.length - 1;
        length += numberOfSpaces;

        char[] sentence = new char[length];
        int index = 0;
        for (int lengthOfWord : lengthsOfWords) {
            for (int i = 0; i < lengthOfWord; i++) {
                sentence[index] = (char) TestFont.SIMPLE_GLYPH;
                index++;
            }
            if (index < length) {
                sentence[index] = ' ';
                index++;
            }
        }
        return sentence;
    }
}
