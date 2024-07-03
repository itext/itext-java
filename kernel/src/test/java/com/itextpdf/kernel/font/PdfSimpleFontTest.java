/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
    Authors: Apryse Software.

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

import com.itextpdf.io.font.FontEncoding;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfOutputStream;
import com.itextpdf.test.ExtendedITextTest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class PdfSimpleFontTest extends ExtendedITextTest {

    private static final byte T_CODE = 116;
    private static final byte E_CODE = 101;
    private static final byte E_CUSTOM_CODE = 103;
    private static final byte OPEN_BRACKET_CODE = 40;
    private static final byte CLOSE_BRACKET_CODE = 41;

    private static Glyph E_GLYPH_FONT_SPECIFIC;
    private static Glyph T_GLYPH_FONT_SPECIFIC;
    private static Glyph E_GLYPH_CUSTOM_MAPPED;

    @BeforeAll
    public static void init() {
        T_GLYPH_FONT_SPECIFIC = new Glyph(T_CODE, 278, 116, new int[]{14, -7, 257, 669});
        T_GLYPH_FONT_SPECIFIC.setChars(new char[]{'t'});

        E_GLYPH_FONT_SPECIFIC = new Glyph(E_CODE, 556, 101, new int[]{40, -15, 516, 538});
        E_GLYPH_FONT_SPECIFIC.setChars(new char[]{'e'});

        E_GLYPH_CUSTOM_MAPPED = new Glyph(E_CUSTOM_CODE, 44, 103);
        E_GLYPH_CUSTOM_MAPPED.setChars(new char[]{'e'});
    }

    @Test
    public void createGlyphLineWithSpecificEncodingTest() throws IOException {
        PdfSimpleFont<FontProgram> fontToTest = new TestSimpleFont(FontEncoding.createFontSpecificEncoding());
        GlyphLine glyphLine = fontToTest.createGlyphLine("te");

        List<Glyph> glyphs = new ArrayList<>();
        glyphs.add(T_GLYPH_FONT_SPECIFIC);
        glyphs.add(E_GLYPH_FONT_SPECIFIC);
        GlyphLine expected = new GlyphLine(glyphs, 0, 2);

        Assertions.assertEquals(expected, glyphLine);
    }

    @Test
    public void createGlyphLineWithEmptyEncodingTest() throws IOException {
        PdfSimpleFont<FontProgram> fontToTest = new TestSimpleFont(FontEncoding.createEmptyFontEncoding());
        GlyphLine glyphLine = fontToTest.createGlyphLine("te");

        List<Glyph> glyphs = new ArrayList<>();
        glyphs.add(E_GLYPH_CUSTOM_MAPPED);
        GlyphLine expected = new GlyphLine(glyphs, 0, 1);

        Assertions.assertEquals(expected, glyphLine);
    }

    @Test
    public void appendGlyphsWithSpecificEncodingTest() throws IOException {
        PdfSimpleFont<FontProgram> fontToTest = new TestSimpleFont(FontEncoding.createFontSpecificEncoding());
        List<Glyph> toAppend = new ArrayList<>();
        int processed = fontToTest.appendGlyphs("te", 0, 1, toAppend);
        Assertions.assertEquals(2, processed);

        List<Glyph> glyphs = new ArrayList<>();
        glyphs.add(T_GLYPH_FONT_SPECIFIC);
        glyphs.add(E_GLYPH_FONT_SPECIFIC);

        Assertions.assertEquals(glyphs, toAppend);
    }

    @Test
    public void appendGlyphsWithEmptyEncodingTest() throws IOException {
        PdfSimpleFont<FontProgram> fontToTest = new TestSimpleFont(FontEncoding.createEmptyFontEncoding());
        List<Glyph> toAppend = new ArrayList<>();
        int processed = fontToTest.appendGlyphs("e ete", 0, 4, toAppend);
        Assertions.assertEquals(3, processed);

        List<Glyph> glyphs = new ArrayList<>();
        glyphs.add(E_GLYPH_CUSTOM_MAPPED);
        glyphs.add(E_GLYPH_CUSTOM_MAPPED);

        Assertions.assertEquals(glyphs, toAppend);
    }

    @Test
    public void appendAnyGlyphWithSpecificEncodingTest() throws IOException {
        PdfSimpleFont<FontProgram> fontToTest = new TestSimpleFont(FontEncoding.createFontSpecificEncoding());
        List<Glyph> toAppend = new ArrayList<>();
        int processed = fontToTest.appendAnyGlyph("te", 0, toAppend);
        Assertions.assertEquals(1, processed);

        List<Glyph> glyphs = new ArrayList<>();
        glyphs.add(T_GLYPH_FONT_SPECIFIC);

        Assertions.assertEquals(glyphs, toAppend);
    }

    @Test
    public void appendAnyGlyphWithEmptyEncodingTest() throws IOException {
        PdfSimpleFont<FontProgram> fontToTest = new TestSimpleFont(FontEncoding.createEmptyFontEncoding());
        List<Glyph> toAppend = new ArrayList<>();
        int processed = fontToTest.appendAnyGlyph("e ete", 0, toAppend);
        Assertions.assertEquals(1, processed);

        List<Glyph> glyphs = new ArrayList<>();
        glyphs.add(E_GLYPH_CUSTOM_MAPPED);

        Assertions.assertEquals(glyphs, toAppend);
    }

    @Test
    public void convertGlyphLineToBytesWithSpecificEncodingTest() throws IOException {
        PdfSimpleFont<FontProgram> fontToTest = new TestSimpleFont(FontEncoding.createFontSpecificEncoding());

        List<Glyph> glyphs = new ArrayList<>();
        glyphs.add(T_GLYPH_FONT_SPECIFIC);
        glyphs.add(E_GLYPH_FONT_SPECIFIC);
        GlyphLine glyphLine = new GlyphLine(glyphs, 0, 2);

        byte[] bytes = fontToTest.convertToBytes(glyphLine);

        Assertions.assertArrayEquals(new byte[]{T_CODE, E_CODE}, bytes);
    }

    @Test
    public void convertGlyphLineToBytesWithEmptyEncodingTest() throws IOException {
        PdfSimpleFont<FontProgram> fontToTest = new TestSimpleFont(FontEncoding.createEmptyFontEncoding());

        List<Glyph> glyphs = new ArrayList<>();
        glyphs.add(T_GLYPH_FONT_SPECIFIC);
        glyphs.add(E_GLYPH_FONT_SPECIFIC);
        GlyphLine glyphLine = new GlyphLine(glyphs, 0, 2);

        byte[] bytes = fontToTest.convertToBytes(glyphLine);

        Assertions.assertArrayEquals(new byte[0], bytes);
    }

    @Test
    public void convertToBytesWithNullEntry() throws IOException {
        PdfSimpleFont<FontProgram> fontToTest = new TestSimpleFont(FontEncoding.createEmptyFontEncoding());

        byte[] bytes = fontToTest.convertToBytes((GlyphLine) null);

        Assertions.assertArrayEquals(new byte[0], bytes);
    }

    @Test
    public void convertGlyphToBytesWithSpecificEncodingTest() throws IOException {
        PdfSimpleFont<FontProgram> fontToTest = new TestSimpleFont(FontEncoding.createFontSpecificEncoding());

        byte[] bytes = fontToTest.convertToBytes(E_GLYPH_FONT_SPECIFIC);

        Assertions.assertArrayEquals(new byte[]{E_CODE}, bytes);
    }

    @Test
    public void convertGlyphToBytesWithCustomEncodingTest() throws IOException {
        FontEncoding emptyFontEncoding = FontEncoding.createEmptyFontEncoding();
        emptyFontEncoding.addSymbol(E_CUSTOM_CODE, E_CODE);
        PdfSimpleFont<FontProgram> fontToTest = new TestSimpleFont(emptyFontEncoding);

        byte[] bytes = fontToTest.convertToBytes(E_GLYPH_FONT_SPECIFIC);

        Assertions.assertArrayEquals(new byte[]{E_CUSTOM_CODE}, bytes);
    }

    @Test
    public void convertGlyphToBytesWithEmptyEncodingTest() throws IOException {
        PdfSimpleFont<FontProgram> fontToTest = new TestSimpleFont(FontEncoding.createEmptyFontEncoding());

        byte[] bytes = fontToTest.convertToBytes(E_GLYPH_FONT_SPECIFIC);

        Assertions.assertArrayEquals(new byte[0], bytes);
    }

    @Test
    public void writeTextGlyphLineWithSpecificEncodingTest() throws IOException {
        PdfSimpleFont<FontProgram> fontToTest = new TestSimpleFont(FontEncoding.createFontSpecificEncoding());

        List<Glyph> glyphs = new ArrayList<>();
        glyphs.add(T_GLYPH_FONT_SPECIFIC);
        glyphs.add(E_GLYPH_FONT_SPECIFIC);
        GlyphLine glyphLine = new GlyphLine(glyphs, 0, 2);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (PdfOutputStream pos = new PdfOutputStream(bos)) {
            fontToTest.writeText(glyphLine, 0, 1, pos);
        }

        Assertions.assertArrayEquals(new byte[]{OPEN_BRACKET_CODE, T_CODE, E_CODE, CLOSE_BRACKET_CODE}, bos.toByteArray());
    }

    @Test
    public void writeTextGlyphLineWithCustomEncodingTest() throws IOException {
        FontEncoding fontEncoding = FontEncoding.createEmptyFontEncoding();
        fontEncoding.addSymbol(E_CUSTOM_CODE, E_CODE);
        PdfSimpleFont<FontProgram> fontToTest = new TestSimpleFont(fontEncoding);

        List<Glyph> glyphs = new ArrayList<>();
        glyphs.add(E_GLYPH_FONT_SPECIFIC);
        glyphs.add(T_GLYPH_FONT_SPECIFIC);
        GlyphLine glyphLine = new GlyphLine(glyphs, 0, 2);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (PdfOutputStream pos = new PdfOutputStream(bos)) {
            fontToTest.writeText(glyphLine, 0, 1, pos);
        }

        Assertions.assertArrayEquals(new byte[]{OPEN_BRACKET_CODE, E_CUSTOM_CODE, CLOSE_BRACKET_CODE}, bos.toByteArray());
    }

    @Test
    public void writeTextGlyphLineWithEmptyEncodingTest() throws IOException {
        PdfSimpleFont<FontProgram> fontToTest = new TestSimpleFont(FontEncoding.createEmptyFontEncoding());

        List<Glyph> glyphs = new ArrayList<>();
        glyphs.add(E_GLYPH_FONT_SPECIFIC);
        glyphs.add(T_GLYPH_FONT_SPECIFIC);
        GlyphLine glyphLine = new GlyphLine(glyphs, 0, 2);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (PdfOutputStream pos = new PdfOutputStream(bos)) {
            fontToTest.writeText(glyphLine, 0, 1, pos);
        }

        Assertions.assertArrayEquals(new byte[]{OPEN_BRACKET_CODE, CLOSE_BRACKET_CODE}, bos.toByteArray());
    }

    private static class TestSimpleFont extends PdfSimpleFont<FontProgram> {

        public TestSimpleFont(FontEncoding fontEncoding) throws IOException {
            this.fontEncoding = fontEncoding;
            setFontProgram(FontProgramFactory.createFont(StandardFonts.HELVETICA));
        }

        @Override
        public Glyph getGlyph(int unicode) {
            if (unicode == E_CODE) {
                return E_GLYPH_CUSTOM_MAPPED;
            }
            return null;
        }

        @Override
        protected void addFontStream(PdfDictionary fontDescriptor) {

        }
    }

}
